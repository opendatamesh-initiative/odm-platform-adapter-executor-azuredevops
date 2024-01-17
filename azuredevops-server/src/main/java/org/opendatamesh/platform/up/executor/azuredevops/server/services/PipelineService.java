package org.opendatamesh.platform.up.executor.azuredevops.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.up.executor.api.resources.ExecutorApiStandardErrors;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.opendatamesh.platform.up.executor.azuredevops.api.clients.AzureDevOpsClient;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunResource;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunState;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.database.entities.PipelineRun;
import org.opendatamesh.platform.up.executor.azuredevops.server.database.repositories.PipelineRunRepository;
import org.opendatamesh.platform.up.executor.azuredevops.server.mappers.PipelineMapper;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.TemplateResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PipelineService {

    @Autowired
    protected PipelineMapper pipelineMapper;

    @Autowired
    protected AzureDevOpsClient azureDevOpsClient;

    @Autowired
    protected ParameterService parameterService;

    @Autowired
    protected PipelineRunRepository pipelineRunRepository;

    @Value("${polling.retries}")
    private Integer pollingNumRetries;

    @Value("${polling.interval}")
    private Integer pollingIntervalSeconds;

    private static final Logger logger = LoggerFactory.getLogger(PipelineService.class);

    public AzureRunResource runPipeline(
            ConfigurationsResource configurationsResource, TemplateResource templateResource, String callbackRef, Long taskId
    ) {

        configurationsResource = addParamsFromContext(configurationsResource);

        PipelineResource pipelineResource = pipelineMapper.toAzurePipelineResource(
                configurationsResource, templateResource, callbackRef
        );

        if(
                templateResource.getOrganization() == null ||
                templateResource.getProject() == null ||
                templateResource.getPipelineId() == null
        )
            throw new UnprocessableEntityException(
                    ExecutorApiStandardErrors.SC422_05_TASK_IS_INVALID,
                    "Impossible to send request to Azure DevOps: "
                            + "you must specify all parameters in template (organization, project and pipelineId)"
            );

        logger.info("Calling AzureDevOps to run the pipeline ...");

        ResponseEntity<AzureRunResource> azureResponse = azureDevOpsClient.runPipeline(
                pipelineResource,
                templateResource.getOrganization(),
                templateResource.getProject(),
                templateResource.getPipelineId()
        );
        AzureRunResource azureResponseBody = azureResponse.getBody();

        if(!azureResponse.getStatusCode().is2xxSuccessful()){

            switch (azureResponse.getStatusCode()) {
                case UNAUTHORIZED:
                    throw new InternalServerException(
                            ExecutorApiStandardErrors.SC401_01_EXECUTOR_UNATHORIZED,
                            "Missing credentials - " + azureResponseBody
                    );
                case FORBIDDEN:
                    throw new InternalServerException(
                            ExecutorApiStandardErrors.SC403_01_EXECUTOR_FORBIDDEN,
                            "User does not have the permission to run the pipeline - " + azureResponseBody
                    );
                default:
                    throw new InternalServerException(
                            ExecutorApiStandardErrors.SC500_50_EXECUTOR_SERVICE_ERROR,
                            "Azure DevOps responded with an error: " + azureResponseBody
                    );
            }

        } else {

            try {

                PipelineRun pipelineRun = new PipelineRun();
                pipelineRun.setTaskId(taskId);
                pipelineRun.setRunId(azureResponseBody.getRunId());
                pipelineRun.setPipelineId(templateResource.getPipelineId());
                pipelineRun.setOrganization(templateResource.getOrganization());
                pipelineRun.setProject(templateResource.getProject());
                pipelineRun.setPipelineName(azureResponseBody.getName());
                pipelineRun.setResult(azureResponseBody.getResult());
                pipelineRun.setVariables(azureResponseBody.getVariables());
                pipelineRun.setTemplateParameters(azureResponseBody.getTemplateParameters());
                pipelineRun.setCreatedAt(azureResponseBody.getCreatedDate());
                pipelineRun.setStatus(azureResponseBody.getState());
                pipelineRun.setFinishedAt(azureResponseBody.getFinishedDate());
                pipelineRunRepository.saveAndFlush(pipelineRun);

            } catch (Exception e) {
                logger.error("Error creating PipelineRun entry: " + e.getMessage());
            }

            logger.info("Pipeline run posted successfully");

            return azureResponseBody;

        }

    }

    @Async
    public CompletableFuture<TaskStatus> getPipelineRunStatus(Long taskId) {

        Optional<PipelineRun> pipelineRunOptional = pipelineRunRepository.findById(taskId);

        if(pipelineRunOptional.isEmpty()){
            throw new NotFoundException(
                    ExecutorApiStandardErrors.SC404_01_PIPELINE_RUN_NOT_FOUND,
                    "Pipeline run with id [" + taskId + "] does not exist");
        }

        PipelineRun pipelineRun = pipelineRunOptional.get();

        int counter = 0;
        ResponseEntity<AzureRunResource> azureRunResponse = null;

        while (counter < pollingNumRetries) {

            azureRunResponse = azureDevOpsClient.getAzureRun(
                    pipelineRun.getOrganization(),
                    pipelineRun.getProject(),
                    pipelineRun.getPipelineId(),
                    pipelineRun.getRunId()
            );

            if(
                    azureRunResponse.getStatusCode().is2xxSuccessful() &&
                            azureRunResponse.getBody().getState().equals(AzureRunState.completed)
            )
                break;

            try {
                Thread.sleep(pollingIntervalSeconds * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Polling thread error: " + e.getMessage().toString());
            }

            counter++;

        }

        AzureRunResource azureResponseBody = azureRunResponse.getBody();

        if(!azureRunResponse.getStatusCode().is2xxSuccessful()){

            switch (azureRunResponse.getStatusCode()) {
                case UNAUTHORIZED:
                    throw new InternalServerException(
                            ExecutorApiStandardErrors.SC401_01_EXECUTOR_UNATHORIZED,
                            "Missing credentials - " + azureResponseBody
                    );
                case FORBIDDEN:
                    throw new InternalServerException(
                            ExecutorApiStandardErrors.SC403_01_EXECUTOR_FORBIDDEN,
                            "User does not have the permission to get the run infos - " + azureResponseBody
                    );
                default:
                    throw new InternalServerException(
                            ExecutorApiStandardErrors.SC500_50_EXECUTOR_SERVICE_ERROR,
                            "Azure DevOps responded with an error: " + azureResponseBody
                    );
            }

        } else {

            pipelineRun.setResult(azureResponseBody.getResult());
            pipelineRun.setVariables(azureResponseBody.getVariables());
            pipelineRun.setStatus(azureResponseBody.getState());
            pipelineRun.setFinishedAt(azureResponseBody.getFinishedDate());

            pipelineRun = pipelineRunRepository.saveAndFlush(pipelineRun);

            switch (pipelineRun.getResult()) {
                case succeeded:
                    return CompletableFuture.completedFuture(TaskStatus.PROCESSED);
                case failed:
                    return CompletableFuture.completedFuture(TaskStatus.FAILED);
                case canceled:
                    return CompletableFuture.completedFuture(TaskStatus.ABORTED);
                default:
                    return CompletableFuture.completedFuture(null);
            }

        }

    }

    private ConfigurationsResource addParamsFromContext(ConfigurationsResource configurationsResource) {

        if(configurationsResource.getContext() != null && configurationsResource.getParams() != null) {
            Map<String, String> params = parameterService.extractParamsFromContext(
                    configurationsResource.getParams(),
                    configurationsResource.getContext()
            );
            configurationsResource.setParams(params);
        }

        configurationsResource.setContext(null);

        return configurationsResource;
    }

}

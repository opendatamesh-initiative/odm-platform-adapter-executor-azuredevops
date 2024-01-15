package org.opendatamesh.platform.up.executor.azuredevops.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.up.executor.api.resources.ExecutorApiStandardErrors;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.opendatamesh.platform.up.executor.azuredevops.api.clients.AzureDevOpsClient;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunResource;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunState;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineResource;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineRunResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.database.entities.PipelineRun;
import org.opendatamesh.platform.up.executor.azuredevops.server.database.mappers.PipelineRunMapper;
import org.opendatamesh.platform.up.executor.azuredevops.server.database.repositories.PipelineRunRepository;
import org.opendatamesh.platform.up.executor.azuredevops.server.mappers.PipelineMapper;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.TemplateResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PipelineService {

    @Autowired
    protected PipelineMapper pipelineMapper;

    @Autowired
    protected AzureDevOpsClient azureDevOpsClient;

    @Autowired
    protected ParameterService parameterService;

    @Autowired
    protected PipelineRunMapper pipelineRunMapper;

    @Autowired
    protected PipelineRunRepository pipelineRunRepository;
    ObjectMapper mapper = new ObjectMapper();

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

        try {
            PipelineRun pipelineRun = new PipelineRun();
            pipelineRun.setTaskId(taskId);
            pipelineRun.setRunId(azureResponseBody.getRunId());
            pipelineRun.setPipelineId(templateResource.getPipelineId());
            pipelineRun.setOrganization(templateResource.getOrganization());
            pipelineRun.setProject(templateResource.getProject());
            pipelineRun.setResult(azureResponseBody.getResult());
            pipelineRun.setVariables(azureResponseBody.getVariables());
            pipelineRun.setCreatedAt(azureResponseBody.getCreatedDate());
            pipelineRun.setStatus(azureResponseBody.getState());
            pipelineRun.setFinishedAt(azureResponseBody.getFinishedDate());
            pipelineRunRepository.saveAndFlush(pipelineRun);
        } catch (Exception e) {
            logger.error("Error creating PipelineRun entry: " + e.getMessage());
        }

        if(!azureResponse.getStatusCode().is2xxSuccessful()){
            throw new InternalServerException(
                    ExecutorApiStandardErrors.SC500_50_EXECUTOR_SERVICE_ERROR,
                    "Azure DevOps responded with an internal server error: " + azureResponseBody
            );
        } else {
            logger.info("Pipeline run posted successfully");
            return azureResponseBody;
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

    public TaskStatus getPipelineRunStatus(Long taskId) {

        Optional<PipelineRun> pipelineRunOptional = pipelineRunRepository.findById(taskId);

        if(pipelineRunOptional.isEmpty()){
            throw new NotFoundException(
                    ExecutorApiStandardErrors.SC404_01_PIPELINE_RUN_NOT_FOUND,
                    "Pipeline run with id [" + taskId + "] does not exist");
        }

        PipelineRun pipelineRun = pipelineRunOptional.get();

        ResponseEntity<AzureRunResource>  azureRunResponse = azureDevOpsClient.getAzureRun(
                pipelineRun.getOrganization(),
                pipelineRun.getProject(),
                pipelineRun.getPipelineId(),
                pipelineRun.getRunId()
        );
        AzureRunResource azureResponseBody = azureRunResponse.getBody();

        pipelineRun.setResult(azureResponseBody.getResult());
        pipelineRun.setVariables(azureResponseBody.getVariables());
        pipelineRun.setStatus(azureResponseBody.getState());
        pipelineRun.setFinishedAt(azureResponseBody.getFinishedDate());

        pipelineRun = pipelineRunRepository.saveAndFlush(pipelineRun);

        PipelineRunResource pipelineRunResource = pipelineRunMapper.toResource(pipelineRun);

        return pipelineRunResource.getStatus();

    }

}

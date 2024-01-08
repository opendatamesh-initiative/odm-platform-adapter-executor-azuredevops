package org.opendatamesh.platform.up.executor.azuredevops.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.up.executor.api.resources.ExecutorApiStandardErrors;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.opendatamesh.platform.up.executor.azuredevops.api.clients.AzureDevOpsClient;
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

import java.util.HashMap;
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

    public String runPipeline(
            ConfigurationsResource configurationsResource, TemplateResource templateResource, String callbackRef
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



        ResponseEntity<String> azureResponse = azureDevOpsClient.runPipeline(
                pipelineResource,
                templateResource.getOrganization(),
                templateResource.getProject(),
                templateResource.getPipelineId()
        );
        String azureResponseBody = azureResponse.getBody();

        if(!azureResponse.getStatusCode().is2xxSuccessful()){
            PipelineRun pipelineRun = new PipelineRun(TaskStatus.FAILED);
            pipelineRunRepository.saveAndFlush(pipelineRun);
            throw new InternalServerException(
                    ExecutorApiStandardErrors.SC502_50_REGISTRY_SERVICE_ERROR,
                    "Azure DevOps responded with an internal server error: " + azureResponseBody
            );
        } else {
            logger.info("Pipeline run posted successfully");

            TypeFactory factory = TypeFactory.defaultInstance();
            MapType type = factory.constructMapType(HashMap.class, String.class, String.class);
            Map<String, String> azurePipelineRun = null;
            try {
                azurePipelineRun = mapper.readValue(azureResponseBody, type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            PipelineRun pipelineRun = new PipelineRun(Long.getLong(azurePipelineRun.get("id")), TaskStatus.PROCESSING);
            pipelineRunRepository.saveAndFlush(pipelineRun);

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

    private PipelineRunResource getPipelineRunStatus(String runId) {


        Optional<PipelineRun> pipelineRun = pipelineRunRepository.findById(runId);
        if(pipelineRun.isEmpty()){
            throw new NotFoundException(
                    ExecutorApiStandardErrors.SC404_01_PIPELINE_RUN_NOT_FOUND,
                    "Pipeline run with id [" + runId + "] does not exist");
        }

        PipelineRunResource pipelineRunResource = pipelineRunMapper.toResource(pipelineRun.get());
        return pipelineRunResource;
    }

}

package org.opendatamesh.platform.up.executor.azuredevops.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.up.executor.api.controllers.AbstractExecutorController;
import org.opendatamesh.platform.up.executor.api.resources.ExecutorApiStandardErrors;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineRunResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.TemplateResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.services.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PipelineController extends AbstractExecutorController {

    @Autowired
    private PipelineService pipelineService;

    @Override
    public TaskResource createTask(TaskResource task) {
        ObjectMapper objectMapper = new ObjectMapper();
        TemplateResource template;
        ConfigurationsResource configurations;

        if(task.getId() == null)
            throw new UnprocessableEntityException(
                    ExecutorApiStandardErrors.SC422_05_TASK_IS_INVALID,
                    "Task Id isn't specified in the task");

        if(task.getConfigurations() == null)
            throw new UnprocessableEntityException(
                    ExecutorApiStandardErrors.SC422_05_TASK_IS_INVALID,
                    "Configuration isn't specified in the task");
        if(task.getTemplate() == null)
            throw new UnprocessableEntityException(
                    ExecutorApiStandardErrors.SC422_05_TASK_IS_INVALID,
                    "Template isn't specified in the task");
        try {
            // TODO handle null values
            template = objectMapper.readValue(task.getTemplate(), TemplateResource.class);
            configurations = objectMapper.readValue(task.getConfigurations(), ConfigurationsResource.class);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(ExecutorApiStandardErrors.SC500_50_REGISTRY_SERVICE_ERROR,
                    "Task service couldn't read template or configuration information. Please check the format of the object");
        }

        String callbackRef = task.getCallbackRef();

        pipelineService.runPipeline(configurations, template, callbackRef, task.getId());

        // TODO: if needed, update task resource after the pipeline completion

        return task;
    }

    @Override
    public TaskResource readTask(TaskResource task) {
        throw new UnsupportedOperationException("Unimplemented method 'readTask'");
    }

    @Operation(
            summary = "Get the task updated version",
            description = "Get the an updated version of the given task"
    )
    @GetMapping({"/{taskId}"})
    @ApiResponses({@ApiResponse(
            responseCode = "200",
            description = "The requested task with updated state from Azure",
            content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = PipelineRunResource.class
                    )
            )}
    ),@ApiResponse(
            responseCode = "404",
            description = "[Conflict](https://www.rfc-editor.org/rfc/rfc9110.html#name-409-conflict)"
                    + "\r\n - Error Code 40401 - Task is already started",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
    )})
    public PipelineRunResource getPipelineRunStatus(@PathVariable(value = "taskId") Long taskId) {

        return pipelineService.getPipelineRunStatus(taskId);
    }

}

package org.opendatamesh.platform.up.executor.azuredevops.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.up.executor.api.controllers.AbstractExecutorController;
import org.opendatamesh.platform.up.executor.api.resources.ExecutorApiStandardErrors;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.TemplateResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.services.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new InternalServerException(ExecutorApiStandardErrors.SC500_50_EXECUTOR_SERVICE_ERROR,
                    "Task service couldn't read template or configuration information. Please check the format of the object");
        }

        String callbackRef = task.getCallbackRef();

        pipelineService.runPipeline(configurations, template, callbackRef, task.getId());

        return task;
    }

    @Override
    public TaskResource readTask(TaskResource task) {
        throw new UnsupportedOperationException("Unimplemented method 'readTask'");
    }

    @Override
    public TaskStatus readTaskStatus(Long taskId) {
        return pipelineService.getPipelineRunStatus(taskId);
    }

}

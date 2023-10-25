package org.opendatamesh.platform.up.executor.azuredevops.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.executor.api.controllers.AbstractExecutorController;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.azuredevops.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.resources.odm.TemplateResource;
import org.opendatamesh.platform.up.executor.azuredevops.services.PipelineService;
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

        try {
            // TODO handle null values
            template = objectMapper.readValue(task.getTemplate(), TemplateResource.class);
            configurations = objectMapper.readValue(task.getConfigurations(), ConfigurationsResource.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String callbackRef = task.getCallbackRef();

        pipelineService.runPipeline(configurations, template, callbackRef);

        // TODO: if needed, update task resource after the pipeline completion

        return task;
    }

    @Override
    public TaskResource readTask(TaskResource task) {
        throw new UnsupportedOperationException("Unimplemented method 'readTask'");
    }

}

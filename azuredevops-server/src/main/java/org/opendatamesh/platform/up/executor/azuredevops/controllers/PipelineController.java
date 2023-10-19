package org.opendatamesh.platform.up.executor.azuredevops.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.executor.api.controllers.AbstractExecutorController;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.azuredevops.resources.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.resources.TemplateResource;
import org.opendatamesh.platform.up.executor.azuredevops.services.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

        String organization = template.getOrganization();
        String project = template.getProject();
        String pipelineId = template.getPipelineId();
        String branch = template.getBranch();

        List<String> stagesToSkip = configurations.getStagesToSkip();
        Map<String, String> params = configurations.getParams();

        String callbackRef = task.getCallbackRef();

        String result = pipelineService.runPipeline(organization, project, pipelineId, branch, callbackRef, stagesToSkip, params);

        // task.setResults(result);

        return task;
    }

    @Override
    public TaskResource readTask(TaskResource task) {
        throw new UnsupportedOperationException("Unimplemented method 'readTask'");
    }

}

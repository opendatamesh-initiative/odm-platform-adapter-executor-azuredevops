package org.opendatamesh.platform.up.executor.azuredevops.services;

import org.opendatamesh.platform.up.executor.azuredevops.components.OAuthTokenManager;
import org.opendatamesh.platform.up.executor.azuredevops.mappers.PipelineMapper;
import org.opendatamesh.platform.up.executor.azuredevops.resources.azure.PipelineResource;
import org.opendatamesh.platform.up.executor.azuredevops.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.resources.odm.TemplateResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PipelineService {

    @Autowired
    private OAuthTokenManager oAuthTokenManager;

    @Autowired
    protected PipelineMapper pipelineMapper;


    private PipelineResource buildRunPipelineRequestBody(ConfigurationsResource configurationsResource, TemplateResource templateResource, String callbackRef) {
        PipelineResource pipelineResource = pipelineMapper.toAzurePipelineResource(configurationsResource, templateResource, callbackRef);
        return pipelineResource;
    }

    private String buildRunPipelineUri(String organization, String project, String pipelineId) {
        String pipelineUri = "https://dev.azure.com/%s/%s/_apis/pipelines/%s/runs?api-version=7.0";
        return String.format(pipelineUri, organization, project, pipelineId);
    }


    public String runPipeline(ConfigurationsResource configurationsResource, TemplateResource templateResource, String callbackRef) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        PipelineResource requestBody = buildRunPipelineRequestBody(configurationsResource, templateResource, callbackRef);
        HttpEntity<PipelineResource> entity = new HttpEntity<>(requestBody, headers);

        String pipelineUri = buildRunPipelineUri(templateResource.getOrganization(), templateResource.getProject(), templateResource.getPipelineId());
        ResponseEntity<String> response = restTemplate.postForEntity(pipelineUri, entity, String.class);

        return response.getBody();
    }
}

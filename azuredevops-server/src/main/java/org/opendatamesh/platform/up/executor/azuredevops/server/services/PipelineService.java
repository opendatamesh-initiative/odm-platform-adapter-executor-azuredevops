package org.opendatamesh.platform.up.executor.azuredevops.server.services;

import org.opendatamesh.platform.up.executor.azuredevops.api.clients.AzureDevOpsClient;
import org.opendatamesh.platform.up.executor.azuredevops.api.components.OAuthTokenManager;
import org.opendatamesh.platform.up.executor.azuredevops.server.mappers.PipelineMapper;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.TemplateResource;
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
    protected PipelineMapper pipelineMapper;

    @Autowired
    protected AzureDevOpsClient azureDevOpsClient;



    public String runPipeline(ConfigurationsResource configurationsResource, TemplateResource templateResource, String callbackRef) {

        PipelineResource pipelineResource = pipelineMapper.toAzurePipelineResource(configurationsResource,templateResource, callbackRef);

        return azureDevOpsClient.runPipeline(pipelineResource, templateResource.getOrganization(), templateResource.getProject(), templateResource.getPipelineId());
    }
}

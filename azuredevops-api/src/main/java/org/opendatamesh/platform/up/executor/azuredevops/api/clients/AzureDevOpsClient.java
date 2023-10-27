package org.opendatamesh.platform.up.executor.azuredevops.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.executor.api.resources.ExecutorApiStandardErrors;
import org.opendatamesh.platform.up.executor.azuredevops.api.components.OAuthTokenManager;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AzureDevOpsClient extends ODMClient {

    private static final Logger logger = LoggerFactory.getLogger(AzureDevOpsClient.class);

    public AzureDevOpsClient(@Value("${spring.security.oauth2.client.provider.azure-devops.basic-uri}") String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    @Autowired
    private OAuthTokenManager oAuthTokenManager;
    private String buildRunPipelineUri(String organization, String project, String pipelineId) {
        String pipelineUri = "/%s/%s/_apis/pipelines/%s/runs?api-version=7.0";
        return String.format(pipelineUri, organization, project, pipelineId);
    }

    public String runPipeline(PipelineResource pipelineResource, String organization, String project, String pipelineId){
        if(organization == null || project == null || pipelineId == null)
            throw new UnprocessableEntityException(
                    ExecutorApiStandardErrors.SC422_05_TASK_IS_INVALID,
                    "Impossible to send request to Azure DevOps: you must specify all parameters in template (organization, project and pipelineId)");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        String pipelineUri = buildRunPipelineUri(organization, project, pipelineId);
        HttpEntity<PipelineResource> entity = new HttpEntity<>(pipelineResource, headers);

        ResponseEntity<String> response = rest.postForEntity(apiUrlFromString(pipelineUri), entity, String.class);

        if(response.getStatusCode().is5xxServerError()){
            throw new InternalServerException(
                    ExecutorApiStandardErrors.SC502_50_REGISTRY_SERVICE_ERROR,
                    "Azure DevOps responded with an internal server error: " + response.getBody());
        }

        logger.info("Pipeline run ended successfully");
        return response.getBody();
    }
}

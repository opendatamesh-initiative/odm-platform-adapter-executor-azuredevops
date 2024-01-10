package org.opendatamesh.platform.up.executor.azuredevops.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AzureDevOpsClient extends ODMClient {
    private OAuthTokenManager oAuthTokenManager;

    public AzureDevOpsClient(
            @Value("${spring.security.oauth2.client.provider.azure-devops.basic-uri}") String serverAddress,
            @Value("${spring.security.oauth2.client.provider.azure-devops.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.registration.azure-devops.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.azure-devops.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.client.registration.azure-devops.scope}") String scope,
            @Value("${spring.security.oauth2.client.registration.azure-devops.authorization-grant-type}") String authorizationGrantType
    ) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
        this.oAuthTokenManager = new OAuthTokenManager(
                "azure-devops",
                "odm-azure-devops-executor-principal",
                tokenUri,
                clientId,
                clientSecret,
                scope,
                authorizationGrantType
        );
    }

    private String buildRunPipelineUri(String organization, String project, String pipelineId) {
        String pipelineUri = "/%s/%s/_apis/pipelines/%s/runs?api-version=7.0";
        return String.format(pipelineUri, organization, project, pipelineId);
    }

    public ResponseEntity<String> runPipeline(
            PipelineResource pipelineResource, String organization, String project, String pipelineId
    ){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        String pipelineUri = buildRunPipelineUri(organization, project, pipelineId);
        HttpEntity<PipelineResource> entity = new HttpEntity<>(pipelineResource, headers);

        ResponseEntity<String> response = rest.postForEntity(apiUrlFromString(pipelineUri), entity, String.class);

        return response;

    }
}

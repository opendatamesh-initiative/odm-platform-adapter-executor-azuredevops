package org.opendatamesh.platform.up.executor.azuredevops.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunResource;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    public ResponseEntity<AzureRunResource> runPipeline(
            PipelineResource pipelineResource, String organization, String project, String pipelineId
    ){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        HttpEntity<PipelineResource> entity = new HttpEntity<>(pipelineResource, headers);

        ResponseEntity<AzureRunResource> response = rest.postForEntity(
                apiUrl(AzureDevOpsAPIRoutes.AZURE_DEVOPS_PIPELINE),
                entity,
                AzureRunResource.class,
                organization,
                project,
                pipelineId
        );

        return response;

    }

    public ResponseEntity<AzureRunResource> getAzureRun(String organization, String project, String pipelineId, Long runId){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        HttpEntity<PipelineResource> entity = new HttpEntity<>(headers);

        ResponseEntity<AzureRunResource> response = rest.exchange(
                apiUrl(AzureDevOpsAPIRoutes.AZURE_DEVOPS_RUN),
                HttpMethod.GET,
                entity,
                AzureRunResource.class,
                organization,
                project,
                pipelineId,
                runId
        );

        return response;

    }

}

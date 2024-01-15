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

    private String buildRunPipelineUri(String organization, String project, String pipelineId) {

        String pipelineUri = "/%s/%s/_apis/pipelines/%s/runs?api-version=7.0";
        return String.format(pipelineUri, organization, project, pipelineId);

    }

    private String buildGetAzureRunUri(String organization, String project, String pipelineId, Long runId) {
        String pipelineUri = "/%s/%s/_apis/pipelines/%s/runs/%s?api-version=7.0";
        return String.format(pipelineUri, organization, project, pipelineId, runId);
    }

    public ResponseEntity<AzureRunResource> runPipeline(
            PipelineResource pipelineResource, String organization, String project, String pipelineId
    ){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        String pipelineUri = buildRunPipelineUri(organization, project, pipelineId);
        HttpEntity<PipelineResource> entity = new HttpEntity<>(pipelineResource, headers);

        ResponseEntity<AzureRunResource> response = rest.postForEntity(apiUrlFromString(pipelineUri), entity, AzureRunResource.class);

        return response;

    }

    public ResponseEntity<String> getAzureRun(String organization, String project, String pipelineId, Long runId){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        String pipelineUri = buildGetAzureRunUri(organization, project, pipelineId, runId);
        HttpEntity<PipelineResource> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = rest.exchange(apiUrlFromString(pipelineUri), HttpMethod.GET, entity, String.class);

        return response;

    }

}

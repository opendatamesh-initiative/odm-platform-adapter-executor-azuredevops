package org.opendatamesh.platform.up.executor.azuredevops.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.up.executor.azuredevops.components.OAuthTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PipelineService {

    @Autowired
    private OAuthTokenManager oAuthTokenManager;

    private String buildRunPipelineRequestBody(String branch, String callbackRef, List<String> stagesToSkip, Map<String, String> params) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonRoot = objectMapper.createObjectNode();

        // Resources
        ObjectNode resourcesNode = objectMapper.createObjectNode();
        ObjectNode repositoriesNode = objectMapper.createObjectNode();
        ObjectNode selfNode = objectMapper.createObjectNode();

        selfNode.put("refName", String.format("refs/heads/%s", branch));
        repositoriesNode.set("self", selfNode);
        resourcesNode.set("repositories", repositoriesNode);
        jsonRoot.set("resources", resourcesNode);

        // Template parameters
        ObjectNode templateParametersNode = objectMapper.createObjectNode();
        templateParametersNode.put("callbackRef", callbackRef);
        if(params != null) {
            params.forEach(templateParametersNode::put);
        }
        jsonRoot.set("templateParameters", templateParametersNode);

        // Stages to skip
        ArrayNode stagesToSkipArray = objectMapper.valueToTree(stagesToSkip);
        jsonRoot.set("stagesToSkip", stagesToSkipArray);

        return jsonRoot.toString();
    }

    private String buildRunPipelineUri(String organization, String project, String pipelineId) {
        String pipelineUri = "https://dev.azure.com/%s/%s/_apis/pipelines/%s/runs?api-version=7.0";
        return String.format(pipelineUri, organization, project, pipelineId);
    }

    public String runPipeline(String organization, String project, String pipelineId, String branch, String callbackRef, List<String> stagesToSkip, Map<String, String> params) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenManager.getToken());

        String requestBody = buildRunPipelineRequestBody(branch, callbackRef, stagesToSkip, params);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String pipelineUri = buildRunPipelineUri(organization, project, pipelineId);

        // TODO decide what to return
        return restTemplate.postForObject(pipelineUri, entity, String.class);
    }
}

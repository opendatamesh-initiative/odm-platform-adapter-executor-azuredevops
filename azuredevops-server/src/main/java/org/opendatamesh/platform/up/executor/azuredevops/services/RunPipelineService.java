package org.opendatamesh.platform.up.executor.azuredevops.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class RunPipelineService {

    public String getRunPipelineRequestBody(String branch) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode jsonRoot = objectMapper.createObjectNode();
        ObjectNode resourcesNode = objectMapper.createObjectNode();
        ObjectNode repositoriesNode = objectMapper.createObjectNode();
        ObjectNode selfNode = objectMapper.createObjectNode();

        selfNode.put("refName", String.format("refs/heads/%s", branch));
        repositoriesNode.set("self", selfNode);
        resourcesNode.set("repositories", repositoriesNode);
        jsonRoot.set("resources", resourcesNode);

        return jsonRoot.toString();
    }
}

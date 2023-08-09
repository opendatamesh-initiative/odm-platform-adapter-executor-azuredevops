package org.opendatamesh.platform.up.executor.azuredevops.controllers;

import org.opendatamesh.platform.up.executor.azuredevops.services.OAuthTokenService;
import org.opendatamesh.platform.up.executor.azuredevops.services.RunPipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(
        value = "/pipelines",
        produces = { "application/json" }
)
@Validated
public class PipelineController {

    @Autowired
    OAuthTokenService oAuthTokenService;

    @Autowired
    RunPipelineService runPipelineService;

    // TODO delete, this is only for tests
    @GetMapping("/get-runs")
    public String getRuns(
            @RequestParam(name = "pipelineUri", required = true) String pipelineUri
    ) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenService.getToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(pipelineUri, HttpMethod.GET, entity, String.class).getBody();
    }

    @PostMapping("/run")
    public String runPipeline(
            @RequestParam(name = "pipelineUri", required = true) String pipelineUri,
            @RequestParam(name = "branch", required = true) String branch
    ) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oAuthTokenService.getToken());

        String requestBody = runPipelineService.getRunPipelineRequestBody(branch);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // TODO decide what to return
        return restTemplate.postForObject(pipelineUri, entity, String.class);
    }

}

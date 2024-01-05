package org.opendatamesh.platform.up.executor.azuredevops.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PipelineRunResource {

    @JsonProperty("taskId")
    @Schema(description = "Auto generated Task ID")
    private String taskId;

    @JsonProperty("runId")
    @Schema(description = "Id from Azure Dev Ops run", required = true)
    private String runId;

    @JsonProperty("status")
    @Schema(description = "Pipeline run status", required = true)
    private String status;

}
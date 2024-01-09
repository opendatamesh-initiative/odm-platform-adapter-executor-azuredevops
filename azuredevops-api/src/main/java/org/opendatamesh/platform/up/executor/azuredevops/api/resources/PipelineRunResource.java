package org.opendatamesh.platform.up.executor.azuredevops.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PipelineRunResource {

    @JsonProperty("taskId")
    @Schema(description = "Auto generated Task ID")
    private Long taskId;

    @JsonProperty("runId")
    @Schema(description = "Id from Azure Dev Ops run", required = true)
    private Long runId;

    @JsonProperty("status")
    @Schema(description = "Pipeline run status", required = true)
    private TaskStatus status;

    @JsonProperty("organization")
    @Schema(description = "Organization name", required = true)
    private String organization;

    @JsonProperty("project")
    @Schema(description = "Project name", required = true)
    private String project;

    @JsonProperty("pipelineId")
    @Schema(description = "Pipeline ID", required = true)
    private String pipelineId;

}
package org.opendatamesh.platform.up.executor.azuredevops.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PipelineRunResource {

    @JsonProperty("taskId")
    @Schema(description = "Task ID")
    private Long taskId;

    @JsonProperty("organization")
    @Schema(description = "Organization name", required = true)
    private String organization;

    @JsonProperty("project")
    @Schema(description = "Project name", required = true)
    private String project;

    @JsonProperty("pipelineId")
    @Schema(description = "Pipeline ID", required = true)
    private String pipelineId;

    @JsonProperty("runId")
    @Schema(description = "Id from Azure DevOps run", required = true)
    private Long runId;

    @JsonProperty("status")
    @Schema(description = "Pipeline from Azure DevOps run status", required = true)
    private TaskStatus status;

    @JsonProperty("result")
    @Schema(description = "Pipeline from Azure DevOps run results")
    private TaskStatus result;

    @JsonProperty("variables")
    @Schema(description = "Variables from Azure DevOps run")
    private Map<String, AzureVariable> variables;

    @JsonProperty("createdAt")
    @Schema(description = "Creation date of the run on Azure DevOps")
    private String createdAt;

    @JsonProperty("finishedAt")
    @Schema(description = "Finish date of the run on Azure DevOps")
    private String finishedAt;

}
package org.opendatamesh.platform.up.executor.azuredevops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureRunResource {
    @JsonProperty("id")
    Long runId;

    @JsonProperty("state")
    AzureRunState state;

    @JsonProperty("result")
    AzureRunResult result;

    @JsonProperty("variables")
    Map<String, AzureVariable> variables;

    @JsonProperty("createdDate")
    String createdDate;

    @JsonProperty("finishedDate")
    String finishedDate;

}

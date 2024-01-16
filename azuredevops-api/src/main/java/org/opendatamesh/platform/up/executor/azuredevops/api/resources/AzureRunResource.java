package org.opendatamesh.platform.up.executor.azuredevops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureRunResource {

    @JsonProperty("id")
    private Long runId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("state")
    private AzureRunState state;

    @JsonProperty("result")
    private AzureRunResult result;

    @JsonProperty("variables")
    private Map<String, AzureVariable> variables;

    @JsonProperty("templateParameters")
    private Map<String, String> templateParameters;

    @JsonProperty("createdDate")
    private String createdDate;

    @JsonProperty("finishedDate")
    private String finishedDate;

}

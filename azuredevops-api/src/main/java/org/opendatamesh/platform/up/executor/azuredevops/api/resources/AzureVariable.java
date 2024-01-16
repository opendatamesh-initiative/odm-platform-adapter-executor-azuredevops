package org.opendatamesh.platform.up.executor.azuredevops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureVariable {

    @JsonProperty("isSecret")
    private Boolean isSecret;

    @JsonProperty("value")
    private String value;

}

package org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationsResource {

    @JsonProperty("stagesToSkip")
    private List<String> stagesToSkip;

    @JsonProperty("params")
    private Map<String, String> params;

    @JsonProperty("context")
    private Map<String, Object> context;

}

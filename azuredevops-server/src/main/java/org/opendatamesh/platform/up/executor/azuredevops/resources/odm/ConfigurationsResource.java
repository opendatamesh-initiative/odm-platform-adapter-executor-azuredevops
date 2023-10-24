package org.opendatamesh.platform.up.executor.azuredevops.resources.odm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationsResource {

    private List<String> stagesToSkip;

    private Map<String, String> params;
}

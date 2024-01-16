package org.opendatamesh.platform.up.executor.azuredevops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PipelineResource {

    private ResourcesResource resources;

    private Map<String, String> templateParameters;

    private List<String> stagesToSkip;

}

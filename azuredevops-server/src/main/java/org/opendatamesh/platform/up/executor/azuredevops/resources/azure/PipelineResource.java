package org.opendatamesh.platform.up.executor.azuredevops.resources.azure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.opendatamesh.platform.up.executor.azuredevops.resources.azure.ResourcesResource;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PipelineResource {

    ResourcesResource resources;

    private Map<String, String> templateParameters;

    List<String> stagesToSkip;

}

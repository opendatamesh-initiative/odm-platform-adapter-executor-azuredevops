package org.opendatamesh.platform.up.executor.azuredevops.resources;

import lombok.Data;

import java.util.List;

@Data
public class ConfigurationsResource {

    private List<String> stagesToSkip;
}

package org.opendatamesh.platform.up.executor.azuredevops.resources;

import lombok.Data;

@Data
public class TemplateResource {

    private String organization;

    private String project;

    private String pipelineId;

    private String branch;
}

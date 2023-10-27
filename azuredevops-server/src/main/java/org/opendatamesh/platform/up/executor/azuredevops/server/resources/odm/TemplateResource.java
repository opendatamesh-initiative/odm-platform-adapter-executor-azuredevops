package org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm;

import lombok.Data;

@Data
public class TemplateResource {

    private String organization;

    private String project;

    private String pipelineId;

    private String branch;
}

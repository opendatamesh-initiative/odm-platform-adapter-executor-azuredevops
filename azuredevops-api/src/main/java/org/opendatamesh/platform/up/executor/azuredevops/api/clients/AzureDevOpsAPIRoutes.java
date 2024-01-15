package org.opendatamesh.platform.up.executor.azuredevops.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum AzureDevOpsAPIRoutes implements ODMApiRoutes {

    AZURE_DEVOPS_PIPELINE("/{organization}/{project}/_apis/pipelines/{pipelineId}/runs?api-version=7.0"),

    AZURE_DEVOPS_RUN("/{organization}/{project}/_apis/pipelines/{pipelineId}/runs/{runId}?api-version=7.0");

    private final String path;

    AzureDevOpsAPIRoutes(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.path;
    }

    @Override
    public String getPath() {
        return path;
    }

}

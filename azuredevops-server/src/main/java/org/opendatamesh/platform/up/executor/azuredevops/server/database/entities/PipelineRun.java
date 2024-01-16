package org.opendatamesh.platform.up.executor.azuredevops.server.database.entities;

import lombok.Data;
import org.opendatamesh.platform.core.dpds.utils.HashMapConverter;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunResult;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunState;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureVariable;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Data
@Entity(name = "PipelineRun")
@Table(name = "PIPELINE_RUNS", schema = "ODMEXECUTOR")
public class PipelineRun {

    @Id
    @Column(name="TASK_ID")
    protected Long taskId;

    @Column(name="RUN_ID")
    protected Long runId;

    @Column(name="ORGANIZATION")
    protected String organization;

    @Column(name="PROJECT")
    protected String project;

    @Column(name="PIPELINE_ID")
    protected String pipelineId;

    @Column(name="PIPELINE_NAME")
    protected String pipelineName;

    @Column(name="STATUS")
    @Enumerated(EnumType.STRING)
    protected AzureRunState status;

    @Column(name="RESULT")
    @Enumerated(EnumType.STRING)
    protected AzureRunResult result;

    @Column(name = "VARIABLES")
    @Convert(converter = HashMapConverter.class)
    protected Map<String, AzureVariable> variables;

    @Column(name = "TEMPLATE_PARAMETERS")
    @Convert(converter = HashMapConverter.class)
    protected Map<String, String> templateParameters;

    @Column(name = "CREATED_AT")
    protected String createdAt;

    @Column(name = "FINISHED_AT")
    protected String finishedAt;

}
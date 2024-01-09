package org.opendatamesh.platform.up.executor.azuredevops.server.database.entities;

import lombok.Data;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.springframework.data.convert.ThreeTenBackPortConverters;

import java.util.Date;

import javax.persistence.*;

@Data
@Entity(name = "PipelineRun")
@Table(name = "PIPELINE_RUNS", schema="ODMEXECUTOR")
public class PipelineRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TASKID")
    protected Long taskId;

    @Column(name="RUNID")
    protected Long runId;

    @Column(name="ORGANIZATION")
    protected String organization;

    @Column(name="PROJECT")
    protected String project;

    @Column(name="PIPELINEID")
    protected String pipelineId;

    @Column(name="STATUS")
    @Enumerated(EnumType.STRING)
    protected TaskStatus status;

    @Column(name="CREATED_AT")
    protected Date createdAt;

    @Column(name="UPDATED_AT")
    protected Date updatedAt;

    public PipelineRun(Long runId, String organization, String project, String pipelineId, TaskStatus status) {
        this.runId = runId;
        this.organization = organization;
        this.project = project;
        this.pipelineId = pipelineId;
        this.status = status;
    }

    public PipelineRun(Long runId, TaskStatus status){
        this.runId = runId;
        this.status = status;
    }

    public void saveStatusFromAzureState(String state){
        switch (state) {
            case "canceling":
                setStatus(TaskStatus.ABORTED);
                break;
            case "completed":
                setStatus(TaskStatus.PROCESSED);
                break;
            case "inProgress":
                setStatus(TaskStatus.PROCESSING);
                break;
            case "unknown":
                setStatus(TaskStatus.FAILED);
                break;
            default:
                setStatus(TaskStatus.FAILED);
                break;
        }
    }

    public PipelineRun(TaskStatus status){
        this.status = status;
    }

    public PipelineRun(){}

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
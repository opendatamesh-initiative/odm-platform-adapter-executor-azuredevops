package org.opendatamesh.platform.up.executor.azuredevops.server.database.entities;

import lombok.Data;

import java.util.Date;

import javax.persistence.*;

@Data
@Entity(name = "PipelineRun")
@Table(name = "PIPELINES_RUNS", schema="ODMEXECUTOR")
public class PipelineRun {
    @Id
    @Column(name="TASKID")
    Long taskId;

    @Column(name="RUNID")
    protected String runId;

    @Column(name="STATUS")
    protected String status;


    @Column(name="CREATED_AT")
    protected Date createdAt;

    @Column(name="UPDATED_AT")
    protected Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
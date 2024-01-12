package org.opendatamesh.platform.up.executor.azuredevops.server.database.repositories;

import org.opendatamesh.platform.up.executor.azuredevops.server.database.entities.PipelineRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface PipelineRunRepository extends JpaRepository<PipelineRun, Long>, JpaSpecificationExecutor<PipelineRun> {

}
package org.opendatamesh.platform.up.executor.azuredevops.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineRunResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.database.entities.PipelineRun;


@Mapper(componentModel = "spring")
public interface PipelineRunMapper {

    PipelineRun toEntity(PipelineRunResource resource);
    PipelineRunResource toResource(PipelineRun entity);

}

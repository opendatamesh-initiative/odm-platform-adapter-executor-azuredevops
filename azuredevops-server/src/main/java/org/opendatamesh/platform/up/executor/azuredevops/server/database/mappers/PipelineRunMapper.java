package org.opendatamesh.platform.up.executor.azuredevops.server.database.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunResult;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.AzureRunState;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineRunResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.database.entities.PipelineRun;


@Mapper(componentModel = "spring")
public interface PipelineRunMapper {

    @Named("statusEnum")
    default AzureRunState statusEnum(TaskStatus status) {
        switch (status) {
            case FAILED:
            case PROCESSED:
                return AzureRunState.completed;
            case PROCESSING:
                return AzureRunState.inProgress;
            default:
                return AzureRunState.unknown;
        }
    }

    @Named("statusEnum")
    default TaskStatus statusEnum(AzureRunState status) {
        switch (status) {
            case canceling:
                return TaskStatus.ABORTED;
            case completed:
                return TaskStatus.PROCESSED;
            case inProgress:
                return TaskStatus.PROCESSING;
            default:
                return null;
        }
    }

    @Named("resultEnum")
    default AzureRunResult resultEnum(TaskStatus result) {
        switch (result) {
            case ABORTED:
                return AzureRunResult.canceled;
            case FAILED:
                return AzureRunResult.failed;
            case PROCESSED:
                return AzureRunResult.succeeded;
            default:
                return AzureRunResult.unknown;
        }
    }

    @Named("resultEnum")
    default TaskStatus resultEnum(AzureRunResult result) {
        switch (result) {
            case canceled:
                return TaskStatus.ABORTED;
            case failed:
                return TaskStatus.FAILED;
            case succeeded:
                return TaskStatus.PROCESSED;
            default:
                return null;
        }
    }

    @Mappings({
            @Mapping(source = "status", target = "status", qualifiedByName = "statusEnum"),
            @Mapping(source = "result", target = "result", qualifiedByName = "resultEnum")
    })
    PipelineRun toEntity(PipelineRunResource resource);

    @Mappings({
            @Mapping(source = "status", target = "status", qualifiedByName = "statusEnum"),
            @Mapping(source = "result", target = "result", qualifiedByName = "resultEnum")
    })
    PipelineRunResource toResource(PipelineRun entity);

}

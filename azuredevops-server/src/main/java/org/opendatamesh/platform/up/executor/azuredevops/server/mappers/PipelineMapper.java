package org.opendatamesh.platform.up.executor.azuredevops.server.mappers;

import org.mapstruct.*;
import org.opendatamesh.platform.up.executor.azuredevops.api.resources.PipelineResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.ConfigurationsResource;
import org.opendatamesh.platform.up.executor.azuredevops.server.resources.odm.TemplateResource;

import java.util.HashMap;

@Mapper(componentModel = "spring")
public interface PipelineMapper {


    @Mapping(source = "configuration.stagesToSkip", target = "stagesToSkip")
    @Mapping(source = "configuration.params", target = "templateParameters")
    @Mapping(source = "template.branch", target = "resources.repositories.refName", qualifiedByName = "refNameFromBranch")
    public PipelineResource toAzurePipelineResource(ConfigurationsResource configuration, TemplateResource template, String callbackRef);

    @Named("refNameFromBranch")
    public static String refNameFromBranch(String branch) {
        return String.format("refs/heads/%s", branch);
    }
    @AfterMapping
    public static void addCallbackRef(String callbackRef, @MappingTarget PipelineResource target) {
        if(target.getTemplateParameters() == null) target.setTemplateParameters(new HashMap<>());
        target.getTemplateParameters().put("callbackRef", callbackRef);
    }

}

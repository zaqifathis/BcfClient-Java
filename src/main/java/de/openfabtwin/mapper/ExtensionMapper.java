package de.openfabtwin.mapper;

import de.openfabtwin.generated.model.ExtensionsGET;
import de.openfabtwin.domain.Extensions;

import static de.openfabtwin.mapper.MapperUtils.enumToStrings;

public class ExtensionMapper {

    private ExtensionMapper(){}

    public static Extensions toDomain(ExtensionsGET raw) {
        if (raw == null) return null;

        Extensions extension = new Extensions();
        extension.setTopicType(raw.getTopicType());
        extension.setTopicStatus(raw.getTopicStatus());
        extension.setTopicLabel(raw.getTopicLabel());
        extension.setSnippetType(raw.getSnippetType());
        extension.setPriority(raw.getPriority());
        extension.setUsers(raw.getUsers());
        extension.setStage(raw.getStage());

        extension.setProjectActions(enumToStrings(raw.getProjectActions()));
        extension.setTopicActions(enumToStrings(raw.getTopicActions()));
        extension.setCommentActions(enumToStrings(raw.getCommentActions()));

        return extension;
    }
}

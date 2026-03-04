package de.openfabtwin.mapper;

import de.openfabtwin.domain.Topic;
import de.openfabtwin.generated.model.TopicGET;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.openfabtwin.mapper.MapperUtils.enumToStrings;

public class TopicMapper {
    private TopicMapper() {}

    public static Topic toDomain(TopicGET raw) {
        if (raw == null) return null;

        Topic topic = new Topic();

        topic.setGuid(raw.getGuid());
        topic.setTitle(raw.getTitle());
        topic.setCreationDate(raw.getCreationDate());
        topic.setCreationAuthor(raw.getCreationAuthor());

        topic.setServerAssignedId(raw.getServerAssigendId()); // note: typo in generated class
        topic.setTopicType(raw.getTopicType());
        topic.setTopicStatus(raw.getTopicStatus());
        topic.setPriority(raw.getPriority());
        topic.setAssignedTo(raw.getAssignedTo());
        topic.setStage(raw.getStage());
        topic.setDescription(raw.getDescription());
        topic.setModifiedDate(raw.getModifiedDate());
        topic.setModifiedAuthor(raw.getModifiedAuthor());
        topic.setDueDate(raw.getDueDate());
        topic.setIndex(raw.getIndex());
        topic.setLabels(raw.getLabels());
        topic.setReferenceLinks(raw.getReferenceLinks());

        if (raw.getAuthorization() != null && raw.getAuthorization().getTopicActions() != null) {
            List<String> actions = enumToStrings(raw.getAuthorization().getTopicActions());
            topic.setTopicActions(actions);
        }
        return topic;
    }

    public static List<Topic> toDomainList(TopicGET[] raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw)
                .map(TopicMapper::toDomain)
                .toList();
    }
}

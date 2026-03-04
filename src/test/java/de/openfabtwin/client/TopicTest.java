package de.openfabtwin.client;

import de.openfabtwin.domain.Topic;
import de.openfabtwin.domain.TopicFilter;
import de.openfabtwin.BcfApiException;
import de.openfabtwin.generated.model.TopicPOST;
import de.openfabtwin.generated.model.TopicPUT;
import org.junit.jupiter.api.Test;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TopicTest extends BcfClientTestBase {

    private static final String PROJECT_ID = "1507329";
    private static final String TOPIC_GUID = "B345F4F2-3A04-B43B-A713-5E456BEF8228";
    private static final String TOPIC_JSON = """
            {
              "guid": "B345F4F2-3A04-B43B-A713-5E456BEF8228",
              "server_assigend_id": "ISSUE-00549",
              "creation_author": "Architect@example.com",
              "creation_date": "2016-08-01T17:34:22.409Z",
              "topic_type": "Clash",
              "topic_status": "open",
              "title": "Example topic 3",
              "priority": "high",
              "labels": ["Architecture", "Heating"],
              "assigned_to": "harry.muster@example.com",
              "authorization": {
                "topic_actions": ["createComment", "createViewpoint"]
              }
            }
            """;

    // -------------------------------------------------------------------------
    // getTopics()
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnListOfTopics() throws Exception {
        stubResponse(200, "[" + TOPIC_JSON + "]");

        List<Topic> topics = bcfClient.getTopics("1507329", null);

        assertEquals(1, topics.size());
        assertEquals("B345F4F2-3A04-B43B-A713-5E456BEF8228", topics.get(0).getGuid());
        assertEquals("Example topic 3", topics.get(0).getTitle());
        assertEquals("Clash", topics.get(0).getTopicType());
        assertEquals("open", topics.get(0).getTopicStatus());
        assertEquals("high", topics.get(0).getPriority());
        assertEquals("Architect@example.com", topics.get(0).getCreationAuthor());
        assertTrue(topics.get(0).getLabels().contains("Architecture"));
        assertTrue(topics.get(0).getLabels().contains("Heating"));
        assertTrue(topics.get(0).getTopicActions().contains("createComment"));
        assertTrue(topics.get(0).getTopicActions().contains("createViewpoint"));
    }

    @Test
    void shouldReturnEmptyListWhenNoTopics() throws Exception {
        stubResponse(200, "[]");
        List<Topic> topics = bcfClient.getTopics("1507329", null);
        assertTrue(topics.isEmpty());
    }

    @Test
    void shouldApplyTopicFilter() throws Exception {
        stubResponse(200, "[" + TOPIC_JSON + "]");
        TopicFilter filter = new TopicFilter()
                .orderBy("creation_date")
                .top(10)
                .skip(0);
        List<Topic> topics = bcfClient.getTopics("1507329", filter);
        assertEquals(1, topics.size());
    }

    // -------------------------------------------------------------------------
    // getTopic()
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnSingleTopic() throws Exception {
        stubResponse(200, TOPIC_JSON);
        Topic topic = bcfClient.getTopic("1507329", "B345F4F2-3A04-B43B-A713-5E456BEF8228");
        assertEquals("B345F4F2-3A04-B43B-A713-5E456BEF8228", topic.getGuid());
        assertEquals("Example topic 3", topic.getTitle());
        assertEquals("Clash", topic.getTopicType());
        assertEquals("open",  topic.getTopicStatus());
        assertEquals("high",  topic.getPriority());
        assertEquals("Architect@example.com", topic.getCreationAuthor());
        assertEquals("2016-08-01T17:34:22.409Z", topic.getCreationDate());
        assertEquals("harry.muster@example.com", topic.getAssignedTo());
        assertTrue(topic.getLabels().contains("Architecture"));
        assertTrue(topic.getTopicActions().contains("createComment"));
        assertTrue(topic.getTopicActions().contains("createViewpoint"));
    }

    @Test
    void shouldReturnTopicWithMinimalFields() throws Exception {
        stubResponse(200, """
                {
                  "guid": "%s",
                  "title": "Minimal topic",
                  "creation_author": "admin@bcfserver",
                  "creation_date": "2024-01-01T00:00:00.000Z"
                }
                """.formatted(TOPIC_GUID));
        Topic topic = bcfClient.getTopic("1507329", "B345F4F2-3A04-B43B-A713-5E456BEF8228");
        assertEquals("B345F4F2-3A04-B43B-A713-5E456BEF8228", topic.getGuid());
        assertEquals("Minimal topic", topic.getTitle());
        // optional fields should be null
        assertNull(topic.getTopicType());
        assertNull(topic.getTopicStatus());
        assertNull(topic.getPriority());
        assertNull(topic.getTopicActions());
    }

    @Test
    void shouldThrowWhenTopicNotFound() throws Exception {
        stubResponse(404, "");
        BcfApiException ex = assertThrows(BcfApiException.class,
                () -> bcfClient.getTopic("1507329", "nonexistent-guid"));
        assertEquals(404, ex.getStatusCode());
    }

    // -------------------------------------------------------------------------
    // createTopic()
    // -------------------------------------------------------------------------

    @Test
    void shouldCreateAndReturnTopic() throws Exception {
        stubResponse(201, TOPIC_JSON);

        TopicPOST payload = new TopicPOST();
        payload.setTitle("Example topic 3");
        Topic created = bcfClient.createTopic("1507329", payload);
        assertEquals("B345F4F2-3A04-B43B-A713-5E456BEF8228", created.getGuid());
        assertEquals("Example topic 3", created.getTitle());
        assertEquals("Clash", created.getTopicType());
        assertEquals("Architect@example.com", created.getCreationAuthor());
    }

    // -------------------------------------------------------------------------
    // updateTopic() — state change
    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void shouldShowStatChangeAfterUpdateTopic() throws Exception {
        HttpResponse<String> getPrevResponse   = mock(HttpResponse.class);
        HttpResponse<String> putUpdateResponse = mock(HttpResponse.class);

        // stub 1 — GET previous state
        when(getPrevResponse.statusCode()).thenReturn(200);
        when(getPrevResponse.body()).thenReturn("""
                {
                  "guid": "%s",
                  "title": "prevTopic",
                  "creation_author": "admin@bcfserver",
                  "creation_date": "2024-01-01T00:00:00.000Z",
                  "topic_status": "open"
                }
                """.formatted(TOPIC_GUID));

        // stub 2 — PUT updated state
        when(putUpdateResponse.statusCode()).thenReturn(200);
        when(putUpdateResponse.body()).thenReturn("""
                {
                  "guid": "%s",
                  "title": "updated Topic - change title",
                  "creation_author": "admin@bcfserver",
                  "creation_date": "2024-01-01T00:00:00.000Z",
                  "modified_author": "admin@bcfserver",
                  "modified_date": "2024-06-01T10:00:00.000Z",
                  "topic_status": "open"
                }
                """.formatted(TOPIC_GUID));

        // first call → GET, second call → PUT
        doReturn(getPrevResponse, putUpdateResponse)
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        // step 1 — get previous state
        Topic prevTopic = bcfClient.getTopic(PROJECT_ID, TOPIC_GUID);
        assertEquals("prevTopic", prevTopic.getTitle());
        assertEquals("open",      prevTopic.getTopicStatus());
        assertNull(prevTopic.getModifiedAuthor()); // not modified yet

        // step 2 — update
        TopicPUT payload = new TopicPUT();
        payload.setTitle("updated Topic - change title");

        Topic updatedTopic = bcfClient.updateTopic(PROJECT_ID, TOPIC_GUID, payload);

        // step 3 — verify state changed
        assertEquals(TOPIC_GUID,                    updatedTopic.getGuid());
        assertEquals("updated Topic - change title", updatedTopic.getTitle());
        assertNotEquals(prevTopic.getTitle(),        updatedTopic.getTitle()); // ← key assertion
        assertEquals("admin@bcfserver",              updatedTopic.getModifiedAuthor());
        assertEquals("2024-06-01T10:00:00.000Z",     updatedTopic.getModifiedDate());
    }

    // -------------------------------------------------------------------------
    // deleteTopic() — state change
    // -------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void shouldShowStateChangeAfterDeleteTopic() throws Exception {
        HttpResponse<String> getPrevResponse    = mock(HttpResponse.class);
        HttpResponse<String> deleteResponse     = mock(HttpResponse.class);
        HttpResponse<String> getAfterResponse   = mock(HttpResponse.class);

        // stub 1 — GET topic exists before delete
        when(getPrevResponse.statusCode()).thenReturn(200);
        when(getPrevResponse.body()).thenReturn("""
                {
                  "guid": "%s",
                  "title": "prevTopic",
                  "creation_author": "admin@bcfserver",
                  "creation_date": "2024-01-01T00:00:00.000Z"
                }
                """.formatted(TOPIC_GUID));

        // stub 2 — DELETE succeeds
        when(deleteResponse.statusCode()).thenReturn(200);
        when(deleteResponse.body()).thenReturn("");

        // stub 3 — GET after delete → 404 (topic gone)
        when(getAfterResponse.statusCode()).thenReturn(404);
        when(getAfterResponse.body()).thenReturn("");

        // sequence: GET → DELETE → GET
        doReturn(getPrevResponse, deleteResponse, getAfterResponse)
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        // step 1 — topic exists before delete
        Topic prevTopic = bcfClient.getTopic(PROJECT_ID, TOPIC_GUID);
        assertEquals("prevTopic", prevTopic.getTitle());

        // step 2 — delete it
        assertDoesNotThrow(() -> bcfClient.deleteTopic(PROJECT_ID, TOPIC_GUID));

        // step 3 — topic no longer exists → 404
        BcfApiException ex = assertThrows(BcfApiException.class,
                () -> bcfClient.getTopic(PROJECT_ID, TOPIC_GUID));
        assertEquals(404, ex.getStatusCode());
    }

}

package de.openfabtwin.client;

import de.openfabtwin.BcfApiException;
import de.openfabtwin.domain.Extensions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExtensionTest  extends BcfClientTestBase{

    // -------------------------------------------------------------------------
    // getExtension()
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnExtensions() throws Exception {
        stubResponse(200, """
            {
              "topic_type": [],
              "topic_status": [],
              "topic_label": [],
              "snippet_type": [],
              "priority": [],
              "users": ["admin@bcfserver"],
              "stage": [],
              "project_actions": ["createTopic", "createDocument"],
              "topic_actions": ["createComment", "createViewpoint"],
              "comment_actions": ["update"]
            }
            """);

        Extensions ext = bcfClient.getExtension("1507329");

        // users — direct List<String>
        assertEquals(1,                 ext.getUsers().size());
        assertEquals("admin@bcfserver", ext.getUsers().get(0));

        // project_actions — enum → String
        assertEquals(2, ext.getProjectActions().size());
        assertTrue(ext.getProjectActions().contains("createTopic"));
        assertTrue(ext.getProjectActions().contains("createDocument"));

        // topic_actions — enum → String
        assertEquals(2, ext.getTopicActions().size());
        assertTrue(ext.getTopicActions().contains("createComment"));
        assertTrue(ext.getTopicActions().contains("createViewpoint"));

        // comment_actions — enum → String
        assertEquals(1, ext.getCommentActions().size());
        assertTrue(ext.getCommentActions().contains("update"));
    }

    @Test
    void shouldThrowWhenProjectNotFound() throws Exception {
        stubResponse(404, "");

        BcfApiException ex = assertThrows(BcfApiException.class,
                () -> bcfClient.getExtension("nonexistent"));

        assertEquals(404, ex.getStatusCode());
    }

}

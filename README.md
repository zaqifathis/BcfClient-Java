# BCF Client Java

A Java client library for the [BIM Collaboration Format (BCF)](https://github.com/buildingSMART/BCF-API) REST API. It handles OAuth 2.0 authentication and provides typed clients for all BCF API resources.

## Requirements

- Java 21
- Maven

## Build

```bash
mvn clean install
```

## Usage

### 1. Authenticate

```java
FoundationClient foundation = new FoundationClient(
    "https://your-bcf-server.com/",
    "your-client-id",
    "your-client-secret"
);
```

On first use, `foundation.getAccessToken()` opens the browser for OAuth 2.0 authorization. Tokens are refreshed automatically.

### 2. Connect to the BCF API

```java
BcfClient bcf = new BcfClient(foundation, "3.0");
bcf.resolveVersion(); // discovers the BCF base URL and initializes all sub-clients
```

### 3. Use the clients

```java
// Projects
List<ProjectGET> projects = bcf.getProject().getAllProjects();

// Topics
TopicGET topic = bcf.getTopic().createTopic(projectId, payload);
List<TopicGET> topics = bcf.getTopic().getTopics(projectId, null, null, null, null);

// Comments, Viewpoints, Documents, Events, Files, and more
bcf.getComment().getComments(projectId, topicId);
bcf.getViewpoint().getViewpoints(projectId, topicId);
```

## Project Structure

```
src/main/java/de/openfabtwin/
├── BcfClient.java                  # Main entry point
├── BcfException.java               # Runtime exception wrapper
├── GeneratedApiClientFactory.java  # Configures the HTTP client with auth
├── auth/
│   ├── FoundationClient.java       # OAuth 2.0 login, token refresh
│   └── OAuthReceiver.java          # Local redirect URI handler
├── client/
│   ├── ProjectClient.java
│   ├── TopicClient.java
│   ├── CommentClient.java
│   ├── ViewpointClient.java
│   ├── DocumentsClient.java
│   ├── DocumentReferencesClient.java
│   ├── EventClient.java
│   ├── FilesClient.java
│   ├── RelatedTopicsClient.java
│   └── SnippetsClient.java
└── generated/                      # OpenAPI-generated models and API stubs
```

## License

MIT

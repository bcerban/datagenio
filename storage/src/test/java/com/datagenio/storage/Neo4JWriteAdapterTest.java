package com.datagenio.storage;

import com.datagenio.crawler.api.*;
import com.datagenio.model.WebFlowGraph;
import com.datagenio.model.WebState;
import com.datagenio.model.WebTransition;
import com.datagenio.model.request.AbstractUrl;
import com.datagenio.storageapi.Relationships;
import com.datagenio.context.Configuration;
import com.datagenio.storageapi.Connection;
import com.datagenio.storageapi.StorageException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.*;

public class Neo4JWriteAdapterTest {

    private static String TEST_URL = "http://test.com";

    private Connection connection;
    private Configuration configuration;
    private Gson gson;
    private GraphDatabaseService databaseService;
    private Neo4JWriteAdapter writeAdapter;

    @Before
    public void setUp() {
        configuration = mock(Configuration.class);
        connection = mock(Connection.class);
        databaseService = mock(GraphDatabaseService.class);
        gson = new GsonBuilder().create();

        doReturn(TEST_URL).when(configuration).getRootUrl();
        doReturn(databaseService).when(connection).create(TEST_URL);

        writeAdapter = new Neo4JWriteAdapter(configuration, connection, gson);
    }

    @Test
    public void testSaveWebFlowGraph() throws StorageException {
        var first = mock(WebState.class);
        var second = mock(WebState.class);
        var transition = mock(WebTransition.class);
        var uri = mock(AbstractUrl.class);

        doReturn("firstID").when(first).getIdentifier();
        doReturn("secondID").when(second).getIdentifier();
        doReturn(uri).when(first).getUrl();
        doReturn(uri).when(second).getUrl();
        doReturn(first).when(transition).getOrigin();
        doReturn(second).when(transition).getDestination();
        doReturn(mock(Node.class)).when(connection).findNode(eq(databaseService), any(), any());

        var graph = mock(WebFlowGraph.class);
        doReturn(List.of(first, second)).when(graph).getStates();
        doReturn(List.of(transition)).when(graph).getTransitions();
        writeAdapter.save(graph);

//        verify(connection, times(2)).addNode(eq(databaseService), any(), any());
        verify(connection, times(1)).addEdge(eq(databaseService), any(), any(), eq(Relationships.WEB_TRANSITION), any());
    }

    @Test
    public void testSaveEventGraph() throws StorageException {
        var first = mock(State.class);
        var second = mock(State.class);
        var transition = mock(Transitionable.class);
        var executedEvent = mock(ExecutedEventable.class);
        var event = mock(Eventable.class);
        var uri = URI.create("http://test.com");
        var stateNode = mock(Node.class);

        doReturn(uri).when(first).getUri();
        doReturn(uri).when(second).getUri();
        doReturn("id1").when(first).getIdentifier();
        doReturn("id2").when(second).getIdentifier();
        doReturn(mock(Document.class)).when(first).getDocument();
        doReturn(mock(Document.class)).when(second).getDocument();
        doReturn(Configuration.REQUEST_SAVE_AS_NODE).when(configuration).getRequestSaveMode();
        doReturn(first).when(transition).getOrigin();
        doReturn(second).when(transition).getDestination();
        doReturn(executedEvent).when(transition).getExecutedEvent();
        doReturn(Transitionable.Status.TRAVERSED).when(transition).getStatus();
        doReturn(event).when(executedEvent).getEvent();
        doReturn(Eventable.EventType.CLICK).when(event).getEventType();
        doReturn(Eventable.Status.SUCCEEDED).when(event).getStatus();
        doReturn(mock(Element.class)).when(event).getSource();
        doReturn(stateNode).when(connection).findNode(eq(databaseService), any(), any());

        var graph = mock(EventFlowGraph.class);
        doReturn(List.of(first, second)).when(graph).getStates();
        doReturn(List.of(transition)).when(graph).getTransitions();
        writeAdapter.save(graph);

        verify(connection, times(1)).addNode(eq(databaseService), any(), any());
        verify(connection, times(2)).addEdge(eq(databaseService), any(), any(), any(), any());
    }
}

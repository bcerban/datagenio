package com.datagenio.crawler;

import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.model.Transition;
import org.jgrapht.Graph;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DirectedPseudograph;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class StateTraversalManagerTest {

    private State state;
    private Graph<State, Transitionable> graph;

    @Before
    public void setUp() {
        this.state = mock(State.class);
        this.graph = new DirectedPseudograph<>(Transitionable.class);
        this.graph.addVertex(this.state);
    }

    @Test
    public void testFindNearestUnfinishedState() {
        doReturn(true).when(this.state).isFinished();
        assertNull(StateTraversalManager.findNearestUnfinishedState(this.graph, this.state));
    }

    @Test
    public void testFindNearestUnfinishedStateNotNull() {
        doReturn(false).when(this.state).isFinished();
        assertEquals(this.state, StateTraversalManager.findNearestUnfinishedState(this.graph, this.state));
    }

    @Test
    public void testFindPath() {
        State second = mock(State.class);
        State third = mock(State.class);

        ExecutedEventable firstEvent = mock(ExecutedEventable.class);
        ExecutedEventable secondEvent = mock(ExecutedEventable.class);

        Transitionable firstToSecond = new Transition(this.state, second, firstEvent);
        Transitionable secondToThird = new Transition(second, third, secondEvent);

        this.graph.addVertex(second);
        this.graph.addVertex(third);
        this.graph.addEdge(this.state, second, firstToSecond);
        this.graph.addEdge(second, third, secondToThird);

        GraphPath path = StateTraversalManager.findPath(this.graph, this.state, third);
        assertEquals(this.state, path.getStartVertex());
        assertEquals(third, path.getEndVertex());
        assertEquals(List.of(firstToSecond, secondToThird), path.getEdgeList());
        assertEquals(List.of(this.state, second, third), path.getVertexList());
    }
}

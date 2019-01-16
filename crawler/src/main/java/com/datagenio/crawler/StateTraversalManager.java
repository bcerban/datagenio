package com.datagenio.crawler;

import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.traverse.BreadthFirstIterator;

public class StateTraversalManager {

    public static State findNearestUnfinishedState(Graph<State, Transitionable> graph, State from) {
        State nextUnfinished = null;
        // TODO: make strategy configurable, check whether we should navigate from parent
        var iterator = new BreadthFirstIterator<>(graph, from);

        while (nextUnfinished == null && iterator.hasNext()) {
            State next = iterator.next();
            if (!next.isFinished()) {
                nextUnfinished = next;
            }
        }

        return nextUnfinished;
    }

    public static GraphPath<State, Transitionable> findPath(Graph<State, Transitionable> graph, State from, State to) {
        DijkstraShortestPath pathBuilder = new DijkstraShortestPath(graph);
        return pathBuilder.getPath(from, to);
    }
}

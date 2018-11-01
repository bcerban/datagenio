package com.datagenio.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class State {
    private StateContext context;
    private Collection<AbstractRequest> requestSet;

    public State(AbstractUrl url) {
        this.context = new StateContext(url);
        this.requestSet = new ArrayList<>();
    }

    public StateContext getContext() {
        return context;
    }

    public void setContext(StateContext context) {
        this.context = context;
    }

    public Collection<AbstractRequest> getRequestSet() {
        return requestSet;
    }

    public void setRequestSet(Collection<AbstractRequest> requestSet) {
        this.requestSet = requestSet;
    }

    public void addRequest(AbstractRequest request) {
        this.requestSet.add(request);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;
        return Objects.equals(context.getContextUrl(), state.context.getContextUrl()) &&
                Objects.equals(requestSet, state.requestSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, requestSet);
    }
}

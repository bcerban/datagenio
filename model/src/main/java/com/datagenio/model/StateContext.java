package com.datagenio.model;

import com.datagenio.model.request.AbstractUrlImpl;

public class StateContext {
    private AbstractUrlImpl contextUrl;
    private Session session;

    public StateContext(AbstractUrlImpl contextUrl) {
        this.contextUrl = contextUrl;
        this.session = new Session();
    }

    public StateContext(AbstractUrlImpl contextUrl, Session session) {
        this.contextUrl = contextUrl;
        this.session = session;
    }

    public AbstractUrlImpl getContextUrl() {
        return contextUrl;
    }

    public void setContextUrl(AbstractUrlImpl contextUrl) {
        this.contextUrl = contextUrl;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}

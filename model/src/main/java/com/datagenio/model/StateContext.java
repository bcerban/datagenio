package com.datagenio.model;

public class StateContext {
    private AbstractUrl contextUrl;
    private Session session;

    public StateContext(AbstractUrl contextUrl) {
        this.contextUrl = contextUrl;
        this.session = new Session();
    }

    public StateContext(AbstractUrl contextUrl, Session session) {
        this.contextUrl = contextUrl;
        this.session = session;
    }

    public AbstractUrl getContextUrl() {
        return contextUrl;
    }

    public void setContextUrl(AbstractUrl contextUrl) {
        this.contextUrl = contextUrl;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}

package com.datagenio.crawler.api;

import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URI;
import java.util.Collection;

public interface State {

    String getIdentifier();
    URI getUri();
    Document getDocument();
    Collection<Eventable> getEventables();
    Collection<Eventable> getUnfiredEventables();
    Eventable getNextEventToFire() throws UncrawlableStateException;
    boolean isFinished();
    boolean isRoot();
    boolean hasScreenShot();
    File getScreenShot();
    String getDocumentFilePath();

    void setIdentifier(String identifier);
    void setUri(URI uri);
    void setDocument(Document document);
    void setEventables(Collection<Eventable> eventables);
    void setUnfiredEventables(Collection<Eventable> eventables);
    void markEventAsFired(ExecutedEventable event);
    void markEventAsUnfired(Eventable event);
    void setScreenShot(File screenshot);
    void setIsRoot(boolean isRoot);
    void setDocumentFilePath(String path);
}

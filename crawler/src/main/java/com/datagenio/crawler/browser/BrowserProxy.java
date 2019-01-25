package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.NetworkProxy;
import com.datagenio.crawler.api.RemoteRequest;
import com.datagenio.crawler.exception.PersistenceException;
import com.datagenio.crawler.util.HarSaver;
import com.datagenio.crawler.util.SiteBoundChecker;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BrowserProxy implements NetworkProxy {

    private BrowserMobProxy proxy;

    public BrowserProxy() {
        proxy = new BrowserMobProxyServer();
        proxy.setTrustAllServers(true);
        proxy.start(0);

        proxy.enableHarCaptureTypes(EnumSet.allOf(CaptureType.class));
    }

    @Override
    public Proxy getDriverProxy() {
        return ClientUtil.createSeleniumProxy(proxy);
    }

    @Override
    public Collection<RemoteRequest> getLoggedRequests() {
        return getLoggedRequests(proxy.getHar().getLog().getEntries());
    }

    @Override
    public Collection<RemoteRequest> getLoggedRequestsForDomain(URI domain) {
        List<HarEntry> entries = proxy.getHar().getLog().getEntries();
        List<HarEntry> filteredEntries = entries.stream()
                .filter(e -> !SiteBoundChecker.isOutOfBounds(URI.create(e.getRequest().getUrl()), domain))
                .collect(Collectors.toList());

        return getLoggedRequests(filteredEntries);
    }

    @Override
    public Collection<RemoteRequest> getLoggedRequestsForDomain(URI domain, String fileName, String saveTo) {
        var har = proxy.getHar();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HarSaver.saveHarFile(har, fileName, saveTo);
                } catch (PersistenceException e) { }
            }
        }).start();

        return getLoggedRequestsForDomain(domain);
    }

    private Collection<RemoteRequest> getLoggedRequests(List<HarEntry> entries) {
        Collection<RemoteRequest> requests = new ArrayList<>();

        IntStream.range(0, entries.size() -1).forEach(order -> {
            var request = buildRequest(entries.get(order));
            request.setSortOrder(order);
            requests.add(request);
        });

        return requests;
    }

    @Override
    public void saveFor(String site) {
        proxy.newHar(site);
    }

    @Override
    public void stop() {
        try {
            proxy.stop();
        } catch (IllegalStateException e) { }
    }

    private RemoteRequest buildRequest(HarEntry entry) {
        return new RemoteHttpRequest(entry.getRequest());
    }
}

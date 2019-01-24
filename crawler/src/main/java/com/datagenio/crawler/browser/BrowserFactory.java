package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.Browser;
import com.datagenio.crawler.api.NetworkProxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class BrowserFactory {

    public static Browser drivenByFirefox() {
        NetworkProxy networkProxy = new BrowserProxy();

        // TODO: add all other required driver options
        FirefoxOptions options = new FirefoxOptions();
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.DISMISS);
        options.setProxy(networkProxy.getDriverProxy());

        return new DrivenBrowser(new FirefoxDriver(options), networkProxy);
    }
}

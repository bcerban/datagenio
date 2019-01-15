package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.Browser;
import org.openqa.selenium.firefox.FirefoxDriver;

public class BrowserFactory {

    public static Browser drivenByFirefox() {
        return new DrivenBrowser(new FirefoxDriver());
    }
}

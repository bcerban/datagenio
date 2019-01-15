package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.Browser;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import org.apache.commons.lang.NotImplementedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class DrivenBrowser implements Browser {

    private static final int BROWSER_CLOSE_TIMEOUT = 5;
    private static Logger logger = LoggerFactory.getLogger(DrivenBrowser.class);
    private static ExecutorService closeExecutor = Executors.newCachedThreadPool(new BrowserCloserFactory());

    private WebDriver driver;

    public DrivenBrowser(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void back() throws BrowserException {
        try {
            logger.debug("Navigating back from {}.", this.driver.getCurrentUrl());

            this.driver.navigate().back();
            Thread.sleep(DEFAULT_WAIT_AFTER_LOAD);

            logger.debug("Back page loaded successfully.");
        } catch (WebDriverException e) {
            logger.debug("Back page load was interrupted before completing.", e);
            throw new BrowserException(e);
        } catch (InterruptedException e) {
            logger.debug("Back page load was interrupted before completing.", e);
            Thread.currentThread().interrupt();
            return;
        }
    }

    @Override
    public void close() throws BrowserException {
        logger.debug("Closing browser at {}.", this.driver.getCurrentUrl());

        var task = closeExecutor.submit(this.driver::quit);
        try {
            task.get(BROWSER_CLOSE_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            logger.debug("Browser timed out while trying to close at {}.", this.driver.getCurrentUrl());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.debug("Unexpected exception while trying to close browser at {}.", this.driver.getCurrentUrl());
            throw new BrowserException(e);
        } catch (InterruptedException e) {
            logger.debug("Interruption while trying to close browser at {}. Will proceed to forced interruption.", this.driver.getCurrentUrl());
            Thread.currentThread().interrupt();
        }

        logger.debug("Browser at {} closed.", this.driver.getCurrentUrl());
    }

    @Override
    public void pause() throws BrowserException {
        throw new NotImplementedException();
    }

    @Override
    public File takeScreenShot() {
        logger.debug("Capturing screen for " + this.driver.getCurrentUrl());

        TakesScreenshot screenshotDriver = (TakesScreenshot) this.driver;
        File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
        removeScreenShotCanvas();

        logger.debug("Screen capture for " + this.driver.getCurrentUrl() + " saved to " + screenshot.getName() + ".");
        return screenshot;
    }

    /**
     * Copied from Crawljax.
     * TODO: verify needed
     */
    private void removeScreenShotCanvas() {
        String js = "";
        js += "var canvas = document.getElementById('fxdriver-screenshot-canvas');";
        js += "if(canvas != null){";
        js += "canvas.parentNode.removeChild(canvas);";
        js += "}";
        try {
            executeJavaScript(js);
        } catch (BrowserException e) {
            logger.error("Removing of Canvas Generated By FirefoxDriver failed,"
                    + " most likely leaving it in the browser", e);
        }
    }

    @Override
    public void navigateTo(URI uri) throws BrowserException {
        try {
            logger.debug("Navigating to {}.", uri.toString());

            this.driver.navigate().to(uri.toString());
            Thread.sleep(DEFAULT_WAIT_AFTER_LOAD);
            handlePopups();

            logger.debug("Page loaded successfully.");
        } catch (WebDriverException e) {
            logger.debug("Navigation was interrupted before completing page load.", e);
            throw new BrowserException(e);
        } catch (InterruptedException e) {
            logger.debug("Navigation was interrupted before completing page load.", e);
            Thread.currentThread().interrupt();
            return;
        }
    }

    @Override
    public Document triggerEvent(Eventable event) throws UnsupportedEventTypeException, InvalidArgumentException {
        logger.debug("Attempting to trigger event {}...", event.getIdentifier());

        // Check element is present in current web interface
        WebElement element = this.driver.findElement(By.xpath(event.getXpath()));
        if (element == null) {
            logger.debug("Element not found for event {} in {}", event.getIdentifier(), this.driver.getCurrentUrl());
            throw new InvalidArgumentException("Selected event is not present in current interface.");
        }

        return handleEventByType(event, element);
    }

    private Document handleEventByType(Eventable event, WebElement element) throws UnsupportedEventTypeException {
        Eventable.EventType type = event.getEventType();
        switch (type) {
            case click:
                return triggerClickableEvent(event, element);
            case submit:
                return triggerSubmitEvent(event, element);
            default:
                throw new UnsupportedEventTypeException("Type " + type.toString() + " is not supported.");
        }
    }

    public Document triggerClickableEvent(Eventable event, WebElement element) {
        try {
            element.click();
        } catch (StaleElementReferenceException|ElementNotInteractableException e) {
            logger.debug(
                    "Element for event {} is unavailable in {}. Error: {}",
                    event.getIdentifier(), this.driver.getCurrentUrl(), e.getMessage()
            );
            throw new InvalidArgumentException("Selected event is unavailable.", e);
        }

        return getDOM();
    }

    public Document triggerSubmitEvent(Eventable event, WebElement element) {
        try {
            // TODO: actual input handling should be done here...
            element.clear();
            element.submit();
        } catch (StaleElementReferenceException|ElementNotInteractableException|NoSuchElementException e) {
            logger.debug(
                    "Element for event {} is unavailable in {}. Error: {}",
                    event.getIdentifier(), this.driver.getCurrentUrl(), e.getMessage()
            );
            throw new InvalidArgumentException("Selected event is stale.", e);
        }

        return getDOM();
    }

    @Override
    public Document getDOM() {
        return Jsoup.parse(this.driver.getPageSource());
    }

    /**
     * alert, prompt, and confirm behave as if the OK button is always clicked.
     * Copied from Crawljax.
     * TODO: verify needed
     */
    private void handlePopups() {
        try {
            executeJavaScript("window.alert = function(msg){return true;};"
                    + "window.confirm = function(msg){return true;};"
                    + "window.prompt = function(msg){return true;};");
        } catch (BrowserException e) {
            logger.error("Handling of PopUp windows failed", e);
        }
    }

    /**
     * Execute JavaScript in the browser.
     */
    @Override
    public Object executeJavaScript(String code) throws BrowserException {
        try {
            logger.debug("Executing javascript for " + this.driver.getCurrentUrl() + ": " + code);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return js.executeScript(code);
        } catch (UnsupportedOperationException e) {
            // HtmlUnitDriver throws UnsupportedOperationException if it can't execute the
            // JavaScript, for example, "Cannot execute JS against a plain text page".
            throw new BrowserException(e);
        } catch (WebDriverException e) {
            throw new BrowserException(e);
        }
    }

    private static class BrowserCloserFactory implements ThreadFactory {
        private static final String NAME_PREFIX = "Datagenio-BrowserCloserThread-";
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public BrowserCloserFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, NAME_PREFIX + threadNumber.getAndIncrement(), 0);
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);

            return t;
        }
    }
}

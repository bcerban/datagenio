package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.*;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.EventTriggerException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import com.datagenio.crawler.model.StateImpl;
import com.datagenio.crawler.util.EventExtractorFactory;
import com.datagenio.databank.util.XPathParser;
import org.apache.commons.lang.NotImplementedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class DrivenBrowser implements Browser {

    private static final int BROWSER_CLOSE_TIMEOUT = 5;
    private static Logger logger = LoggerFactory.getLogger(DrivenBrowser.class);
    private static ExecutorService closeExecutor = Executors.newCachedThreadPool(new BrowserCloserFactory());

    private NetworkProxy proxy;
    private WebDriver driver;
    private EventableExtractor extractor;
    private final String initialHandle;

    public DrivenBrowser(WebDriver driver, NetworkProxy proxy) {
        this.driver = driver;
        this.proxy = proxy;
        this.extractor = EventExtractorFactory.get();
        this.initialHandle = driver.getWindowHandle();

        this.driver.manage().window().maximize();
        this.driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_AFTER_LOAD, TimeUnit.SECONDS);
    }

    @Override
    public void back() throws BrowserException {
        try {
            logger.debug("Navigating back from {}.", this.driver.getCurrentUrl());
            driver.navigate().back();
            logger.debug("Back page loaded successfully.");
        } catch (WebDriverException e) {
            logger.debug("Back page load was interrupted before completing.", e);
            throw new BrowserException(e);
        }
    }

    @Override
    public void backOrClose() throws BrowserException {
        State previous = getCurrentBrowserState();
        back();
        State current = getCurrentBrowserState();
        if (current.equals(previous)) {
            close();
            switchToDefaultWindow();
        }
    }

    @Override
    public void close() throws BrowserException {
        logger.debug("Closing browser at {}.", driver.getCurrentUrl());
        handleClosingTask(closeExecutor.submit(driver::close));
        logger.debug("Browser session closed.");
    }

    @Override
    public void quit() throws BrowserException {
        logger.debug("Closing all browser windows.");
        proxy.stop();
        handleClosingTask(closeExecutor.submit(driver::quit));
        logger.debug("All browser sessions finished.");
    }

    private void handleClosingTask(Future<?> task) throws BrowserException {
        try {
            task.get(BROWSER_CLOSE_TIMEOUT, TimeUnit.SECONDS);
            Thread.sleep(BROWSER_CLOSE_TIMEOUT);
        } catch (TimeoutException e) {
            logger.debug("Browser timed out while trying to close.");
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.debug("Unexpected exception while trying to close browser.");
            throw new BrowserException(e);
        } catch (InterruptedException|NoSuchWindowException e) {
            logger.debug("Interruption while trying to close browser. Will proceed to forced interruption.");
            Thread.currentThread().interrupt();
        }
    }

    private void switchToDefaultWindow() throws BrowserException {
        if (driver == null) {
            throw new BrowserException("Driver is not available.");
        }

        try {
            driver.switchTo().window(initialHandle);
        } catch (NoSuchWindowException e) {
            throw new BrowserException("Default window not found.", e);
        }
    }

    @Override
    public void pause() throws BrowserException {
        throw new NotImplementedException();
    }

    @Override
    public State getCurrentBrowserState() throws BrowserException {
        return new StateImpl(
                URI.create(driver.getCurrentUrl()),
                getDOM(),
                extractor
        );
    }

    @Override
    public File getScreenShotFile() {
        logger.debug("Capturing screen for " + driver.getCurrentUrl());

        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
        removeScreenShotCanvas();

        logger.debug("Screen capture for " + driver.getCurrentUrl() + " saved to " + screenshot.getName() + ".");
        return screenshot;
    }

    @Override
    public byte[] getScreenShotBytes() {
        logger.debug("Capturing screen for " + driver.getCurrentUrl());

        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        byte[] screenshot = screenshotDriver.getScreenshotAs(OutputType.BYTES);
        removeScreenShotCanvas();

        logger.debug("Screen capture for " + driver.getCurrentUrl() + " returned.");
        return screenshot;
    }

    @Override
    public Collection<RemoteRequest> getCapturedRequests(URI domain) {
        return proxy.getLoggedRequestsForDomain(domain);
    }

    @Override
    public Collection<RemoteRequest> getCapturedRequests(URI domain, String fileName, String saveToDirectory) {
        return proxy.getLoggedRequestsForDomain(domain, fileName, saveToDirectory);
    }

    /**
     * Copied from Crawljax.
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

            proxy.saveFor(uri.toString());
            driver.navigate().to(uri.toString());
            handlePopups();

            logger.debug("Page loaded successfully.");
        } catch (WebDriverException e) {
            logger.debug("Navigation was interrupted before completing page load.", e);
            throw new BrowserException(e);
        }
    }

    @Override
    public void triggerEvent(Eventable event, Map<String, String> inputs) throws UnsupportedEventTypeException, EventTriggerException {
        logger.debug("Attempting to trigger event {}...", event.getEventIdentifier());
        WebElement element = null;

        try {
            // Check element is present in current web interface
            element = driver.findElement(By.xpath(event.getXpath()));
        } catch (NoSuchElementException e) { }

        if (element == null) {
            logger.debug("Element not found for event {} in {}", event.getEventIdentifier(), driver.getCurrentUrl());
            throw new EventTriggerException("Selected event is not present in current interface.");
        }

        proxy.saveFor(event.getEventIdentifier().replaceAll("/", "-"));
        handleEventByType(event, element, inputs);
    }

    private void handleEventByType(Eventable event, WebElement element, Map<String, String> inputs) throws UnsupportedEventTypeException, EventTriggerException {
        Eventable.EventType type = event.getEventType();
        switch (type) {
            case CLICK:
                triggerClickableEvent(event, element);
                break;
            case SUBMIT:
                triggerSubmitEvent(event, element, inputs);
                break;
            default:
                throw new UnsupportedEventTypeException("Type " + type.toString() + " is not supported.");
        }
    }

    public void triggerClickableEvent(Eventable event, WebElement element) throws EventTriggerException {
        try {
            int handleCount = driver.getWindowHandles().size();
            element.click();

            if (driver.getWindowHandles().size() > handleCount) {
                String newest = (String)driver.getWindowHandles().toArray()[driver.getWindowHandles().size() - 1];
                driver.switchTo().window(newest);
            }
        } catch (StaleElementReferenceException|ElementNotInteractableException e) {
            logger.debug(
                    "Element for event {} is unavailable in {}. Error: {}",
                    event.getEventIdentifier(), driver.getCurrentUrl(), e.getMessage()
            );
            throw new EventTriggerException("Selected event is unavailable.", e);
        }
    }

    public void triggerSubmitEvent(Eventable event, WebElement element, Map<String, String> inputs) throws EventTriggerException {
        try {
            driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_AFTER_SUBMIT, TimeUnit.SECONDS);
            fillElementInputs(element, inputs);

            // Submit on form is unreliable because it doesn't trigger javascript functions.
            // Instead, we are required to find the submit button/input and CLICK on it.
            // element.submit();
            findSubmitElement(event).click();
            Thread.sleep(5000);
            this.driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_AFTER_LOAD, TimeUnit.SECONDS);
        } catch (StaleElementReferenceException|ElementNotInteractableException|NoSuchElementException e) {
            logger.debug(
                    "Element for event {} is unavailable in {}. Error: {}",
                    event.getEventIdentifier(), driver.getCurrentUrl(), e.getMessage()
            );
            throw new EventTriggerException("Selected event is stale.", e);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage());
        }
    }

    private WebElement findSubmitElement(Eventable event) {
        Element child = extractor.findSubmitableChild(event.getSource());
        if (child != null) {
            return driver.findElement(By.xpath(XPathParser.getXPathFor(child)));
        }

        throw new NoSuchElementException("Submittable child not found.");
    }

    private void fillElementInputs(WebElement element, Map<String, String> inputs) {
        inputs.forEach((xpath, value) -> fillElementByXpath(element, xpath, value));
    }

    private void fillElementByXpath(WebElement element, String xpath, String value) {
        try {
            var webElement = element.findElement(By.xpath(xpath));
            if (webElement.isEnabled()) {
                webElement.clear();
                webElement.sendKeys(value);
            }
        } catch (NoSuchElementException|InvalidElementStateException e) { }
    }

    @Override
    public Document getDOM() throws BrowserException {
        try {
            return Jsoup.parse(driver.getPageSource());
        } catch (NoSuchWindowException e) {
            throw new BrowserException("Can't access browser.", e);
        }
    }

    /**
     * alert, prompt, and confirm behave as if the OK button is always clicked.
     * Copied from Crawljax.
     */
    private void handlePopups() {
        try {
            executeJavaScript("window.alert = function(msg){return true;};"
                    + "window.confirm = function(msg){return true;};"
                    + "window.prompt = function(msg){return true;};"
                    + "window.print=function(){};");
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

package com.datagenio.crawler.browser;

import com.datagenio.context.EventInput;
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
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DrivenBrowser implements Browser {

    private static final int BROWSER_CLOSE_TIMEOUT = 15;
    private static Logger logger = LoggerFactory.getLogger(DrivenBrowser.class);
    private static ExecutorService closeExecutor = Executors.newCachedThreadPool(new BrowserCloserFactory());

    private final Lock readLock;
    private final Lock writeLock;

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

        ReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
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
        readLock.lock();

        try {
            Document view = getDOM();
            State state = new StateImpl(URI.create(driver.getCurrentUrl()), view);
            List<Eventable> events = extractor.extractShuffled(state, view);

            // TODO: Check that all detected eventables are interactable

            state.setEventables(events);
            state.setUnfiredEventables(events);
            return state;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public File getScreenShotFile() {
        logger.debug("Capturing screen for " + driver.getCurrentUrl());

        readLock.lock();
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
        removeScreenShotCanvas();

        logger.debug("Screen capture for " + driver.getCurrentUrl() + " saved to " + screenshot.getName() + ".");

        readLock.unlock();
        return screenshot;
    }

    @Override
    public byte[] getScreenShotBytes() {
        logger.debug("Capturing screen for " + driver.getCurrentUrl());

        readLock.lock();
        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
        byte[] screenshot = screenshotDriver.getScreenshotAs(OutputType.BYTES);
        removeScreenShotCanvas();
        readLock.unlock();

        logger.debug("Screen capture for " + driver.getCurrentUrl() + " returned.");
        return screenshot;
    }

    @Override
    public Collection<RemoteRequest> getCapturedRequests(URI domain) {
        readLock.lock();
        try {
            return proxy.getLoggedRequestsForDomain(domain);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Collection<RemoteRequest> getCapturedRequests(URI domain, String fileName, String saveToDirectory) {
        readLock.lock();
        try {
            return proxy.getLoggedRequestsForDomain(domain, fileName, saveToDirectory);
        } finally {
            readLock.unlock();
        }
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
        navigateTo(uri, true);
    }

    @Override
    public void navigateTo(URI uri, boolean saveProxyData) throws BrowserException {
        writeLock.lock();
        try {
            logger.debug("Navigating to {}.", uri.toString());

            if (saveProxyData) proxy.saveFor(uri.toString());
            driver.navigate().to(uri.toString());
            Thread.sleep(100);
            handlePopups();

            logger.debug("Page loaded successfully.");
        } catch (WebDriverException|InterruptedException e) {
            logger.debug("Navigation was interrupted before completing page load.", e);
            throw new BrowserException(e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void triggerEvent(Eventable event, List<EventInput> inputs) throws UnsupportedEventTypeException, EventTriggerException {
        triggerEvent(event, inputs, true);
    }

    @Override
    public void triggerEvent(Eventable event, List<EventInput> inputs, boolean saveProxyData) throws UnsupportedEventTypeException, EventTriggerException {
        logger.debug("Attempting to trigger event {}...", event.getEventIdentifier());
        writeLock.lock();
        WebElement element = null;

        try {
            // Check element is present in current web interface
            element = driver.findElement(By.xpath(event.getXpath()));
        } catch (NoSuchElementException e) { } finally {
            writeLock.unlock();
        }

        if (element == null) {
            logger.debug("Element not found for event {} in {}", event.getEventIdentifier(), driver.getCurrentUrl());
            throw new EventTriggerException("Selected event is not present in current interface.");
        }

        if (saveProxyData) proxy.saveFor(event.getEventIdentifier().replaceAll("/", "-"));
        handleEventByType(event, element, inputs);
        handlePopups();
    }

    private void handleEventByType(Eventable event, WebElement element, List<EventInput> inputs) throws UnsupportedEventTypeException, EventTriggerException {
        Eventable.EventType type = event.getEventType();
        switch (type) {
            case CLICK:
                triggerClickEvent(event, element);
                break;
            case SUBMIT:
                triggerSubmitEvent(event, element, inputs);
                break;
            default:
                throw new UnsupportedEventTypeException("Type " + type.toString() + " is not supported.");
        }
    }

    public void triggerClickEvent(Eventable event, WebElement element) throws EventTriggerException {
        writeLock.lock();
        try {
            int handleCount = driver.getWindowHandles().size();
            element.click();
            Thread.sleep(1000);

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
        } catch (InterruptedException e) {
            logger.debug(e.getMessage());
        } finally {
            writeLock.lock();
        }
    }

    public void triggerSubmitEvent(Eventable event, WebElement element, List<EventInput> inputs) throws EventTriggerException {
        writeLock.lock();
        try {
            fillElementInputs(element, inputs);

            // Submit on form is unreliable because it doesn't trigger javascript functions.
            // Instead, we are required to find the submit button/input and CLICK on it.
            // If no clickable element is found, then we try submitting.
            try {
                findSubmitElement(event).click();
            } catch (NoSuchElementException e) {
                element.submit();
            }

            Thread.sleep(1000);
        } catch (StaleElementReferenceException|ElementNotInteractableException e) {
            logger.debug(
                    "Element for event {} is unavailable in {}. Error: {}",
                    event.getEventIdentifier(), driver.getCurrentUrl(), e.getMessage()
            );
            clearElementInputs(element, inputs);

            throw new EventTriggerException("Selected event is stale.", e);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    private WebElement findSubmitElement(Eventable event) {
        Element child = extractor.findSubmittableChild(event.getSource());
        if (child != null) {
            return driver.findElement(By.xpath(XPathParser.getXPathFor(child)));
        }

        throw new NoSuchElementException("Submittable child not found.");
    }

    private void fillElementInputs(WebElement element, List<EventInput> inputs) {
        inputs.forEach(input -> fillElementByXpath(element, input.getXpath(), input.getInputValue()));
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

    private void clearElementInputs(WebElement element, List<EventInput> inputs) {
        inputs.forEach(input -> {
            try {
                var webElement = element.findElement(By.xpath(input.getXpath()));
                webElement.clear();
            } catch (java.util.NoSuchElementException|InvalidElementStateException e) { }
        });
    }

    @Override
    public Document getDOM() throws BrowserException {
        readLock.lock();
        try {
            return Jsoup.parse(driver.getPageSource());
        } catch (NoSuchWindowException e) {
            throw new BrowserException("Can't access browser.", e);
        } finally {
            readLock.unlock();
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

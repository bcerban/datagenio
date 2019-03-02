package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.NetworkProxy;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.EventTriggerException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DrivenBrowserTest {

    private static String TEST_URI = "http://test.com";

    private FirefoxDriver driver;
    private NetworkProxy proxy;
    private DrivenBrowser browser;
    private WebDriver.Navigation navigation;
    private WebDriver.Options options;
    private WebDriver.Window window;
    private WebDriver.Timeouts timeouts;

    @Before
    public void setUp() {
        navigation = mock(WebDriver.Navigation.class);
        options = mock(WebDriver.Options.class);
        window = mock(WebDriver.Window.class);
        timeouts = mock(WebDriver.Timeouts.class);
        driver = mock(FirefoxDriver.class);
        proxy = mock(NetworkProxy.class);

        when(driver.navigate()).thenReturn(navigation);
        when(driver.manage()).thenReturn(options);
        when(options.window()).thenReturn(window);
        when(options.timeouts()).thenReturn(timeouts);

        browser = new DrivenBrowser(driver, proxy);
    }

    @Test
    public void testNavigateTo() throws BrowserException {
        URI uri = URI.create(TEST_URI);
        browser.navigateTo(uri);

        verify(driver, times(1)).navigate();
        verify(navigation, times(1)).to(anyString());
    }

    @Test
    public void testTakeScreenShot() throws IOException {
        File screenShot = File.createTempFile("browserTest", ".png");
        screenShot.deleteOnExit();

        when(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE)).thenReturn(screenShot);
        assertEquals(screenShot, browser.getScreenShotFile());
    }

    @Test
    public void testBack() throws BrowserException {
        browser.back();

        verify(driver, times(1)).navigate();
        verify(navigation, times(1)).back();
    }

    @Test(expected = BrowserException.class)
    public void testBackWithWebDriverException() throws BrowserException {
        doThrow(new WebDriverException()).when(navigation).back();
        browser.back();

        verify(driver, times(1)).navigate();
        verify(navigation, times(1)).back();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerEventElementNotFound() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(driver.findElement(any(By.class))).thenReturn(null);
        browser.triggerEvent(event, null);

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerClickableEventStale() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.CLICK);
        doThrow(new StaleElementReferenceException("")).when(element).click();

        browser.triggerEvent(event, null);

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerClickableEventNotInteractable() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.CLICK);
        when(driver.findElement(any(By.class))).thenReturn(element);
        doThrow(new ElementNotInteractableException("")).when(element).click();

        browser.triggerEvent(event, null);

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test
    public void testTriggerClickableEvent() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.CLICK);
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(driver.getPageSource()).thenReturn("");

        browser.triggerEvent(event, null);

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerSubmitEventStale() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);
        Element source = mock(Element.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.SUBMIT);
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(event.getSource()).thenReturn(source);
        when(source.is("button")).thenReturn(true);
        when(source.attr("type")).thenReturn("submit");
        when(source.tagName()).thenReturn("#root");
        doThrow(new StaleElementReferenceException("")).when(element).click();

        browser.triggerEvent(event, new ArrayList<>());

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerSubmitEventNotInteractable() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);
        Element source = mock(Element.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.SUBMIT);
        when(event.getSource()).thenReturn(source);
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(source.is("button")).thenReturn(true);
        when(source.attr("type")).thenReturn("submit");
        when(source.tagName()).thenReturn("#root");
        doThrow(new ElementNotInteractableException("")).when(element).click();

        browser.triggerEvent(event, new ArrayList<>());

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test
    public void testTriggerSubmitEventNoSuchElement() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);
        Element source = mock(Element.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.SUBMIT);
        when(event.getSource()).thenReturn(source);
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(source.is("button")).thenReturn(true);
        when(source.attr("type")).thenReturn("submit");
        when(source.tagName()).thenReturn("#root");
        doThrow(new NoSuchElementException("")).when(element).click();

        browser.triggerEvent(event, new ArrayList<>());

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
        verify(element, times(1)).submit();
    }

    @Test
    public void testTriggerSubmitEvent() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);
        Element source = mock(Element.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.SUBMIT);
        when(event.getSource()).thenReturn(source);
        when(driver.findElement(any(By.class))).thenReturn(element);
        when(driver.getPageSource()).thenReturn("");
        when(source.is("button")).thenReturn(true);
        when(source.attr("type")).thenReturn("submit");
        when(source.tagName()).thenReturn("#root");

        browser.triggerEvent(event, new ArrayList<>());

        verify(event, times(2)).getEventIdentifier();
        verify(event, times(1)).getXpath();
        verify(element, times(1)).click();
    }

    @Test(expected = UnsupportedEventTypeException.class)
    public void testTriggerEventUnsupportedType() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        String xpath = "/html/body/span[1]/button";
        when(event.getXpath()).thenReturn(xpath);
        when(event.getEventIdentifier()).thenReturn(xpath);
        when(event.getEventType()).thenReturn(Eventable.EventType.HOVER);
        when(driver.findElement(any(By.class))).thenReturn(element);

        browser.triggerEvent(event, new ArrayList<>());

        verify(event, times(1)).getEventIdentifier();
        verify(event, times(1)).getXpath();
    }
}

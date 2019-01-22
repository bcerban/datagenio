package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.NetworkProxy;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.EventTriggerException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DrivenBrowserTest {

    private static String TEST_URI = "http://test.com";

    private FirefoxDriver driver;
    private NetworkProxy proxy;
    private DrivenBrowser browser;
    private WebDriver.Navigation navigation;

    @Before
    public void setUp() {
        navigation = mock(WebDriver.Navigation.class);
        driver = mock(FirefoxDriver.class);
        proxy = mock(NetworkProxy.class);
        browser = new DrivenBrowser(driver, proxy);

        when(this.driver.navigate()).thenReturn(this.navigation);
    }

    @Test
    public void testNavigateTo() throws BrowserException {
        URI uri = URI.create(TEST_URI);
        this.browser.navigateTo(uri);

        verify(this.driver, times(1)).navigate();
        verify(this.navigation, times(1)).to(anyString());
    }

    @Test
    public void testTakeScreenShot() throws IOException {
        File screenShot = File.createTempFile("browserTest", ".png");
        screenShot.deleteOnExit();

        when(((TakesScreenshot)this.driver).getScreenshotAs(OutputType.FILE)).thenReturn(screenShot);
        assertEquals(screenShot, this.browser.getScreenShotFile());
    }

    @Test
    public void testBack() throws BrowserException {
        this.browser.back();

        verify(this.driver, times(1)).navigate();
        verify(this.navigation, times(1)).back();
    }

    @Test(expected = BrowserException.class)
    public void testBackWithWebDriverException() throws BrowserException {
        doThrow(new WebDriverException()).when(this.navigation).back();
        this.browser.back();

        verify(this.driver, times(1)).navigate();
        verify(this.navigation, times(1)).back();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerEventElementNotFound() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(this.driver.findElement(any(By.class))).thenReturn(null);
        this.browser.triggerEvent(event, null);

        verify(event, times(2)).getIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerClickableEventStale() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.click);
        when(this.driver.findElement(any(By.class))).thenReturn(element);
        doThrow(new StaleElementReferenceException("")).when(element).click();

        this.browser.triggerEvent(event, null);

        verify(event, times(2)).getIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerClickableEventNotInteractable() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.click);
        when(this.driver.findElement(any(By.class))).thenReturn(element);
        doThrow(new ElementNotInteractableException("")).when(element).click();

        this.browser.triggerEvent(event, null);

        verify(event, times(2)).getIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test
    public void testTriggerClickableEvent() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.click);
        when(this.driver.findElement(any(By.class))).thenReturn(element);
        when(this.driver.getPageSource()).thenReturn("");

        this.browser.triggerEvent(event, null);

        verify(event, times(1)).getIdentifier();
        verify(event, times(1)).getXpath();
        verify(this.driver, times(1)).getPageSource();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerSubmitEventStale() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.submit);
        when(this.driver.findElement(any(By.class))).thenReturn(element);
        doThrow(new StaleElementReferenceException("")).when(element).submit();

        this.browser.triggerEvent(event, new HashMap<>());

        verify(event, times(2)).getIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerSubmitEventNotInteractable() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.submit);
        when(this.driver.findElement(any(By.class))).thenReturn(element);
        doThrow(new ElementNotInteractableException("")).when(element).submit();

        this.browser.triggerEvent(event, new HashMap<>());

        verify(event, times(2)).getIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test(expected = EventTriggerException.class)
    public void testTriggerSubmitEventNoSuchElement() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.submit);
        when(this.driver.findElement(any(By.class))).thenReturn(element);
        doThrow(new NoSuchElementException("")).when(element).submit();

        this.browser.triggerEvent(event, new HashMap<>());

        verify(event, times(2)).getIdentifier();
        verify(event, times(1)).getXpath();
    }

    @Test
    public void testTriggerSubmitEvent() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.submit);
        when(this.driver.findElement(any(By.class))).thenReturn(element);
        when(this.driver.getPageSource()).thenReturn("");

        this.browser.triggerEvent(event, new HashMap<>());

        verify(event, times(1)).getIdentifier();
        verify(event, times(1)).getXpath();
        verify(element, times(1)).submit();
        verify(this.driver, times(1)).getPageSource();
    }

    @Test(expected = UnsupportedEventTypeException.class)
    public void testTriggerEventUnsupportedType() throws UnsupportedEventTypeException, EventTriggerException {
        Eventable event = mock(Eventable.class);
        WebElement element = mock(WebElement.class);

        when(event.getXpath()).thenReturn("/html/body/span[1]/button");
        when(event.getEventType()).thenReturn(Eventable.EventType.hover);
        when(this.driver.findElement(any(By.class))).thenReturn(element);

        this.browser.triggerEvent(event, new HashMap<>());

        verify(event, times(1)).getIdentifier();
        verify(event, times(1)).getXpath();
    }
}

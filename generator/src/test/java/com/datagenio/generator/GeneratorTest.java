package com.datagenio.generator;

import com.datagenio.crawler.api.Crawler;
import com.datagenio.crawler.api.EventFlowGraph;

import com.datagenio.generator.api.GraphConverter;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.storage.api.ReadAdapter;
import com.datagenio.storage.api.WriteAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GeneratorTest {
    private GraphConverter converter;
    private Crawler crawler;
    private Generator generator;
    private ReadAdapter readAdapter;
    private WriteAdapter writeAdapter;

    @Before
    public void setUp() {
        this.converter = mock(GraphConverter.class);
        this.crawler = mock(Crawler.class);
        this.readAdapter = mock(ReadAdapter.class);
        this.writeAdapter = mock(WriteAdapter.class);
        this.generator = new Generator(this.crawler, this.converter, this.readAdapter, this.writeAdapter);
    }

    @Test
    public void testCrawlSite() {
        EventFlowGraph eventFlowGraph = mock(EventFlowGraph.class);
        doReturn(eventFlowGraph).when(crawler).crawl();

        generator.crawlSite();

        verify(crawler, times(1)).crawl();
        verify(writeAdapter, times(1)).save(eventFlowGraph);
    }

    @Test
    public void testGenerateWebModel() {
        EventFlowGraph eventFlowGraph = mock(EventFlowGraph.class);
        WebFlowGraph webFlowGraph = mock(WebFlowGraph.class);

        when(this.crawler.crawl()).thenReturn(eventFlowGraph);
        when(this.converter.convert(eventFlowGraph)).thenReturn(webFlowGraph);

        this.generator.generateWebModel();
    }
}

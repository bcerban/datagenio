package com.datagenio.generator;

import com.datagenio.context.Context;
import com.datagenio.crawler.api.Crawler;
import com.datagenio.crawler.api.EventFlowGraph;

import com.datagenio.generator.api.GraphConverter;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.storageapi.ReadAdapter;
import com.datagenio.storageapi.WriteAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GeneratorTest {
    private Context context;
    private GraphConverter converter;
    private Crawler crawler;
    private Generator generator;
    private ReadAdapter readAdapter;
    private WriteAdapter writeAdapter;

    @Before
    public void setUp() {
        context = mock(Context.class);
        converter = mock(GraphConverter.class);
        crawler = mock(Crawler.class);
        readAdapter = mock(ReadAdapter.class);
        writeAdapter = mock(WriteAdapter.class);
        generator = new Generator(context, crawler, converter, readAdapter, writeAdapter);
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

        when(crawler.crawl()).thenReturn(eventFlowGraph);
        when(converter.convert(eventFlowGraph)).thenReturn(webFlowGraph);

        generator.generateWebModel();
    }
}

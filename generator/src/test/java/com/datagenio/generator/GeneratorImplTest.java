package com.datagenio.generator;

import com.datagenio.context.Context;
import com.datagenio.crawler.api.Crawler;
import com.datagenio.crawler.api.EventFlowGraph;

import com.datagenio.generator.api.GraphConverter;
import com.datagenio.model.WebFlowGraph;
import com.datagenio.storageapi.ReadAdapter;
import com.datagenio.storageapi.WriteAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GeneratorImplTest {
    private Context context;
    private GraphConverter converter;
    private Crawler crawler;
    private GeneratorImpl generator;
    private ReadAdapter readAdapter;
    private WriteAdapter writeAdapter;

    @Before
    public void setUp() {
        context = mock(Context.class);
        converter = mock(GraphConverter.class);
        crawler = mock(Crawler.class);
        readAdapter = mock(ReadAdapter.class);
        writeAdapter = mock(WriteAdapter.class);
        generator = new GeneratorImpl(context, crawler, converter, readAdapter, writeAdapter);
    }

    @Test
    public void testGenerateWebModel() {
        EventFlowGraph eventFlowGraph = mock(EventFlowGraph.class);
        WebFlowGraph webFlowGraph = mock(WebFlowGraph.class);

        when(context.continueExistingModel()).thenReturn(true);
        when(readAdapter.loadWebModel()).thenReturn(webFlowGraph);
        when(crawler.crawl()).thenReturn(eventFlowGraph);
        when(converter.convert(eventFlowGraph, webFlowGraph)).thenReturn(webFlowGraph);

        generator.generateWebModel();
    }
}

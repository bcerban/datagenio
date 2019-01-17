package com.datagenio.generator;

import com.datagenio.crawler.Crawler;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.GraphConverter;

import com.datagenio.model.api.WebFlowGraph;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class GeneratorTest {
    private GraphConverter converter;
    private Crawler crawler;
    private Generator generator;

    @Before
    public void setUp() {
        this.converter = mock(GraphConverter.class);
        this.crawler = mock(Crawler.class);
        this.generator = new Generator(this.crawler, this.converter);
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

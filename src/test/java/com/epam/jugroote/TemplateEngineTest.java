package com.epam.jugroote;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TemplateEngineTest {

    ViewLoader viewLoader = mock(ViewLoader.class);
    TemplateEngine engine = new TemplateEngine(viewLoader);

    @Test
    public void testGet() {
        String name = "simple";
        GrutView view = mock(GrutView.class);
        when(view.var(any(), any())).thenReturn(view);
        when(viewLoader.get(name)).thenReturn(view);
        GrutView simple = engine.get(name);
        assertEquals(view, simple);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull() throws Exception {
        engine.get("simpleTest");
    }
}
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
        JugView view = mock(JugView.class);
        when(viewLoader.get(name)).thenReturn(view);
        JugView simple = engine.get(name);
        assertEquals(view, simple);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull() throws Exception {
        engine.get("simpleTest");
    }
}
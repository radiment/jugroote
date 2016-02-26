package com.epam.jugroote;

import org.junit.Test;

import static org.junit.Assert.*;

public class TemplateEngineTest {

    private TemplateEngine engine = new TemplateEngine();

    @Test
    public void testWriteTo() throws Exception {
        JugView view = engine.get("simpleTest");
        assertNotNull(view);
    }
}
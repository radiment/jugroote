package com.epam.jugroote.loader;

import com.epam.jugroote.JugView;
import org.junit.Test;

import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GroovyHtmlViewLoaderTest {

    @Test
    public void testGet() throws Exception {
        GroovyHtmlViewLoader viewLoader = new GroovyHtmlViewLoader(
                GroovyViewLoaderTest.class.getResource("/simpleTest.ghtml").toURI());
        JugView view = viewLoader.get("simpleTest");
        assertNotNull(view);
        StringWriter writer = new StringWriter();
        view.writeTo(writer);
        String result = writer.toString();
        assertThat(result, startsWith("<!DOCTYPE html>"));
        assertThat(result, containsString("<title>ghtml</title>"));
        System.out.println(result);
    }
}
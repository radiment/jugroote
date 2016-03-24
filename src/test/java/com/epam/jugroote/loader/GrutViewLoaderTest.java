package com.epam.jugroote.loader;

import com.epam.jugroote.JugView;
import org.junit.Test;

import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GrutViewLoaderTest {

    @Test
    public void testGet() throws Exception {
        GrutViewLoader viewLoader = new GrutViewLoader(
                GroovyViewLoaderTest.class.getResource("/simpleTest.gr").toURI());
        JugView view = viewLoader.get("simpleTest");
        view.var("body", "test");
        assertNotNull(view);
        StringWriter writer = new StringWriter();
        view.writeTo(writer);
        String result = writer.toString();
        assertThat(result, startsWith("<!DOCTYPE html>"));
        assertThat(result, containsString("<title>ghtml</title>"));
        System.out.println(result);
    }
}
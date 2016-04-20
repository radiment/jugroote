package com.epam.jugroote;

import groovy.lang.GroovyCodeSource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class GrutViewTest {

    private GrutTemplate template;

    @Before
    public void setUp() throws Exception {
        GroovyCodeSource codeSource = new GroovyCodeSource(this.getClass().getResource("/simpleTest.groovy"));
        template = new GrutTemplate("test", codeSource);
    }

    @Test
    public void testWrite() throws IOException {
        StringWriter writer = new StringWriter();
        new GrutView(template).writeTo(writer);
        String result = writer.toString();
        assertFalse("Writer is empty", result.isEmpty());
        assertThat(result, startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void testWithoutBody() throws IOException {
        StringWriter writer = new StringWriter();
        new GrutView(template).writeTo(writer);
        String result = writer.toString();
        assertThat(result, not(containsString("<body>")));
    }

    @Test
    public void testWithBody() throws IOException {
        StringWriter writer = new StringWriter();
        new GrutView(template).var("body", "body").writeTo(writer);
        String result = writer.toString();
        assertThat(result, containsString("<body>"));
    }

}
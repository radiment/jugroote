package com.epam.jugroote.loader;

import com.epam.jugroote.GrutView;
import org.junit.Test;

import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

public class GroovyViewLoaderTest {

    @Test
    public void testGet() throws Exception {
        GroovyViewLoader viewLoader = new GroovyViewLoader(
                GroovyViewLoaderTest.class.getResource("/simpleTest.groovy").toURI());
        GrutView view = viewLoader.get("simpleTest");
        assertNotNull(view);
        StringWriter writer = new StringWriter();
        view.writeTo(writer);
        String result = writer.toString();
        assertThat(result, startsWith("<!DOCTYPE html>"));
        assertThat(result, containsString("<title>groovy</title>"));
        System.out.println(result);
    }
}
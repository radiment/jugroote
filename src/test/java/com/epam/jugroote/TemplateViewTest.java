package com.epam.jugroote;

import com.epam.jugroote.loader.GrutViewLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

public class TemplateViewTest {
    TemplateEngine engine;

    @Before
    public void setUp() throws URISyntaxException {
        engine = new TemplateEngine(new GrutViewLoader(TemplateViewTest.class.getResource("/simpleTest.gr").toURI()));
    }

    @Test
    public void testTemplate() throws IOException {
        GrutView login = engine.get("login");
        StringWriter writer = new StringWriter();
        login.writeTo(writer);
        System.out.println(writer);
        String result = writer.toString();
        assertThat(result, startsWith("<!DOCTYPE html>"));
    }
}

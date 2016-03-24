package com.epam.jugroote.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.Test;

import java.io.Writer;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class GrutConverterTest {

    @Test
    public void testConvertToScript() throws Exception {
        String result = GrutConverter.convertToScript(this.getClass().getResourceAsStream("/simpleTest.gr"));
        try {
            Binding binding = new Binding();
            binding.setVariable("_writer", mock(Writer.class));
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(result);
        } catch (Exception e) {
            System.out.println(result);
            fail(e.getMessage());
        }
        System.out.println(result);
    }
}
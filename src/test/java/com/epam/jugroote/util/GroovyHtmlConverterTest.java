package com.epam.jugroote.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Test;

import java.io.Writer;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class GroovyHtmlConverterTest {

    @Test
    public void testConvertToScript() throws Exception {
        String result = GroovyHtmlConverter.convertToScript(this.getClass().getResourceAsStream("/simpleTest.ghtml"));
        try {
            Binding binding = new Binding();
            binding.setVariable("_writer", mock(Writer.class));
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(result);
        } catch (CompilationFailedException e) {
            fail(e.getMessage());
        }
        System.out.println(result);
    }
}
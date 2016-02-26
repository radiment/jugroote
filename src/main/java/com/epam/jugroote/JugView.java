package com.epam.jugroote;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.IOException;
import java.io.Writer;

public class JugView {

    private JugTemplate template;
    Binding binding = new Binding();

    public JugView(JugTemplate template) {
        this.template = template;
    }

    public void writeTo(Writer writer) throws IOException {
        binding.setVariable("_writer", writer);
        Script script = new GroovyShell(binding).parse(template.getCodeSource());
        script.run();
    }

    public JugView var(String name, Object value) {
        binding.setVariable(name, value);
        return this;
    }

    public JugView prop(String name, Object value) {
        binding.setProperty(name, value);
        return this;
    }
}

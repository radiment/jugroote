package com.epam.jugroote;

import com.epam.jugroote.util.PropertyUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class GrutView {

    private GrutTemplate template;
    Binding binding = new Binding();
    CompilerConfiguration configuration;
    private Script script;

    public GrutView(GrutTemplate template) {
        this.template = template;
        this.configuration = new CompilerConfiguration();
    }

    public GrutView(GrutTemplate template, CompilerConfiguration configuration) {
        this.template = template;
        this.configuration = configuration;
    }

    public void writeTo(Writer writer) throws IOException {
        binding.setVariable("_writer", writer);
        Script script = getScript();
        script.setBinding(binding);
        script.run();
    }

    private Script getScript() {
        if (script == null) {
            script = new GroovyShell(configuration).parse(template.getCodeSource());
        }
        return script;
    }

    public GrutView var(String name, Object value) {
        binding.setVariable(name, value);
        return this;
    }

    public GrutView vars(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            binding.setVariable(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public GrutView props(Object props) {
        PropertyUtil.getPropertiesFor(props.getClass()).forEach(
                (s, getter) -> binding.setVariable(s, getter.get(props)));
        return this;
    }
}

package com.epam.jugroote.loader;

import com.epam.jugroote.GrutTemplate;
import com.epam.jugroote.GrutView;
import com.epam.jugroote.ViewLoader;
import com.epam.jugroote.util.ConfigurationException;
import com.epam.jugroote.util.GrutConverter;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.IOException;
import java.net.URI;

public class GrutViewLoader implements ViewLoader {

    public static final String EXTENSION = ".gr";
    private URI baseUri;
    private String scriptBaseClass = TemplateScriptBase.class.getName();

    public GrutViewLoader(URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public GrutView get(String name) {
        try {
            URI uri = baseUri.resolve(name + EXTENSION);
            String script = GrutConverter.convertToScript(uri.toURL().openStream());
            GroovyCodeSource source = new GroovyCodeSource(script, name + EXTENSION, GroovyShell.DEFAULT_CODE_BASE);
            return new GrutView(new GrutTemplate(name, source), getCompilerConfiguration());
        } catch (IOException e) {
            throw new ConfigurationException("Read script error: ", e);
        }
    }

    public GrutViewLoader setScriptBase(String scriptBase) {
        this.scriptBaseClass = scriptBase;
        return this;
    }

    public GrutViewLoader setScriptBaseClass(Class scriptBaseClass) {
        this.scriptBaseClass = scriptBaseClass.getName();
        return this;
    }

    protected CompilerConfiguration getCompilerConfiguration() {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(scriptBaseClass);
        return configuration;
    }
}

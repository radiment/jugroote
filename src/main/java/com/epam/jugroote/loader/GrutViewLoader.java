package com.epam.jugroote.loader;

import com.epam.jugroote.JugTemplate;
import com.epam.jugroote.JugView;
import com.epam.jugroote.ViewLoader;
import com.epam.jugroote.util.ConfigurationException;
import com.epam.jugroote.util.GrutConverter;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.net.URI;

public class GrutViewLoader implements ViewLoader {

    public static final String EXTENSION = ".gr";
    private URI baseUri;

    public GrutViewLoader(URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public JugView get(String name) {
        try {
            URI uri = baseUri.resolve(name + EXTENSION);
            String script = GrutConverter.convertToScript(uri.toURL().openStream());
            GroovyCodeSource source = new GroovyCodeSource(script, name + EXTENSION, GroovyShell.DEFAULT_CODE_BASE);
            return new JugView(new JugTemplate(name, source));
        } catch (IOException e) {
            throw new ConfigurationException("Read script error: ", e);
        }
    }
}

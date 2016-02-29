package com.epam.jugroote.loader;

import com.epam.jugroote.JugTemplate;
import com.epam.jugroote.JugView;
import com.epam.jugroote.ViewLoader;
import com.epam.jugroote.util.ConfigurationException;
import com.epam.jugroote.util.GroovyHtmlConverter;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.net.URI;

public class GroovyHtmlViewLoader implements ViewLoader {

    private URI baseUri;

    public GroovyHtmlViewLoader(URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public JugView get(String name) {
        try {
            URI uri = baseUri.resolve(name + ".ghtml");
            String script = GroovyHtmlConverter.convertToScript(uri.toURL().openStream());
            GroovyCodeSource source = new GroovyCodeSource(script, name + ".ghtml", GroovyShell.DEFAULT_CODE_BASE);
            return new JugView(new JugTemplate(name, source));
        } catch (IOException e) {
            throw new ConfigurationException("Read script error: ", e);
        }
    }
}

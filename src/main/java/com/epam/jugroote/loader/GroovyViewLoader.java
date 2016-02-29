package com.epam.jugroote.loader;

import com.epam.jugroote.JugTemplate;
import com.epam.jugroote.JugView;
import com.epam.jugroote.ViewLoader;
import com.epam.jugroote.util.ConfigurationException;
import groovy.lang.GroovyCodeSource;

import java.io.IOException;
import java.net.URI;

public class GroovyViewLoader implements ViewLoader {

    private URI baseUri;

    public GroovyViewLoader(URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public JugView get(String name) {
        try {
            GroovyCodeSource script = new GroovyCodeSource(baseUri.resolve(name + ".groovy"));
            return new JugView(new JugTemplate(name, script));
        } catch (IOException e) {
            throw new ConfigurationException("Read script error: ", e);
        }
    }
}

package com.epam.jugroote.loader;

import com.epam.jugroote.GrutTemplate;
import com.epam.jugroote.GrutView;
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
    public GrutView get(String name) {
        try {
            GroovyCodeSource script = new GroovyCodeSource(baseUri.resolve(name + ".groovy"));
            return new GrutView(new GrutTemplate(name, script));
        } catch (IOException e) {
            throw new ConfigurationException("Read script error: ", e);
        }
    }
}

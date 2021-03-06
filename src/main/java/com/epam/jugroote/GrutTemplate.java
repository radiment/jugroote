package com.epam.jugroote;

import groovy.lang.GroovyCodeSource;

public class GrutTemplate {
    private String name;
    private GroovyCodeSource codeSource;

    public GrutTemplate(String name, GroovyCodeSource codeSource) {
        this.name = name;
        this.codeSource = codeSource;
    }

    public String getName() {
        return name;
    }

    public GroovyCodeSource getCodeSource() {
        return codeSource;
    }
}

package com.epam.jugroote;

public class TemplateEngine {

    private ViewLoader loader;

    public TemplateEngine(ViewLoader loader) {
        this.loader = loader;
    }

    public JugView get(String name) {
        JugView view = loader.get(name);
        if (view == null) {
            throw new IllegalArgumentException("Template with name '" + name + "' not found");
        }
        return view;
    }
}

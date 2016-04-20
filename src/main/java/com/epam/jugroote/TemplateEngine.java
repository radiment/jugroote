package com.epam.jugroote;

public class TemplateEngine {

    private ViewLoader loader;

    public TemplateEngine(ViewLoader loader) {
        this.loader = loader;
    }

    public GrutView get(String name) {
        GrutView view = loader.get(name);
        if (view == null) {
            throw new IllegalArgumentException("Template with name '" + name + "' not found");
        }
        return view.var("_engine", this);
    }
}

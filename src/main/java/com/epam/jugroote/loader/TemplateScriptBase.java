package com.epam.jugroote.loader;

import com.epam.jugroote.TemplateEngine;
import groovy.lang.Script;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public abstract class TemplateScriptBase extends Script {
    public void template(Map<String, Object> params, String templateName) throws IOException {
        TemplateEngine engine = getEngine();
        engine.get(templateName).vars(params).writeTo(getWriter());
    }

    public TemplateEngine getEngine() {
        return (TemplateEngine) getBinding().getVariable("_engine");
    }

    public Writer getWriter() {
        return (Writer) getBinding().getVariable("_writer");
    }
}

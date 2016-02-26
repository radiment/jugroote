package com.epam.jugroote.util;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class PropertyUtilTest {

    @Test
    public void testField() throws Exception {
        TestClass testField = new TestClass();
        Map<String, PropertyUtil.Getter> result = PropertyUtil.getPropertiesFor(testField.getClass());
        assertNotNull(result.get("field"));
        assertEquals("field", result.get("field").get(testField));
    }

    @Test
    public void testMethod() {
        TestClass testClass = new TestClass();
        Map<String, PropertyUtil.Getter> result = PropertyUtil.getPropertiesFor(testClass.getClass());
        assertNotNull(result.get("body"));
        assertNotNull(result.get("bool"));
        assertNull(result.get("private"));
        assertEquals("body-method", result.get("body").get(testClass));
        assertTrue((Boolean) result.get("bool").get(testClass));
    }

    class TestClass {
        String body = "body";
        private String field = "field";

        public boolean isBool() {
            return true;
        }

        public String getBody() {
            return body + "-method";
        }

        private String getPrivate() {
            return "private";
        }
    }

}
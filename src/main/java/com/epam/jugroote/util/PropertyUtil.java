package com.epam.jugroote.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyUtil {

    private static final Map<Class, Map<String, Getter>> CACHE = new ConcurrentHashMap<>();
    public static final String PREFIX_GET = "get";
    public static final String PREFIX_IS = "is";

    public static Map<String, Getter> getPropertiesFor(Class<?> clazz) {
        Map<String, Getter> result = CACHE.get(clazz);
        if (result == null) {
            result = new HashMap<>();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                String name = method.getName();
                String prefix = boolean.class.equals(method.getReturnType()) ? PREFIX_IS : PREFIX_GET;
                int length = prefix.length();
                if (name.startsWith(prefix) && method.getParameterCount() == 0) {
                    name = name.substring(length, length + 1).toLowerCase()
                            + name.substring(length + 1);
                    result.put(name, new MethodGetter(name, method));
                }
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Getter getter = result.get(field.getName());
                if (getter == null) {
                    result.put(field.getName(), new FieldGetter(field));
                }
            }
            CACHE.putIfAbsent(clazz, result);
        }
        return result;
    }

    static class FieldGetter implements Getter {

        Field field;

        public FieldGetter(Field field) {
            this.field = field;
        }

        @Override
        public Object get(Object object) {
            try {
                field.setAccessible(true);
                Object result = field.get(object);
                field.setAccessible(false);
                return result;
            } catch (IllegalAccessException e) {
                throw new ConfigurationException(
                        "Error get property " + field.getName() + " with reason: " + e.getMessage() , e);
            }
        }
    }



    static class MethodGetter implements Getter {
        String name;
        Method method;

        MethodGetter(String name, Method method) {
            this.name = name;
            this.method = method;
        }

        @Override
        public Object get(Object object) {
            try {
                return method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConfigurationException("Error get property " + name + " with reason: " + e.getMessage() , e);
            }
        }
    }

    public interface Getter {
        Object get(Object object);
    }
}

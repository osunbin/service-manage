package com.bin.client.util;

import com.bin.webmonitor.common.util.StringUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectUtil {


    public static Object getFieldValue(Object obj, String fieldName) {
        if (null == obj || StringUtil.isBlank(fieldName)) {
            return null;
        }
        Field field = getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName);
        return getFieldValue(obj, field);
    }

    public static Field getField(Class<?> beanClass, String name) throws SecurityException {
        final Field[] fields = getFieldsDirectly(beanClass, true);
        for (Field field : fields) {
            if (name.equals(getFieldName(field))) {
                return field;
            }
        }
        return null;
    }


    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                int length = allFields.length;
                allFields = Arrays.copyOf(allFields, length + declaredFields.length);
                System.arraycopy(declaredFields, 0, allFields, length, declaredFields.length);
            }
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    public static String getFieldName(Field field) {
        if (null == field) {
            return null;
        }
        return field.getName();
    }

    public static Object getFieldValue(Object obj, Field field) {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            obj = null;
        }
        setAccessible(field);
        Object result;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            String exceptionMsg = String.format("IllegalAccess for %s.%s", field.getDeclaringClass(), field.getName());
            throw new RuntimeException(exceptionMsg, e);
        }
        return result;
    }

    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && !accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

}

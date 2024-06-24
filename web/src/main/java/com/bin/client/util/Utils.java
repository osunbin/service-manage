package com.bin.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Utils {
    private static final List<Class<?>> CLASS_CAN_BE_STRING = Arrays.asList(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            Character.class);

    public static Map<String, Field> getBeanPropertyFields(Class cl) {
        Map<String, Field> properties = new HashMap<>();
        for (; cl != null; cl = cl.getSuperclass()) {
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                properties.put(field.getName(), field);
            }
        }

        return properties;
    }

    public static <T> T mapToPojo(Map<String, Object> map, Class<T> cls) throws ReflectiveOperationException {
        T instance = cls.getDeclaredConstructor().newInstance();
        Map<String, Field> beanPropertyFields = getBeanPropertyFields(cls);
        for (Map.Entry<String, Field> entry : beanPropertyFields.entrySet()) {
            String name = entry.getKey();
            Field field = entry.getValue();
            Object mapObject = map.get(name);
            if (mapObject == null) {
                continue;
            }

            Type type = field.getGenericType();
            Object fieldObject = getFieldObject(mapObject, type);
            field.set(instance, fieldObject);
        }

        return instance;
    }

    private static Object getFieldObject(Object mapObject, Type fieldType) throws ReflectiveOperationException {
        if (fieldType instanceof Class<?>) {
            return convertClassType(mapObject, (Class<?>) fieldType);
        } else if (fieldType instanceof ParameterizedType) {
            return convertParameterizedType(mapObject, (ParameterizedType) fieldType);
        } else if (fieldType instanceof GenericArrayType
                || fieldType instanceof TypeVariable<?>
                || fieldType instanceof WildcardType) {
            // ignore these type currently
            return null;
        } else {
            throw new IllegalArgumentException("Unrecognized Type: " + fieldType.toString());
        }
    }
    private static Object convertClassType(Object mapObject, Class<?> type) throws ReflectiveOperationException {
        if (type.isPrimitive() || isAssignableFrom(type, mapObject.getClass())) {
            return mapObject;
        } else if (Objects.equals(type, String.class) && CLASS_CAN_BE_STRING.contains(mapObject.getClass())) {
            // auto convert specified type to string
            return mapObject.toString();
        } else if (mapObject instanceof Map) {
            return mapToPojo((Map<String, Object>) mapObject, type);
        } else {
            // type didn't match and mapObject is not another Map struct.
            // we just ignore this situation.
            return null;
        }
    }

    private static Object convertParameterizedType(Object mapObject, ParameterizedType type)
            throws ReflectiveOperationException {
        Type rawType = type.getRawType();
        if (!isAssignableFrom((Class<?>) rawType, mapObject.getClass())) {
            return null;
        }

        Type[] actualTypeArguments = type.getActualTypeArguments();
        if (isAssignableFrom(Map.class, (Class<?>) rawType)) {
            Map<Object, Object> map = (Map<Object, Object>)
                    mapObject.getClass().getDeclaredConstructor().newInstance();
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) mapObject).entrySet()) {
                Object key = getFieldObject(entry.getKey(), actualTypeArguments[0]);
                Object value = getFieldObject(entry.getValue(), actualTypeArguments[1]);
                map.put(key, value);
            }

            return map;
        } else if (isAssignableFrom(Collection.class, (Class<?>) rawType)) {
            Collection<Object> collection = (Collection<Object>)
                    mapObject.getClass().getDeclaredConstructor().newInstance();
            for (Object m : (Iterable<?>) mapObject) {
                Object ele = getFieldObject(m, actualTypeArguments[0]);
                collection.add(ele);
            }

            return collection;
        } else {
            // ignore other type currently
            return null;
        }
    }
    public static boolean isAssignableFrom(Class<?> superType, Class<?> targetType) {
        // any argument is null
        if (superType == null || targetType == null) {
            return false;
        }
        // equals
        if (Objects.equals(superType, targetType)) {
            return true;
        }
        // isAssignableFrom
        return superType.isAssignableFrom(targetType);
    }


    public static String getSimpleParaName(String paraName) {
        paraName = paraName.replaceAll(" ", "");
        if (paraName.indexOf(".") > 0) {
            //拆分成单个字母
            String[] pnAry = paraName.split("");
            //类全路径
            List<String> originalityList = new ArrayList<>();
            //类名
            List<String> replaceList = new ArrayList<>();

            String tempP = "";
            //方法泛型参数        java.util.List<java.lang.String>
            // originalityList：  java.util.List | java.lang.String
            // replaceList：      List | String
            for (int i = 0; i < pnAry.length; i++) {
                if (pnAry[i].equalsIgnoreCase("<")) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else if (pnAry[i].equalsIgnoreCase(">")) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else if (pnAry[i].equalsIgnoreCase(",")) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else if (i == pnAry.length - 1) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else {
                    if (!pnAry[i].equalsIgnoreCase("[") && !pnAry[i].equalsIgnoreCase("]")) {
                        tempP += pnAry[i];
                    }
                }
            }

            for (int i = 0; i < replaceList.size(); i++) {
                paraName = paraName.replaceAll(originalityList.get(i), replaceList.get(i));
            }
            return paraName;
        } else {
            return paraName;
        }
    }
}

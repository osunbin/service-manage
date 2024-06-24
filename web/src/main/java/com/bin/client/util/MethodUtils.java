package com.bin.client.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MethodUtils {

    public static String generateMethodKey(Method method, boolean withGenericParameters) {
        StringBuilder stringBuilder = new StringBuilder();

        //method.getDeclaringClass().getCanonicalName()  该方法所在类的全路径名   com.rpc.client.support.App
        stringBuilder.append(getSimpleTypeName(method.getDeclaringClass().getCanonicalName()))
                .append(".")
                .append(generateMethodSignature(method, withGenericParameters));
        return stringBuilder.toString();
    }

    /**
     * method.(parameters)
     *
     */
    public static String generateMethodSignature(Method method, boolean withGenericParameters) {
        StringBuilder stringBuilder = new StringBuilder()
                .append(method.getName())
                .append("(");
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (withGenericParameters) {
            int parametersLength = genericParameterTypes.length;
            for (int i = 0; i < parametersLength; i++) {
                stringBuilder.append(generateTypeNameWithGenericParameters(genericParameterTypes[i]));
                if (i < parametersLength - 1) {
                    stringBuilder.append(",");
                }
            }
        } else {
            int parametersLength = parameterTypes.length;
            for (int i = 0; i < parametersLength; i++) {
                stringBuilder.append(getSimpleTypeName(parameterTypes[i].getCanonicalName()));
                if (i < parametersLength - 1) {
                    stringBuilder.append(",");
                }
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    /**
     * Map<T,List<T>>
     *
     * @param type
     * @return
     */
    public static String generateTypeNameWithGenericParameters(Type type) {
        StringBuilder stringBuilder = new StringBuilder();
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            stringBuilder.append(getSimpleTypeName(rawType.getTypeName()))
                    .append("<");
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            int actualTypeArgumentsLength = actualTypeArguments.length;
            for (int i = 0; i < actualTypeArgumentsLength; i++) {
                stringBuilder.append(generateTypeNameWithGenericParameters(actualTypeArguments[i]));
                if (i < actualTypeArgumentsLength - 1) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(">");
        } else {
            stringBuilder.append(getSimpleTypeName(type.getTypeName()));
        }
        return stringBuilder.toString();
    }

    /**
     * 取简单类型名 例如：String
     *
     * @param typeName
     * @return
     */
    public static String getSimpleTypeName(String typeName) {
        int index = typeName.lastIndexOf(".");
        if (index > 0) {
            return typeName.substring(index + 1);
        }
        return typeName;
    }
}

package com.bin.webmonitor.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

    /**
     * 判断集合非空
     */
    public static <T> boolean isNotEmpty(Collection<T> c) {
        return null != c && c.size() > 0;
    }

    /**
     * 集合为空集
     */
    public static <T> boolean isEmpty(Collection<T> c) {
        return null == c || c.size() == 0;
    }

    /**
     * 判断map非空
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return null != map && map.size() > 0;
    }

    /**
     * 判断数组非空
     */
    public static boolean isNotEmpty(String... s) {
        return null != s && s.length > 0;
    }

    /**
     * 判断数组非空
     */
    public static boolean isNotEmpty(int... i) {
        return null != i && i.length > 0;
    }

    /**
     * 判断数组非空
     */
    public static boolean isNotEmpty(long... l) {
        return null != l && l.length > 0;
    }

    /**
     * 求集合大小
     */
    public static <T> int size(Collection<T> c) {
        if (null != c) {
            return c.size();
        }
        return 0;
    }

    /**
     * 求map大小
     */
    public static <K, V> int size(Map<K, V> map) {
        if (null != map) {
            return map.size();
        }
        return 0;
    }

    /**
     * 构造分页子集(防数组越界)
     *
     * @param pid   页号
     * @param psize 页大小
     */
    public static <T> List<T> subListForPage(List<T> list, int pid, int psize) {
        List<T> result = null;
        if (null != list && pid > 0 && psize > 0) {
            int listSize = list.size();
            if (listSize > 0) {
                int fromIndex = (pid - 1) * psize;
                int toIndex = pid * psize;
                // 防止越界
                if (fromIndex > listSize) {
                    fromIndex = listSize;
                }
                if (toIndex > listSize) {
                    toIndex = listSize;
                }
                result = list.subList(fromIndex, toIndex);
            }
        }
        return result;
    }

    /**
     * 数组转List
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> convertToList( T... t) {
        List<T> list = null;
        if (null != t && t.length > 0) {
            list = new ArrayList<>(t.length);
            Collections.addAll(list, t);
        }
        return list;
    }

    /**
     * 同value值的list转map
     */
    public static <K, V> Map<K, V> convertToMap(List<K> keyList, V value) {
        if (isNotEmpty(keyList)) {
            Map<K, V> map = new HashMap<>(keyList.size());
            for (K key : keyList) {
                map.put(key, value);
            }
            return map;
        }
        return null;
    }

    /**
     * 分页算法
     */
    public static <T> List<T> selectByPage(Integer page, Integer size, List<T> totalList) {
        List<T> resultList = new ArrayList<>();

        int totalSize = totalList.size() - 1;
        int start = size * page;
        int end = size * (page + 1);
        if (totalSize < start) {
            return resultList;
        }
        if (totalSize < end) {
            for (int index = start; index <= totalSize; ++index) {
                T t = totalList.get(index);
                resultList.add(t);
            }
            return resultList;
        }
        for (int index = start; index < end; ++index) {
            T t = totalList.get(index);
            resultList.add(t);
        }
        return resultList;
    }



    public static <T> T getFirstOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

}

package com.autohome.car.api.common;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ToolUtils {

    /**
     * 将一个列表按照给定的大小拆分成多个小的列表
     *
     * @param list 列表对象
     * @param size 每个小列表的大小
     * @return 拆分后的小列表集合
     */
    public static <T> List<List<T>> splitList(List<T> list, int size) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        int totalSize = list.size();
        int splitSize = (totalSize + size - 1) / size;
        List<List<T>> resultList = new ArrayList<>(splitSize);
        for (int i = 0; i < splitSize; i++) {
            int fromIndex = i * size;
            int toIndex = Math.min(fromIndex + size, totalSize);
            resultList.add(list.subList(fromIndex, toIndex));
        }
        return resultList;
    }

    public static <T extends Serializable> T deepCopy(T obj) {
        return (T) SerializationUtils.clone(obj);
    }

    public static <T extends Serializable> List<T> deepCopyList(List<T> src) {
        List<T> dest = new ArrayList<>(src.size());
        for (T obj : src) {
            dest.add(deepCopy(obj));
        }
        return dest;
    }
}

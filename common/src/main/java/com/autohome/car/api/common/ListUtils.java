package com.autohome.car.api.common;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public ListUtils() {
    }

    public static List groupListByQuantity(List list, int quantity) {
        if (list != null && list.size() != 0) {
            if (quantity <= 0) {
                new IllegalArgumentException("Wrong quantity.");
            }

            List wrapList = new ArrayList();

            for(int count = 0; count < list.size(); count += quantity) {
                wrapList.add(new ArrayList(list.subList(count, count + quantity > list.size() ? list.size() : count + quantity)));
            }

            return wrapList;
        } else {
            return list;
        }
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }
}

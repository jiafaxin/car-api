package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.common.ToolUtils;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.CompareJson;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CallBack {

    /**
     * 环境有： test, beta, pro 可以设置
     */
    default String getEnv() {
        return "test";
    }

    /**
     * @param paramContext 参数上下文
     * @param idsService   获取ids
     * @param path         对比url
     */
    void call(Param paramContext, IdsService idsService, String path);


    default void compareById(Param param, String path, List<Integer> ids) {
        List<CompletableFuture> tasks = new ArrayList<>();
        int i = 0;
        for (Integer id : ids) {
            String url = path + id;
            tasks.add(new CompareJson().exclude(param.getExclude()).compareUrlAsyncCommon(url, getEnv()));
            if (tasks.size() > 20) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i + "    "+url);
            }
        }
        if (!tasks.isEmpty()) {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        }
    }

    default void compareByIds(Param param, String path, List<Integer> ids) {
        Collections.shuffle(ids);
        List<List<Integer>> lists = ToolUtils.splitList(ids, param.getSlice());
        System.out.println("总共有：" + lists.size());
        int i = 0;
        List<CompletableFuture> tasks = new ArrayList<>();
        for (List<Integer> list : lists) {
            String url = path + Joiner.on(",").join(list);
            tasks.add(new CompareJson().exclude(param.getExclude()).compareUrlAsyncCommon(url, getEnv()));
            if (tasks.size() > 20) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i + "    "+url);
            }
        }
        if (!tasks.isEmpty()) {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        }
    }

    default void compareByIdString(Param param, String path, List<String> ids) {
        System.out.println("总共有：" + ids.size());
        List<CompletableFuture> tasks = new ArrayList<>();
        int i = 0;
        for (String id : ids) {
            String url = path + id;
            tasks.add(new CompareJson().exclude(param.getExclude()).compareUrlAsyncCommon(url, getEnv()));
            if (tasks.size() > 20) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i + "    "+url);
            }
        }
        if (!tasks.isEmpty()) {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        }
    }

    default void compare(Param param, String path) {
        //System.out.println("总共有 1 条:" + path);
        new CompareJson().exclude(param.getExclude()).compareUrlAsyncCommon(path, getEnv());
    }

    default <T> void compareCustom(Param param, String path, List<T> rows){
        List<List<T>> lists = ToolUtils.splitList(rows, param.getSlice());
        System.out.println("总共有：" + lists.size());
        int i = 0;
        List<CompletableFuture> tasks = new ArrayList<>();
        String url = "";
        for (List<T> list : lists) {
            for(T item : list){
                url = buildUrl(param, path, item);
                tasks.add(new CompareJson().exclude(param.getExclude()).compareUrlAsyncCommon(url, getEnv()));
            }
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            tasks.clear();
            i++;
            if(i % 100 ==0){
                System.out.println(i + "  " + url);
            }
        }
        if (!tasks.isEmpty()) {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        }

    }

    default <T> String buildUrl(Param param, String path, T item){
        return path;
    }

}

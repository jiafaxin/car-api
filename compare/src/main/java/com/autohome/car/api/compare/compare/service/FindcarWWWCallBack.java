package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.CompareJson;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class FindcarWWWCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<List<String>> inputArrays = new ArrayList<>();

        List<String> level = new ArrayList<>(getLeveID().keySet());
        inputArrays.add(level);
        List<String> fueltype = Arrays.asList("0","-1","1","2","3","4","5","6","7","8","9","701","801");
        inputArrays.add(fueltype);
        List<String> price = Arrays.asList("0_0");
        inputArrays.add(price);
        List<String> seat = Arrays.asList("3", "5", "6", "8", "7", "0");
        inputArrays.add(seat);
        List<String> drive = Arrays.asList("1", "2", "3", "4", "5", "0");
        inputArrays.add(drive);
        List<String> gear = Arrays.asList("101", "2", "5", "0", "1", "10", "8");
        inputArrays.add(gear);
        List<String> struct = Arrays.asList("1", "4", "5", "0", "8", "10", "3");
        inputArrays.add(struct);
        List<String> country = Arrays.asList("1", "4", "3", "0", "6", "10", "201");
        inputArrays.add(country);
        List<String> config = Arrays.asList("_2_", "_8_", "_3_", "_0_", "_6_", "_10_");
        inputArrays.add(config);
        List<String> attribute = Arrays.asList("0", "1", "3", "5");
        inputArrays.add(attribute);
        List<String> displacement = Arrays.asList("1.6_2.5", "2.9_4.0", "2.5_3.0_3.5_5.5", "1.2_1.8", "2.0_3.6", "0_0", "10_10");
        inputArrays.add(displacement);

        // 随机选择 5000 种情况的组合
        List<List<String>> randomCombinations = generateRandomCombinations(inputArrays, 50000);

        System.out.println("====测试case：" + randomCombinations.size() + "=====");

        int i = 0;
        List<CompletableFuture> tasks = new ArrayList<>();
        for (List<String> param : randomCombinations) {
            String[] paramArray = param.toArray(new String[0]);
            String url = String.format(
                    path + "&level=%s&fuel=%s&price=%s&seat=%s&drive=%s&gear=%s&structure=%s&country=%s&config=%s&attribute=%s&displacement=%s",
                    (Object[]) paramArray
            );
            tasks.add(new CompareJson().exclude(paramContext.getExclude()).compareUrlAsyncCommon(url, getEnv()));
            if (tasks.size() > 20) {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                tasks = new ArrayList<>();
            }
            i++;
            if (i % 1000 == 0) {
                System.out.println(i + "    "+url);
            }
        }
        if (!tasks.isEmpty()) {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        }

    }

    private static Map<String, Integer> getLeveID(){
        Map<String, Integer> levelMap = new HashMap<>();

        // 添加映射关系
        levelMap.put("car", 0);
        levelMap.put("a00", 1);
        levelMap.put("a0", 2);
        levelMap.put("a", 3);
        levelMap.put("b", 4);
        levelMap.put("c", 5);
        levelMap.put("d", 6);
        levelMap.put("s", 7);
        levelMap.put("mpv", 8);
        levelMap.put("suv", 9);
        levelMap.put("mb", 11);
        levelMap.put("qk", 13);
        levelMap.put("p", 14);
        levelMap.put("suva0", 16);
        levelMap.put("suva", 17);
        levelMap.put("suvb", 18);
        levelMap.put("suvc", 19);
        levelMap.put("suvd", 20);
        levelMap.put("mpva", 21);
        levelMap.put("mpvb", 22);
        levelMap.put("mpvc", 23);
        levelMap.put("mpvd", 24);
        return levelMap;
    }

    private static List<List<String>> generateRandomCombinations(List<List<String>> inputArrays, int count) {
        List<List<String>> randomCombinations = new ArrayList<>();
        Random random = ThreadLocalRandom.current();

        while (randomCombinations.size() < count) {
            List<String> combination = new ArrayList<>();
            for (List<String> array : inputArrays) {
                String randomItem = getRandomItem(array, random);
                combination.add(randomItem);
            }
            randomCombinations.add(combination);
        }

        return randomCombinations;
    }

    private static String getRandomItem(List<String> array, Random random) {
        int randomIndex = random.nextInt(array.size());
        return array.get(randomIndex);
    }
}

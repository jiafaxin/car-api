package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class SolrSeriesNumCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<List<String>> inputArrays = new ArrayList<>();
        List<String> energytype = Arrays.asList("4", "5", "6", "7","4_5", "4_6_7", "4_5", "6_7", "1_5");
        //List<String> energytype = Arrays.asList("4_5_6");
        inputArrays.add(energytype);
        List<String> price = Arrays.asList("5_8_8_10_10_15_15_20_20_25_25_35_35_50","5_8", "20_25", "1_10", "3_30", "25_35", "0_20_30_50", "20_30_40", "30_10", "0");
        //List<String> price = Arrays.asList("0_20", "0");
        inputArrays.add(price);
        List<String> seat = Arrays.asList("3", "5", "6", "8", "7", "0", "2_7", "4_5");
        inputArrays.add(seat);
        List<String> level = Arrays.asList("2", "8_102_101", "16", "17", "17_17", "5_6", "18_14", "14_15", "0");
        inputArrays.add(level);
        List<String> brand = Arrays.asList("12_18", "22", "1_10", "19_33_33", "19_19", "0", "36");
        inputArrays.add(brand);
        List<String> mileage = Arrays.asList("0_300", "100_299", "1_100", "0", "19_19", "200_100", "400_500_500_600");
        inputArrays.add(mileage);
        List<String> struct = Arrays.asList("1_9", "4", "1_5_3", "0", "6_8", "10_10", "1_2_3");
        inputArrays.add(struct);
        List<String> country = Arrays.asList("1_9", "4", "1_5_3", "0", "6_8", "10_10", "1_2_3_4");
        inputArrays.add(country);
        List<String> isimport = Arrays.asList("1_3", "4", "1_5_3", "0", "1_6", "10_10");
        inputArrays.add(isimport);
        List<String> config = Arrays.asList("2_4_8", "2_8_14", "1_5_3", "0", "1_6", "10_10");
        inputArrays.add(config);
        List<String> gearbox = Arrays.asList("101", "2_8_14", "1_5", "0", "1_6", "10_10", "1_2");
        inputArrays.add(gearbox);
        List<String> flowmode = Arrays.asList("1", "2_8_14", "1_5", "0", "1_2_3", "10_10");
        inputArrays.add(flowmode);
        List<String> drivetype = Arrays.asList("1", "2_8_14", "1_2_3", "0", "1_3", "5_4");
        inputArrays.add(drivetype);
        List<String> dcap = Arrays.asList("1.6_2.5", "2.9_4.0", "2.5_3.0_3.5_5.5", "1.2_1.8", "2.0_3.6", "0", "10_10");
        inputArrays.add(dcap);
        List<String> sortType = Arrays.asList("1","2","4","5","3","6","7","0");
        inputArrays.add(sortType);

        //--------------测试seriesResult 打开注释
        List<String> size = Arrays.asList("1","2","3");
        //List<String> size = Arrays.asList("3");
        inputArrays.add(size);

        //-------------测试series、spec 打开注释
        List<Integer> seriesIdAll = idsService.getAllSeriesIds();
        Collections.shuffle(seriesIdAll, new Random());
        List<String> seriesId = seriesIdAll.stream().map(String::valueOf).limit(10).collect(Collectors.toList());
        //List<String> seriesId = Arrays.asList("");
        inputArrays.add(seriesId);

        // 随机选择 5000 种情况的组合
        List<List<String>> randomCombinations = generateRandomCombinations(inputArrays, 5000);

        System.out.println("====测试case：" + randomCombinations.size() + "=====");

        int i = 0;
        for (List<String> param : randomCombinations) {
            String[] paramArray = param.toArray(new String[0]);
            String url = String.format(
                    path + "&energytype=%s&price=%s&seat=%s&level=%s&brand=%s&mileage=%s&struct=%s&country=%s&isimport=%s&config=%s&gearbox=%s&flowmode=%s&drivetype=%s&dcap=%s&sortType=%s&pagesize=%s&seriesId=%s",
                    (Object[]) paramArray
            );
            compare(paramContext, url);
            System.out.println(i++);
        }


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

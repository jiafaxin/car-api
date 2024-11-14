package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.Arrays;
import java.util.List;

public class ShowPicInfoByLevelListCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> showIds = idsService.getShowIds();
        List<Integer> sizes = Arrays.asList(0,5,10,15,20,21,22,30);
        List<Integer> evcars = Arrays.asList(0,1,2);
        List<String> levelStr = Arrays.asList("0","1" ,"2" ,"3" ,"4" ,"5" ,"6" ,"7","9" ,"11" ,"12","级,的" ,"13" ,"14" ,"15" ,"16" ,"17" ,"18" , "19" ,"20" ,"21" ,"22" ,"23" ,"24",
                "16,17,18,19,20","2,3,4,6,7,8,16,17,18,19,20","16,17,18,19,20","贾发欣","5,6","1,2,3,4,5,6,7,8,9,11,12,13,14,15","9,16,17,18,19,20",
                "","1,2,3,4,5,6,7,8","asda","汉子,sada,sad,fasd");
        for(String levelid: levelStr){
            System.out.println("当前level为："+levelid);
            for(int evcar : evcars){
                for(int size : sizes){
                    String npath = path + "&levellist=" + levelid + "&evcar=" + evcar +"&size="+size + "&showid=";
                    compareById(paramContext, npath, showIds);
                }
            }
        }
    }

}

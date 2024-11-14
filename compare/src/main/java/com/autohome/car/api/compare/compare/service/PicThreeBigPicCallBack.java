package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * int size
 * int showid
 * int pavilionid
 */
public class PicThreeBigPicCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);
        //List<String> size = Arrays.asList("30", "5000", "10000");
        List<String> size = Arrays.asList("30", "50");
        List<String> utimes = Arrays.asList("","2023-09-05 15:57:11", "2022-09-08 00:00:00", formattedDate, "");
        for(String s : size){
            String url = path + "&size=" + s + "&utime=";
            compareByIdString(paramContext, url, utimes);
        }

    }
}

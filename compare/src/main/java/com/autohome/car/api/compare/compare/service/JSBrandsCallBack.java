package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class JSBrandsCallBack implements CallBack {
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<String> states = Const.yearState;
        String[] filterimages = new String[]{"0","1"};
        int[] typeids = {0,1,2,3};
        for (String state:states) {
            System.out.println("当前状态为："+ state);
            for (String filterimage:filterimages) {
                for(int typeid:typeids){
                    String npath = path + "&state="+state+"&IsFilterSpecImage="+filterimage+"&typeid=" + typeid;
                    compare(paramContext, npath);
                }
            }
        }
    }

}

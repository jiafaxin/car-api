package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;

import java.util.List;

public class PicClassItemsBySeriesIdStateInnerColorCallBack implements CallBack{
    @Override
    public void call(Param paramContext, IdsService idsService, String path) {
        List<Integer> seriesIds = idsService.getAllSeriesIds();
        List<Integer> innerColorIds = idsService.getAllInnerColorIds();
        List<String> state = Const.stateNewList;
        for(int j = 0;j<innerColorIds.size();j++){
            for(int z = 0;z<state.size();z++){
                String npath = path +"&innercolorid="+innerColorIds.get(j)+"&state="+state.get(z)+"&seriesid=";
                compareById(paramContext, npath, seriesIds);
            }
        }
    }
}

package com.autohome.car.api.compare.compare.service;

import com.autohome.car.api.compare.compare.constant.Const;
import com.autohome.car.api.compare.compare.enums.OperType;
import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.tools.CompareJson;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 如果默认是不满足，则自已定义service 实现 CallBack接口
 */
public class DefaultCallBack implements CallBack {
    @Override
    public void call(Param param, IdsService idsService, String path) {
        String[] exclude = {"root.result.paramtypeitems[*].paramitems[*].pnid","root.result.t1"};
        param.setExclude(exclude);
        OperType operType = param.getOperType();
        String field = param.getField();
        if (StringUtils.isBlank(field)) {
            field = operType.getField();
        }
        path = String.format("%s%s%s%s", path, "&", field, "=");
        switch (operType) {
            case FCT_ID:
                compareById(param, path, idsService.getAllFctId());
                break;
            case SPEC_ID:
                compareById(param, path, idsService.getAllSpecIds());
                break;
            case SERIES_ID:
                if (param.isHasState()) {
                    compareHasStateById(path, param, idsService.getAllSeriesIds(), Const.stateList);
                } else {
                    compareById(param, path, idsService.getAllSeriesIds());
                }
                break;
            case SPEC_ID_List:
                compareByIds(param, path, idsService.getAllSpecIds());
                break;
            case SERIES_ID_List:
                compareByIds(param, path, idsService.getAllSeriesIds());
                break;
            case SERIES_List:
                compareByIds(param, path, idsService.getAllSpecIds());
                break;
            case NO_PARAM:
                compare(param, path);
                break;
            case BRAND_ID_LIST:
                compareByIds(param, path, idsService.getAllBrandIds());
                break;
            case BRAND_ID:
                compareById(param, path,idsService.getAllBrandIds());
                break;
            case ID:
                compareById(param, path,idsService.getAllBrandIds());
                break;
            case SHOW_ID://车展id
                compareById(param, path, idsService.getShowIds());
                break;
            case STATE://车展id
                compareByIdString(param, path, idsService.getState());
                break;
            default:
                System.out.println("没有对应的操作类型，不做处理， 请设置 OperType这个枚举");

        }
    }
    public void compareHasStateById(String path, Param param, List<Integer> ids, List<String> stateList) {
        for (String state : stateList) {
            String tempPath = path.concat("&state=" + state).concat("&" + param.getField() + "=");
            compareById(param, tempPath, ids);
        }
    }
    public void compare(Param param, String url) {
        new CompareJson().exclude(param.getExclude()).compareUrlAsyncCommon(url, getEnv());
    }


}

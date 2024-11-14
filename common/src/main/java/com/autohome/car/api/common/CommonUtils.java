package com.autohome.car.api.common;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    public static final String SERIES_LIST_TOPIC = "series_list_topic";

    public static final String SERIES_DETAIL_TOPIC = "series_detail_topic";

    public static final String SERIES_SPEC_LIST_TOPIC = "series_spec_list_topic";

    public static final String PARAM_CONFIG_TOPIC = "param_config_topic";

    public static final String PIC_LIST_TOPIC = "pic_list_topic";

    public static final String SPEC_DETAIL_TOPIC = "spec_detail_topic";


    public static final String APPID = "appid";

    public static final String _APPID = "_appid";

    /**
     * 校验 appid
     */
    public static Map<String, Object> getAppIdError(int errorCode) {
        Map<String, Object> resultError = new HashMap<>();
        switch (errorCode){
            case 103:
                resultError.put("returncode", ReturnMessageEnum.RETURN_MESSAGE_ENUM103.getReturnCode());
                resultError.put("message", ReturnMessageEnum.RETURN_MESSAGE_ENUM103.getReturnMsg());
                break;
            case 104:
                resultError.put("returncode", ReturnMessageEnum.RETURN_MESSAGE_ENUM104.getReturnCode());
                resultError.put("message", ReturnMessageEnum.RETURN_MESSAGE_ENUM104.getReturnMsg());
                break;
        }
        return resultError;
    }

}

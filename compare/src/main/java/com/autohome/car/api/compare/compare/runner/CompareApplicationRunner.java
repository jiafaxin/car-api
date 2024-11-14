package com.autohome.car.api.compare.compare.runner;

import com.autohome.car.api.compare.compare.param.IdsService;
import com.autohome.car.api.compare.compare.param.Param;
import com.autohome.car.api.compare.compare.service.CallBack;
import com.autohome.car.api.compare.compare.service.DefaultCallBack;
import com.autohome.car.api.compare.compare.url.UrlRegistry;
import com.autohome.car.api.compare.tools.CompareJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

@Component
@Slf4j
public class CompareApplicationRunner implements ApplicationRunner {

    @Resource
    private UrlRegistry urlRegistry;

    @Resource
    private IdsService idsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (; ; ) {
            Scanner myObj = new Scanner(System.in);  // 创建Scanner对象
            System.out.println("请输入要测试的url:");
            String url = myObj.nextLine();
            System.out.println("开始对比：" + url);
            Map<String, Param> urlMap = urlRegistry.getUrlMap();
            Param param = urlMap.get(url);
            if (Objects.isNull(param)) {
                System.out.println("请输入正确的urL");
                continue;
            }
            CallBack callBack = param.getCallBack();
            if (Objects.isNull(callBack)) {
                callBack = new DefaultCallBack();
            }
            String envUrl = CompareJson.map.get(callBack.getEnv());
            if (StringUtils.isBlank(envUrl)) {
                continue;
            }
            System.out.println("当前环境是：" + callBack.getEnv() + "， 对应的域名:" + envUrl);
            callBack.call(param, idsService, String.format("%s%s", url, "?_appid=app"));
            System.out.println("对比完成");

        }
    }
}

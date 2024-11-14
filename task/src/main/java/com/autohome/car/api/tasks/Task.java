package com.autohome.car.api.tasks;

import com.autohome.job.core.util.HandlerTestTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages = {"com.autohome.car.api"})
@EnableScheduling
public class Task {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Task.class, args);

        //添加参数：jobhandlertest 可以本地调试
        if (args != null && Arrays.stream(args).anyMatch(x -> x.indexOf("jobhandlertest") >= 0)) {
            HandlerTestTool.Testing(context);
        }
    }
}

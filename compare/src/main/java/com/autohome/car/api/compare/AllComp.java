package com.autohome.car.api.compare;

import com.autohome.car.api.common.HttpClient;
import com.autohome.car.api.compare.tools.CompareJson;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class AllComp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PiccolorInnerColoritemsbyspecid.class, args);

        String indexname = "nginx-yhcpzx-2023.09.05";

        String url = "http://10.28.4.197:8080/elasticsearch/_msearch";

        Set<String> allurls = new HashSet<>();

        List<CompletableFuture> tasks = new ArrayList<>();

        Date et = new Date();
        Date st = DateUtils.addHours(et,-1);

        while (et.after(st)) {
            int from = 0;
            System.out.println("time："+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(et));
            long f = DateUtils.addMinutes(et, -1).getTime();
            long e = et.getTime();
            et = DateUtils.addMinutes(et, -1);
            while (true) {
                System.out.println("now:" + from);
                List<String> urls = new ArrayList<>();
                try {
                    String body = "[{\"index\":\"nginx-yhcpzx-*\",\"ignore_unavailable\":true,\"timeout\":30000,\"preference\":1693907456650}\n" +
                            "{\"version\":true,\"size\":500,\"from\":" + from + ",\"sort\":[{\"@timestamp\":{\"order\":\"desc\",\"unmapped_type\":\"boolean\"}}],\"_source\":{\"excludes\":[]},\"aggs\":{\"2\":{\"date_histogram\":{\"field\":\"@timestamp\",\"interval\":\"30s\",\"time_zone\":\"Asia/Shanghai\",\"min_doc_count\":1}}},\"stored_fields\":[\"*\"],\"script_fields\":{},\"docvalue_fields\":[\"@timestamp\"],\"query\":{\"bool\":{\"must\":[{\"match_all\":{}},{\"match_phrase\":{\"host_addr\":{\"query\":\"car.api.autohome.com.cn\"}}},{\"range\":{\"@timestamp\":{\"gte\":"+f+",\"lte\":"+e+",\"format\":\"epoch_millis\"}}}],\"filter\":[],\"should\":[],\"must_not\":[{\"match_phrase\":{\"upstream_addr\":{\"query\":\"10.180.218.64:80\"}}},{\"bool\":{\"should\":[{\"match_phrase\":{\"status\":\"404\"}},{\"match_phrase\":{\"status\":\"302\"}}],\"minimum_should_match\":1}}]}},\"highlight\":{\"pre_tags\":[\"@kibana-highlighted-field@\"],\"post_tags\":[\"@/kibana-highlighted-field@\"],\"fields\":{\"*\":{}},\"fragment_size\":2147483647}}]\n";
                    HttpHeaders httpHeaders = new DefaultHttpHeaders();
                    httpHeaders.add("kbn-version", "6.3.2");
                    httpHeaders.add("content-type", "application/x-ndjson");

                    String str = "";

                    while (true) {
                        try {
                            str = HttpClient.postBody(url, body, httpHeaders).join();
                            break;
                        } catch (Exception ex) {
                        }
                    }

                    JSONObject json = new JSONObject(str);

                    JSONArray os = ((JSONObject) json.getJSONArray("responses").get(0)).getJSONObject("hits").getJSONArray("hits");
                    if (os.length() <= 10)
                        break;

                    for (Object o : os) {
                        JSONObject jo = (JSONObject) o;
                        jo.getJSONObject("_source").getString("request_uri");
                        String newUrl = jo.getJSONObject("_source").getString("request_uri");
                        if (allurls.contains(newUrl)) continue;
                        allurls.add(newUrl);
                        urls.add(newUrl);
                    }

                    int i = 0;
                    for (String url1 : urls) {
                        tasks.add(new CompareJson().compareUrlAsyncCommon(url1).thenAccept(x -> {

                        }));
                        if (i++ % 20 == 0) {
                            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                            tasks = new ArrayList<>();
                        }
                    }

                    CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
                    tasks = new ArrayList<>();
                } catch (Exception ex) {
                    System.out.println(ex);
                }

                from = from + 500;


            }
        }


        System.out.println("完成");

    }

}

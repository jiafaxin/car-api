package com.autohome.car.api.tasks.basic;

import autohome.rpc.car.car_api.v1.common.CarPhotoViewItemMessage;
import com.alibaba.fastjson2.JSON;
import com.autohome.car.api.common.*;
import com.autohome.car.api.common.http.ApiPicHotResult;
import com.autohome.car.api.common.http.HttpHelper;
import com.autohome.car.api.common.http.ResponseContent;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.services.basic.series.PhotosService;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.handler.annotation.JobHander;
import com.autohome.job.core.log.XxlJobLogger;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@JobHander(value = "PicPreHotServiceJob")
@Service
public class PicPreHotServiceJob extends IJobHandler {

    private static final String API_UTL = "http://huhang-cdn.corpautohome.com/openapi/preload/add";


    @Autowired
    private SeriesMapper seriesMapper;

    @Autowired
    private PhotosService photosService;


    @Autowired
    private RedisTemplate redisTemplate;


    private static final String CACHE_SERIES_ID = "pre_hot_cache_series_id";

    private static final String CACHE_COUNT = "pre_hot_cache_count";

    /**
     * 执行任务
     * @param strings
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        XxlJobLogger.log("图片预热 start");
        Long startTime = System.currentTimeMillis();
        try {
            //刷cdn
            picPreHot();

        } catch (Exception ex) {
            XxlJobLogger.log("图片预热 error.>>" + JSON.toJSONString(ex));
        }
        Long endTime = System.currentTimeMillis();
        System.out.println("本次图片预热耗时:" + (endTime - startTime)/1000 + "s");
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "图片预热结束耗时为：" + (endTime - startTime)/1000);
    }

    /**
     * 6531
     * 2591
     * 图片预热，刷cdn
     * 半小时执行一次 每天0-8执行 半小时执行6万url
     * 0 0/30 0-8 * * ?
     * @throws Exception
     */
    public void picPreHot() throws InterruptedException {
        List<Integer> ids = seriesMapper.getAllSeriesIds().stream().sorted().collect(Collectors.toList());
        AtomicInteger count = new AtomicInteger(0);
        Object cacheSeriesId = redisTemplate.opsForValue().get(CACHE_SERIES_ID);
        int curSeriesId = 0;
        //每次取redis中取上次的车系id
        if(null != cacheSeriesId){
            int tmpId = (int) cacheSeriesId;
            ids = ids.stream().filter(s -> s >= tmpId).sorted().collect(Collectors.toList());
        }
        for (Integer seriesId : ids) {
            curSeriesId = seriesId;
            //每次跑6万条数据
            if(count.get() >= 40000){
                curSeriesId = seriesId;
                break;
            }

            List<CarPhotoViewItemMessage> messages = photosService.get(seriesId);
            if(CollectionUtils.isNotEmpty(messages)){
                List<String> urls = new ArrayList<>();
                List<String> filePaths = messages.stream().map(CarPhotoViewItemMessage::getPicFilePath).collect(Collectors.toList());
                for(String filePath : filePaths){
                    //每次加1
                    count.addAndGet(1);
                    String stringPic2000x1500 = CarSettings.getInstance().GetFullImagePathByPrefix(filePath, "2000x1500_");
                    String stringPic2000x1500Webp = stringPic2000x1500 + ".webp";
                    urls.add(stringPic2000x1500Webp);
                }
                List<List<String>> divideList = ListUtils.groupListByQuantity(urls, 50);
                //List<CompletableFuture> tasks = new ArrayList<>();
                for (List<String> list : divideList) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            String reqPath = StringUtils.join(list,",");
                            Map mapParam = new HashMap();
                            mapParam.put("whouse", "cars");
                            mapParam.put("urls", reqPath);
                            ResponseContent responseContent = HttpHelper.postResponse(API_UTL, new HashMap<>(), mapParam, "UTF-8", 30000, 0);
                            if (responseContent != null && responseContent.getContent() != null) {
                                ApiPicHotResult apiResult = JSON.parseObject(responseContent.getContent(), ApiPicHotResult.class);
                                XxlJobLogger.log("结果："+JSON.toJSONString(apiResult) + "车系："+seriesId+"执行到：" + count.get());
                                if (!apiResult.isStatus() ||
                                        (null != apiResult.getBody() && !apiResult.getBody().getTotalStatus().contains("success"))) {
                                    XxlJobLogger.log("图片预热请求预热地址失败");
                                }
                            }
                        } catch (Exception e) {
                            XxlJobLogger.log("图片预热请求预热地址异常");
                            throw new RuntimeException();
                        }
                    });
                    Thread.sleep(1500);
                }
            }
        }
        //最后写到redis
        cache(curSeriesId,count);
    }

    public void cache(int curSeriesId,AtomicInteger count){
        //记录跑到的车系id和跑的总数
        redisTemplate.opsForValue().set(CACHE_SERIES_ID,curSeriesId,30, TimeUnit.DAYS);
        Object num = redisTemplate.opsForValue().get(CACHE_COUNT);
        int sum = 0;
        if(null != num){
            sum = (Integer) num + count.get();
        }else{
            sum = count.get();
        }
        //图片总数
        redisTemplate.opsForValue().set(CACHE_COUNT,sum,30, TimeUnit.DAYS);
    }

    /**
     *
     * 测试
     */
//    public static void main(String[] args) {
//        String str = "~/cardfs/product/g31/M07/74/A4/autohomecar__ChxoHWYU02-Aep6QADBo3zULMQQ249.jpg";
//        String stringPic2000x1500 = CarSettings.getInstance().GetFullImagePathByPrefix(str, "2000x1500_");
//        String stringPic2000x1500Webp = stringPic2000x1500 + ".webp";
//        System.out.println(stringPic2000x1500);
//        System.out.println(stringPic2000x1500Webp);
//    }
}

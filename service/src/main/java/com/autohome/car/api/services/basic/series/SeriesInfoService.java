package com.autohome.car.api.services.basic.series;

import com.autohome.car.api.common.*;
import com.autohome.car.api.data.popauto.GroupMapper;
import com.autohome.car.api.data.popauto.SeriesMapper;
import com.autohome.car.api.data.popauto.entities.GroupEntity;
import com.autohome.car.api.data.popauto.entities.SeriesViewEntity;
import com.autohome.car.api.services.basic.*;
import com.autohome.car.api.services.basic.models.*;
import com.autohome.car.api.services.models.SeriesConfig;
import com.autohome.car.api.services.models.SeriesInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SeriesInfoService extends BaseService<SeriesInfo> {

    @Autowired
    SeriesMapper seriesMapper;

    @Autowired
    SeriesBaseService seriesBaseService;

    @Autowired
    FactoryBaseService factoryBaseService;

    @Autowired
    BrandBaseService brandBaseService;

    @Autowired
    LevelBaseService levelBaseService;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    QRCodeService qrCodeService;

    @Override
    protected EhCacheName getCacheName() {
        return EhCacheName.M_5;
    }

    @Override
    protected Integer getRedisTimeoutMinutes() {
        return 24*60;
    }

    @Override
    protected SeriesInfo getData(Map<String, Object> params) {
        return getSeriesInfo((int)params.get("seriesId"));
    }

    Map<String,Object> makeParam(int seriesId){
        Map<String,Object> param = new LinkedHashMap<>();
        param.put("seriesId",seriesId);
        return param;
    }

    public SeriesInfo get(int seriesId,boolean dispqrcode,boolean needHtmlDecode) {
        SeriesInfo info = get(makeParam(seriesId));
        if (info != null) {
            if (!dispqrcode) {
                info.setQrcode("");
            }
            if (needHtmlDecode) {
                info.setSeriesname(HtmlUtils.decode(info.getSeriesname()));
            }
        }
        return info;
    }

    public Map<Integer, SeriesInfo> getMap(List<Integer> seriesIds,boolean dispqrcode,boolean needHtmlDecode) {
        List<SeriesInfo> list = getList(seriesIds,dispqrcode,needHtmlDecode);
        return list.stream().collect(Collectors.toMap(SeriesInfo::getSeriesid, x -> x,(x, y)->x));
    }

    public List<SeriesInfo> getList(List<Integer> seriesIds,boolean dispqrcode,boolean needHtmlDecode){
        if(CollectionUtils.isEmpty(seriesIds))
            return new ArrayList<>();

        List<Map<String,Object>> params = new ArrayList<>();
        for (Integer id : seriesIds) {
            if(id==null)
                continue;

            params.add(makeParam(id));
        }
        List<SeriesInfo> list = mGet(params);
        if(!CollectionUtils.isEmpty(list)){
            List<SeriesInfo> opList = ToolUtils.deepCopyList(list);
            for(SeriesInfo info : opList){
                if (info != null) {
                    if (!dispqrcode) {
                        info.setQrcode("");
                    }
                    if (needHtmlDecode) {
                        info.setSeriesname(HtmlUtils.decode(info.getSeriesname()));
                    }
                }
            }
            return opList;
        }
        return new ArrayList<>();
    }

    SeriesInfo getSeriesInfo(int seriesId) {
        SeriesInfo seriesInfo = new SeriesInfo();
        seriesInfo.setSeriesid(seriesId);

        AtomicReference<SeriesViewEntity> seriesViewAR = new AtomicReference<>();
        AtomicReference<String> qrCodeAR = new AtomicReference<>("");
        AtomicReference<SeriesBaseInfo> baseInfoAR = new AtomicReference<>();
        AtomicReference<FactoryBaseInfo> fctAR = new AtomicReference<>();
        AtomicReference<BrandBaseInfo> brandAR = new AtomicReference<>();
        AtomicReference<LevelBaseInfo> levelAR = new AtomicReference<>();
        AtomicReference<GroupEntity> groupAR = new AtomicReference<>();

        List<CompletableFuture> tasks = new ArrayList<>();
        tasks.add(CompletableFuture.supplyAsync(() -> seriesMapper.getSeriesView(seriesId)).thenAccept(x ->
                seriesViewAR.set(x)
        ));
        tasks.add(seriesBaseService.get(seriesId).thenComposeAsync(x -> {
            if(x==null)
                return CompletableFuture.completedFuture(null);

            baseInfoAR.set(x);

            List<CompletableFuture> ctasks = new ArrayList<>();

            ctasks.add(factoryBaseService.getFactoryAsync(x.getFactId()).thenAccept(fct -> fctAR.set(fct)));
            ctasks.add(brandBaseService.get(x.getBrandId()).thenAccept(brand -> brandAR.set(brand)));
            ctasks.add(levelBaseService.getLevelAsync(x.getLevelId()).thenAccept(level -> levelAR.set(level)));

            ctasks.add(CompletableFuture.supplyAsync(() -> groupMapper.getGroup(x.getBrandId())).thenAccept(group -> groupAR.set(group)));

            return CompletableFuture.allOf(ctasks.toArray(new CompletableFuture[ctasks.size()]));
        }));

        tasks.add(qrCodeService.series(seriesId).thenAccept(x -> qrCodeAR.set(x)));
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();

        SeriesViewEntity seriesView = seriesViewAR.get();
        if (seriesView == null)
            return null;

        SeriesBaseInfo baseInfo = baseInfoAR.get();
        FactoryBaseInfo fct = fctAR.get();
        BrandBaseInfo brand = brandAR.get();
        LevelBaseInfo level = levelAR.get();
        GroupEntity group = groupAR.get();

        return make(seriesView,baseInfo,fct,brand,level,group, qrCodeAR.get());
    }


    public void refreshAll(Consumer<String> log) {
        List<GroupEntity> groups = groupMapper.getAllGroup();
        seriesMapper.getAllSeriesView().forEach(seriesView -> {
            int seriesId = seriesView.getSeriesid();
            try {
                SeriesBaseInfo x = seriesBaseService.get(seriesId).join();
                SeriesInfo info = make(
                        seriesView,
                        seriesBaseService.get(seriesId).join(),
                        factoryBaseService.getFactory(x.getFactId()),
                        brandBaseService.get(x.getBrandId()).join(),
                        levelBaseService.getLevel(x.getLevelId()),
                        groups.stream().filter(y -> y.getId() == x.getBrandId()).findFirst().orElse(null),
                        qrCodeService.series(seriesId).join()
                );
                refresh(makeParam(seriesId), info);
            } catch (Exception e) {
                log.accept("error:" + seriesId + ">>>>" + ExceptionUtil.getStackTrace(e));
            }
        });
    }

    SeriesInfo make(
            SeriesViewEntity seriesView,
            SeriesBaseInfo baseInfo,
            FactoryBaseInfo fct,
            BrandBaseInfo brand,
            LevelBaseInfo level,
            GroupEntity group,
            String qrCode
    ){
        if(baseInfo==null||seriesView==null)
            return null;
        SeriesInfo seriesInfo = new SeriesInfo();
        seriesInfo.setSeriesid(seriesView.getSeriesid());

        seriesInfo.setQrcode(qrCode);
        seriesInfo.setSeriesplace(seriesView.getSeriesplace());
        seriesInfo.setContainelectriccar(seriesView.getContainelectriccar()+"");
        seriesInfo.setFctfirstletter(seriesView.getFctFirstLetter());


        seriesInfo.setSeriesname(baseInfo.getName());
        seriesInfo.setBrandid(baseInfo.getBrandId());
        seriesInfo.setFctid(baseInfo.getFactId());
        seriesInfo.setLevelid(baseInfo.getLevelId());
        seriesInfo.setSerieslogo(ImageUtil.getFullImagePath(baseInfo.getLogo()));
        seriesInfo.setRawSeriesLogo(baseInfo.getLogo());
        seriesInfo.setSeriesofficialurl(baseInfo.getUrl());
        seriesInfo.setSeriesfirstletter(baseInfo.getFl());

        if(fct!=null) {
            seriesInfo.setFctname(fct.getName());
            seriesInfo.setFctlogo(ImageUtil.getFullImagePath(fct.getLogo()));
            seriesInfo.setFctofficialurl(fct.getUrl());
        }

        if(brand!=null) {
            seriesInfo.setBrandname(brand.getName());
            seriesInfo.setBrandlogo(ImageUtil.getFullImagePath(brand.getLogo()));
            seriesInfo.setBrandofficialurl(brand.getUrl());
            seriesInfo.setBrandfirstletter(brand.getFirstLetter());
        }

        seriesInfo.setLevelname(level==null?"": level.getName());

        seriesInfo.setCountryid(group==null?0:group.getCountryId());
        seriesInfo.setCountryname(group==null?"": group.getCountry());

        return seriesInfo;
    }


}

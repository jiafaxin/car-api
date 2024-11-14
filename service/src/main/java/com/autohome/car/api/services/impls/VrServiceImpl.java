package com.autohome.car.api.services.impls;

import autohome.rpc.car.car_api.v1.vr.*;
import com.autohome.car.api.common.ReturnMessageEnum;
import com.autohome.car.api.data.popauto.entities.VrSpecEntity;
import com.autohome.car.api.data.popauto.entities.ZiXunCarPicVrEntity;
import com.autohome.car.api.services.AutoCacheService;
import com.autohome.car.api.services.VrService;
import com.autohome.car.api.services.basic.VrSpecBaseService;
import com.autohome.car.api.services.common.CommonFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class VrServiceImpl implements VrService {

    @Resource
    private AutoCacheService autoCacheService;

    @Resource
    private VrSpecBaseService vrSpecBaseService;

    /**
     * 车系id集合获取vr信息
     * @param request
     * @return
     */
    @Override
    public GetVRUrlAndCoverImgBySeriesIdListResponse getVRUrlAndCoverImgBySeriesIdList(GetVRUrlAndCoverImgBySeriesIdListRequest request) {
        GetVRUrlAndCoverImgBySeriesIdListResponse.Builder builder = GetVRUrlAndCoverImgBySeriesIdListResponse.newBuilder();
        List<Integer> seriesIds = CommonFunction.getListFromStr(request.getSeriesidList());
        if(CollectionUtils.isEmpty(seriesIds) || seriesIds.size() > 20){
            return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM101.getReturnMsg())
                    .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM101.getReturnCode()).build();
        }
        //字段转化
        int ahrotateOld = CommonFunction.getStringToInt(request.getAhrotate(),1);
        String param_src = request.getSrc();
        String flg = StringUtils.isNotBlank(param_src) ? "&" : "?";
        String src = StringUtils.isNotBlank(param_src) ? "?src=" + param_src : "";
        String ahrotate = flg + "_ahrotate=" + ahrotateOld;
        GetVRUrlAndCoverImgBySeriesIdListResponse.Result.Builder resultBuild = GetVRUrlAndCoverImgBySeriesIdListResponse.Result.newBuilder();

        for(int seriesId : seriesIds){
            List<VrSpecEntity> vrSpecEntities = vrSpecBaseService.getDataBySeriesId(seriesId);
            if(!CollectionUtils.isEmpty(vrSpecEntities)){
                GetVRUrlAndCoverImgBySeriesIdListResponse.VrUrl.Builder vrUrl = GetVRUrlAndCoverImgBySeriesIdListResponse.VrUrl.newBuilder();
                vrUrl.setSeriesId(vrSpecEntities.get(0).getSeriesId());
                vrUrl.setSpecid(vrSpecEntities.get(0).getSpecId());
                vrUrl.setVrUrl(null != vrSpecEntities.get(0).getPanoUrl() ?
                        CommonFunction.VR_SERIES_APP_ROOT_URL+vrSpecEntities.get(0).getPanoUrl()+src+ahrotate : "");
                vrUrl.setCoverUrl(null != vrSpecEntities.get(0).getCoverUrl() ?
                        vrSpecEntities.get(0).getCoverUrl() : "");
                resultBuild.addSpeclist(vrUrl);
            }
        }
        builder.setResult(resultBuild).
                setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode()).
                setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg());
        return builder.build();
    }

    /**
     * 车系id或者车型id获取vr信息
     * @param request
     * @return
     */
    @Override
    public GetVRUrlAndCoverImageResponse getVRUrlAndCoverImage(GetVRUrlAndCoverImageRequest request) {
        GetVRUrlAndCoverImageResponse.Builder builder = GetVRUrlAndCoverImageResponse.newBuilder();
        int seriesId = request.getSeriesid();
        int specId = request.getSpecid();
        if ((seriesId == 0 && specId == 0) || (seriesId <= 0 && specId <= 0)) {
            return builder.setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM101.getReturnCode())
                    .setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM101.getReturnMsg())
                    .build();
        }
        int ahrotatetmp = CommonFunction.getStringToInt(request.getAhrotate(),1);
        String param_src = request.getSrc();
        String flg = StringUtils.isNotBlank(param_src) ? "&" : "?";
        String src = StringUtils.isNotBlank(param_src) ? "?src=" + param_src : "";
        String ahrotate = flg + "_ahrotate=" + ahrotatetmp;
        List<VrSpecEntity> vrSpecEntities = null;
        if(specId <= 0){
            vrSpecEntities = vrSpecBaseService.getDataBySeriesId(seriesId);
        }else{
            vrSpecEntities = vrSpecBaseService.getDataBySpecId(specId);
        }
        GetVRUrlAndCoverImageResponse.Result.Builder resultBuild = GetVRUrlAndCoverImageResponse.Result.newBuilder();
        if(!CollectionUtils.isEmpty(vrSpecEntities)){
            for(VrSpecEntity vrSpecEntity : vrSpecEntities){
                GetVRUrlAndCoverImageResponse.VrUrl.Builder vrUrl = GetVRUrlAndCoverImageResponse.VrUrl.newBuilder();
                vrUrl.setSpecid(vrSpecEntity.getSpecId());
                vrUrl.setVrUrl(null !=  vrSpecEntity.getPanoUrl() ?
                        CommonFunction.VR_SERIES_APP_ROOT_URL + vrSpecEntity.getPanoUrl() + src+ahrotate : "");
                vrUrl.setCoverUrl169(null != vrSpecEntity.getCoverUrl16_9() ?
                        CommonFunction.VR_SERIES_IMAGE_ROOT_URL + vrSpecEntity.getCoverUrl16_9() : "");
                vrUrl.setCoverUrl43(null != vrSpecEntity.getCoverUrl4_3() ?
                        CommonFunction.VR_SERIES_IMAGE_ROOT_URL + vrSpecEntity.getCoverUrl4_3() : "");
                vrUrl.setCoverUrl(null != vrSpecEntity.getCoverUrl() ?
                        vrSpecEntity.getCoverUrl().replace("http://", "https://") : "");
                resultBuild.addSpeclist(vrUrl);
            }
        }
        resultBuild.setSeriesid(seriesId);
        return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setReturnCode(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnCode())
                .setResult(resultBuild)
                .build();
    }

    @Override
    public GetIndexSlideVrResponse getIndexSlideVr(GetIndexSlideVrRequest request) {
        GetIndexSlideVrResponse.Builder builder = GetIndexSlideVrResponse.newBuilder();
        GetIndexSlideVrResponse.Result.Builder result = GetIndexSlideVrResponse.Result.newBuilder();

        List<ZiXunCarPicVrEntity> list = autoCacheService.getIndexSlideVr();

        for (ZiXunCarPicVrEntity entity : list) {
            if (Objects.isNull(entity)) {
                continue;
            }
            result.addVrlist(GetIndexSlideVrResponse.Result.Vrlist.newBuilder().
                    setTitle(StringUtils.defaultString(entity.getTitle())).
                    setImgurl(StringUtils.defaultString(StringUtils.replace(entity.getImgUrl(),"220x165_","300x225_"))).
                    setLinkurl(StringUtils.defaultString(entity.getLinkUrl())))
                    .build();
        }
        return builder.setReturnMsg(ReturnMessageEnum.RETURN_MESSAGE_ENUM0.getReturnMsg())
                .setResult(result)
                .build();    }
}

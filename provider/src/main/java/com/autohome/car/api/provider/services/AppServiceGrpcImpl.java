package com.autohome.car.api.provider.services;

import com.autohome.car.api.data.popauto.AppPictureMapper;
import com.autohome.car.api.data.popauto.AutoTagMapper;
import com.autohome.car.api.data.popauto.entities.AppPictureEntity;
import com.autohome.car.api.data.popauto.entities.AutoTagEntity;
import com.autohome.car.api.services.TagService;
import org.apache.dubbo.config.annotation.DubboService;
import autohome.rpc.car.car_api.v1.app.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.autohome.car.api.common.ReturnMessageEnum.RETURN_MESSAGE_ENUM102;

@DubboService
@RestController
public class AppServiceGrpcImpl extends DubboAppServiceTriple.AppServiceImplBase {


    @Autowired
    AutoTagMapper autoTagMapper;

    @Autowired
    AppPictureMapper appPictureMapper;

    @Autowired
    TagService tagService;

    @Override
    @GetMapping("/v1/App/AutoTag_TagList.ashx")
    public AutoTagTagListResponse autoTagTagList(AutoTagTagListRequest request) {
        AutoTagTagListResponse.Builder builder = AutoTagTagListResponse.newBuilder();
        builder.setReturnCode(0).setReturnMsg("成功");

            int topid = request.getTop();
            int orderid = request.getOrderid();

            if (topid < 1) {
                return builder.setReturnCode(RETURN_MESSAGE_ENUM102.getReturnCode()).setReturnMsg(RETURN_MESSAGE_ENUM102.getReturnMsg()).build();
            }

        AutoTagTagListResponse.Result.Builder result = AutoTagTagListResponse.Result.newBuilder();

        for (AutoTagEntity tag : autoTagMapper.getTagList(topid)) {
            result.addTaglist(AutoTagTagListResponse.Result.Taglist.newBuilder().setId(tag.getId()).setName(tag.getName()));
        }
        return builder.setResult(result).build();
    }

    @Override
    @GetMapping("/v1/App/RecommendPicListv2.ashx")
    public RecommendPicListv2Response recommendPicListv2(RecommendPicListv2Request request) {
        RecommendPicListv2Response.Builder builder = RecommendPicListv2Response.newBuilder().setReturnCode(0).setReturnMsg("成功");
        RecommendPicListv2Response.Result.Builder result = RecommendPicListv2Response.Result.newBuilder();
        List<AppPictureEntity> list =  appPictureMapper.getAppNewPicture();
        Map<AppPictureEntity,List<AppPictureEntity>> map = list.stream().collect(Collectors.groupingBy(AppPictureEntity::groupBy));
        map.forEach((k,v)->{
            RecommendPicListv2Response.Result.Item.Builder item = RecommendPicListv2Response.Result.Item.newBuilder();
            item.setBigimg(k.getBigImg());
            item.setBrandid(k.getBrandId());
            item.setDisplaytype(k.getDisplayType());
            item.setLooptype(k.getLooptype());
            item.setPictypeid(k.getPicId());
            item.setPublishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(k.getPublishTime()));
            item.setSeriesid(k.getSeriesId());
            item.setSpecid(k.getSpecId());
            item.setTitle(k.getTitle());
            for (AppPictureEntity vitem : v) {
                item.addPicitems(
                        RecommendPicListv2Response.Result.Item.Picitem.newBuilder()
                                .setPicid(vitem.getPicId())
                                .setPicpath(vitem.getPicpath().replace("/m_","/"))
                                .setPictype(vitem.getPicTypeId())
                );
            }
            result.addItems(item);
        });
        return builder.setResult(result).build();
    }

    @Override
    @GetMapping("/v1/App/AutoTag_CarListAutoHome.ashx")
    public AutoTagCarListAutoHomeResponse autoTagCarListAutoHome(AutoTagCarListAutoHomeRequest request) {
        return tagService.autoTagCarListAutoHome(request);
    }

    @Override
    @GetMapping("/v1/App/AutoTag_CarListPrice.ashx")
    public AutoTagCarListPriceResponse autoTagCarListPrice(AutoTagCarListPriceRequest request) {
        return tagService.autoTagCarListPrice(request);
    }
}
package com.autohome.car.api.compare.compare.param;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.compare.ScnCompare;
import com.autohome.car.api.data.popauto.*;
import com.autohome.car.api.data.popauto.entities.CarPhotoTestRowEntity;
import com.autohome.car.api.data.popauto.entities.ColorInfoEntity;
import com.autohome.car.api.data.popauto.entities.PicClassEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdsService {

    @Resource
    private SeriesMapper seriesMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private FactoryMapper factoryMapper;

    @Resource
    private SeriesViewMapper seriesViewMapper;

    @Resource
    private SpecViewMapper specViewMapper;

    @Resource
    private SpecMapper specMapper;

    @Resource
    private PicClassMapper picClassMapper;

    @Resource
    private CarPhotoViewMapper carPhotoViewMapper;

    @Resource
    private InnerFctColorMapper innerFctColorMapper;

    @Resource
    private ShowMapper showMapper;

    public List<Integer> getAllLevelId() {
        return seriesViewMapper.getAllLevelIdFromSeriesView();
    }

    public List<Integer> getAllBrandIds() {
        return brandMapper.getAllBrandIds();
    }

    public List<Integer> getAllSeriesIds() {
        return seriesMapper.getAllSeriesIds();
    }

    public List<Integer> getAllSpecIds() {
        //List<Integer> specIds = getSpecIds();
        List<Integer> specIds = null;
        if (CollectionUtils.isEmpty(specIds)) {
            specIds = specMapper.getAllIds();
           // specIds = specIds.stream().filter(e -> e > 1000000).collect(Collectors.toList());
        }
        return specIds;
    }

    public List<Integer> getAllSpecYearIds() {
        return specViewMapper.getAllSpecYearIds();
    }

    public List<KeyValueDto<Integer, Integer>> getAllSeriesYearIds() {
        return specViewMapper.getAllSeriesYearIds();
    }

    public List<Integer> getAllPicClassIds(){
        List<PicClassEntity> picClass =  picClassMapper.getPicClassList();
        if(CollectionUtils.isEmpty(picClass)){
            return new ArrayList<>();
        }
        return picClass.stream().map(PicClassEntity::getId).collect(Collectors.toList());
    }

    /**
     * 获取所有内饰颜色id
     * @return
     */
    public List<Integer> getAllInnerColorIds(){
        List<ColorInfoEntity> colorInfoEntities = innerFctColorMapper.getAllColorInfo();
        if(CollectionUtils.isEmpty(colorInfoEntities)){
            return new ArrayList<>();
        }
        return colorInfoEntities.stream().map(ColorInfoEntity::getId).collect(Collectors.toList());
    }

    public List<CarPhotoTestRowEntity> getRound5000SeriesSpecColorPicRows(){
        return carPhotoViewMapper.getRound5000SeriesSpecColorPicRows();
    }
    public List<CarPhotoTestRowEntity> getRound5000SeriesSpecInnerColorPicRows(){
        return carPhotoViewMapper.getRound5000SeriesSpecInnerColorPicRows();
    }

    public List<Integer> getSpecIds() {
        return ScnCompare.SpecIds();
    }

    public List<KeyValueDto<Integer,Integer>> getAllFactoryBrands(){
        return factoryMapper.getAllFactoryBrands();
    }


    public List<Integer> getAllFctId() {
        return factoryMapper.getAllFactoryIds();
    }

    public List<KeyValueDto<Integer, Integer>> getShowIdsAndPavilionIds(){
        return showMapper.getShowIdsAndPavilionIds();
    }

    /**
     * seriesId
     * showId
     * @return
     */
    public List<KeyValueDto<Integer, Integer>> getSeriesIdShowIdAll(){
        return showMapper.getSeriesIdShowIdAll();
    }

    public List<String> getState(){
        return Arrays.asList("0","0x0001","0x0002","0x0004","0x0008", "0x0010", "0x0003","0x000c","0x000e","0x001c","0x000f","0x001e", "0X001F");
    }
    public List<Integer> getShowIds(){
        return Arrays.asList(0,1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9 ,10 ,11 ,12 ,13 ,14 ,15 ,16 ,17 ,18 ,19 ,20 ,22 ,23 ,24 ,29 ,32 ,33 ,34 ,35 ,36,
                39 ,41 ,43 ,44 ,45 ,47 ,48 ,50 ,51 ,52 ,57 ,58 ,60 ,61 ,62 ,63 ,64 ,71 ,72 ,73 ,74 ,75 ,76 ,77 ,78 ,80 ,81 ,83 ,84 ,85 ,86,
                87 ,88 ,89 ,90 ,92 ,93 ,94 ,95 ,96 ,97 ,98 ,99 ,100 ,101 ,102 ,103 ,104 ,105 ,106 ,107 ,108 ,109 ,110 ,111 ,112 ,114, 115,
                116 ,117 ,118 ,121 ,122 ,123 ,124 ,125 ,126 ,127 ,128 ,129 ,131 ,132 ,133 ,134 ,138 ,139 ,141 ,143 ,144 ,145 ,146 ,151 ,154
                ,155 ,156);
    }

    public List<Integer> getBrandIdsFromShowCarsView() {
        return Arrays.asList(238, 3226, 378, 46, 547, 215, 69, 3819, 501, 92, 115, 3627, 284, 161, 361, 169, 324, 75, 132, 438, 487, 181, 15, 9, 109, 458, 530,
                89, 3, 252, 152, 146, 52, 95, 72, 404, 3793, 118, 450, 264, 335, 281, 358, 235, 78, 66, 410, 32, 327, 364, 275, 318, 26, 272, 12, 35, 129, 390,
                155, 2860, 106, 584, 86, 63, 112, 255, 98, 341, 441, 49, 198, 476, 144, 167, 499, 353, 1017, 330, 405, 259, 516, 113, 184, 267, 67, 313, 376,
                27, 276, 121, 319, 568, 322, 173, 370, 273, 58, 156, 536, 81, 130, 436, 38, 87, 505, 350, 44, 585, 193, 342, 293, 1, 50, 47, 70, 425, 93, 525, 187,
                502, 233, 310, 456, 210, 279, 362, 373, 3576, 316, 270, 170, 3814, 224, 124, 439, 416, 84, 153, 110, 133, 10, 3307, 61, 41, 588, 339, 253, 290,
                104, 2268, 396, 147, 119, 96, 428, 165, 142, 2867, 282, 288, 188, 334, 503, 357, 374, 65, 79, 19, 265, 3614, 365, 73, 25, 36, 368, 583, 560, 228,
                105, 205, 554, 13, 62, 540, 42, 291, 397, 56, 448, 199, 248, 99, 168, 116, 331, 162, 2987, 308, 3726, 185, 231, 325, 669, 3720, 371, 122, 22, 76,
                222, 202, 108, 59, 225, 82, 251, 437, 39, 33, 182, 88, 159, 208, 3789, 53, 400, 302, 45, 537, 294, 3248, 345, 309, 48, 71, 403, 286, 94, 263, 117,
                140, 163, 432, 186, 257, 332, 77, 280, 2341, 363, 223, 472, 372, 269, 518, 60, 83, 509, 203, 154, 3804, 134, 40, 34, 11, 183, 111, 54, 91, 352,
                103, 289, 97, 214, 3795, 143, 569, 120, 383, 329, 360, 260, 283, 220, 114, 575, 20, 68, 80, 366, 323, 174, 74, 369, 320, 392, 131, 249, 512, 3781,
                57, 157, 14, 37, 529, 137, 8, 3695, 51, 200, 151, 100);
    }

}

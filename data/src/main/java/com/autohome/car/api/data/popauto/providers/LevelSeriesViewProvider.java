package com.autohome.car.api.data.popauto.providers;

public class LevelSeriesViewProvider {

    public String getSeriesInfoByLevelId(int levelId) {
        StringBuilder sb = new StringBuilder("SELECT \n" +
                "   seriesid,seriesstate,levelid,bbsisshow,userdcarstate,brandid,brandfirstletter,fctid,seriespricemin,seriespricemax,gearboxmanual,gearboxauto,\n" +
                "   countryid,mindisplacement,maxdisplacement,seriesplace,qianqu,houqu,siqu,qiyou,chaiyou,youdianhunhe,diandong,chadianhundong,zengcheng,qingranliao,qinghunsiba,qinghunersi, \n" +
                "   liangxiang,sanxiang,xianbei,lvxing,yingdingchangpeng,ruandingchangpeng,yingdingpaoche,keche,huoche,pika,mpv,suv,\n" +
                "   seat2,seat4,seat5,seat6,seat7,seat8,seriesrank,seriesIsImgSpec,\n" +
                "   config1,config2,config3,config4,config5,config6,config7,config8,config9,config10,config11,config12,\n" +
                "   config13,config14,config15,config16,config17,config18,config19,config20,config21,NewSeriesOrdercls,isimport,\n" +
                "   kuajie_sanxiang as kuajieSanXiang,kuajie_liangxiang as kuajieLiangXiang, kuajie_lvxing as kuajieLvXing, kuajie_suv as kuajieSuv\n" +
                "   FROM www_LevelSeriesView WITH(NOLOCK) WHERE ");
        switch (levelId) {
            case 9:
                sb.append(" levelid >= 16 AND levelid <= 20 ");
                break;
            case 8:
                sb.append(" levelid=8 OR (levelid >= 21 AND levelid <= 24) ");
                break;
            case 14:
            case 15:
                sb.append(" levelid >= 14 AND levelid <= 15 ");
                break;
            default:
                sb.append( " levelid = " + levelId);
        }
        sb.append(" ORDER BY seriesstate ASC ");

        return sb.toString();
    }

}

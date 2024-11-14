package com.autohome.car.api.data.popauto.providers;


import com.autohome.car.api.common.BaseConfig.Spec;
import com.google.api.client.util.Joiner;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SpecParamViewProvider {

    static final String CvGetGearBox = "select  specid as [key],ItemValue as [value]  from CV_SpecParamView with(Nolock) where item='变速箱' and name = '简称'";
    static final String GetGearBox = "select  specid as [key],valu as [value]  from specconfig with(nolock) where  item='变速箱' and name = '简称'";


    public String getGearBox(int specId) {
        if (Spec.isCvSpec(specId)) {
            return CvGetGearBox.concat(" AND specid = #{specId}");
        } else {
            return GetGearBox.concat(" AND specid = #{specId}");
        }
    }


    public String getAllGearBox() {
        return GetGearBox.concat("\n UNION ALL \n").concat(CvGetGearBox);
    }


    static final String getDicEmissionStandards4Cv = "select distinct  SpecId as [key],itemvalue as [value] from CV_SpecParamView with(nolock) where name = '环保标准' and itemvalue <> '-' and len(itemvalue)>0";
    static final String getDicEmissionStandards = "select distinct  SpecId as [key],valu as [value] from specconfig with(nolock) where name = '环保标准' and valu <> '-' and len(valu)>0";

    public String getDicEmissionStandards(int specId) {
        if (Spec.isCvSpec(specId)) {
            return getDicEmissionStandards4Cv.concat(" AND specid = #{specId}");
        } else {
            return getDicEmissionStandards.concat(" AND specid = #{specId}");
        }
    }

    public String getAllDicEmissionStandards() {
        return getDicEmissionStandards.concat("\n UNION ALL \n").concat(getDicEmissionStandards4Cv);
    }

    static final String getOilBoxVolume4CV = "select specid as [key],isnull(ItemValue,0) as [value] from cv_specParamview with(Nolock)  where item='车身' and name = '油箱容积(L)'";
    static final String getOilBoxVolume = "select specid as [key],valu as [value] from specconfig with(nolock) where item = '车身'  and name = '油箱容积(L)' and valu <> '-' and valu <> ''and valu <> '待查' ";

    public String getOilBoxVolume(int specId) {
        if (Spec.isCvSpec(specId)) {
            return getOilBoxVolume4CV.concat(" AND specid = #{specId}");
        } else {
            return getOilBoxVolume.concat(" AND specid = #{specId}");
        }
    }

    public String getAllOilBoxVolume() {
        return getOilBoxVolume.concat("\n UNION ALL \n").concat(getOilBoxVolume4CV);
    }

    static final String getEngineKW4Cv = "select distinct specid as [key], isnull(ItemValue,0) as  [value]  from cv_specParamview with(Nolock)  where item='发动机' and  name = '最大功率(kW)'  and ItemValue<> '' and ItemValue <> '-'";

    public String getEngineKW(int specId) {
        return getEngineKW4Cv.concat(" AND specid = #{specId}");
    }

    public String getAllEngineKW() {
        return getEngineKW4Cv;
    }

    public String getAllChargeTime(int seriesId) {
        String sql = "select  seriesid as id,specid as cid,ISNULL(valu,0) as ct, name as nm from specconfig with(nolock) where item = '电动机' and name in ('快充时间(小时)','慢充时间(小时)')  and len(valu)>0 and valu<>'-' and seriesId = #{seriesId} " +
                " union all " +
                " select  SeriesId as id,SpecId as cid,ISNULL(ItemValue,0) as  ct, name as nm  from CV_SpecParamView  where item = '电动机' and name in ('快充时间(小时)','慢充时间(小时)')  and len(ItemValue)>0 and ItemValue <> '-' and seriesId = #{seriesId} ";
        return sql;
    }


    public String getSpecConfig(boolean isVc, boolean unCv, List<Integer> specList) {
        String sqlWhere = Joiner.on(',').join(specList);
        StringBuilder sqlColumnBuilder = new StringBuilder();
        StringBuilder sqlPivotBuilder = new StringBuilder();
        for (Integer specId : specList) {
            sqlPivotBuilder.append("[").append(specId).append("],");
            sqlColumnBuilder.append(String.format("ISNULL([%s],%s) AS [%s],", specId, 0, specId));
        }
        String sqlColumn = StringUtils.substringBeforeLast(sqlColumnBuilder.toString(), ",");
        String sqlPivot = StringUtils.substringBeforeLast(sqlPivotBuilder.toString(), ",");
        String sql = "";
        if (isVc && !unCv) {
            sql = getAllCvSpec(sqlWhere, sqlPivot, sqlColumn);
        } else if (!isVc && unCv) {
            sql = getAllUnCvSpec(sqlWhere, sqlPivot, sqlColumn);
        } else {
            sql = getcvSepc(sqlWhere, sqlPivot, sqlColumn);
        }
        return sql;
    }

    private String getcvSepc(String sqlWhere, String sqlPivot, String sqlColumn) {
        return "SELECT configid as configId,Item as item,Name as name, "+ sqlColumn +" " +
                "                        FROM ( Select P.itemid AS configid, P.item,P.name,t.specid,t.ItemValue ,p.pordercls,p.ordercls from " +
                "                        Optimize_ParamItem_Info AS P WITH(NOLOCK) left join " +
                "                        ( " +
                "                                SELECT SpecId,Item,Name,valu AS ItemValue " +
                "                                FROM SpecConfig WITH(NOLOCK) WHERE SpecId IN (" + sqlWhere + ") " +
                "                                union all " +
                "                                SELECT SpecId, Item,Name,ItemValue AS ItemValue " +
                "                                FROM CV_SpecParamView WITH(NOLOCK) WHERE SpecId IN (" + sqlWhere + ") " +
                "                            ) AS T ON P.Item=T.Item AND P.Name= T.Name  " +
                "                            " +
                "                        ) as A " +
                "                        PIVOT (MAX(ItemValue) FOR SpecId IN (" + sqlPivot + ")) AS B " +
                "                        ORDER BY pordercls,ordercls";
    }

    private String getAllUnCvSpec(String sqlWhere, String sqlPivot, String sqlColumn) {
        return "SELECT configid as configId,Item as item,Name as name,"+ sqlColumn +" " +
                "                        FROM ( SELECT SpecId,configid, Item,Name,valu AS ItemValue,pordercls,ordercls " +
                "\t\t                        FROM SpecConfig WITH(NOLOCK) " +
                "\t\t                        WHERE SpecId IN(" + sqlWhere + "))AS A " +
                "                        PIVOT (MAX(ItemValue) FOR SpecId IN (" + sqlPivot + ")) AS B " +
                "                        ORDER BY pordercls,ordercls";
    }

    private String getAllCvSpec(String sqlWhere, String sqlPivot, String sqlColumn) {
        return " SELECT 0 configid as configId,Item as item,Name as name,"+ sqlColumn +" " +
                "                        FROM ( SELECT SpecId,0 as configid,Item,Name,ItemValue,0 ItemType,Porder,Sorder " +
                "\t\t                        FROM dbo.CV_SpecParamView WITH(NOLOCK) " +
                "\t\t                        WHERE SpecId IN(" + sqlWhere + "))AS A " +
                "                        PIVOT (MAX(ItemValue) FOR SpecId IN (" + sqlPivot + ")) AS B  " +
                "                        ORDER BY Porder,Sorder";
    }

}

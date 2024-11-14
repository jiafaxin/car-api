package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.common.cache.AutoCache;
import com.autohome.car.api.data.popauto.providers.SpecProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SpecMapper {
    @Select("select id as name  from spec_new with(nolock) where  SpecState <=30 AND isshow=0 \n" +
            "union all \n" +
            "select id as name from cv_spec with(nolock) where SpecState <=30 and ParamShow=0")
    List<Integer> getParamNowShowSpecs();

    @Select("select id from spec_new with(nolock) \n" +
            "union all \n" +
            "select id from cv_spec with(nolock) ")
    List<Integer> getAllIds();

    @SelectProvider(value = SpecProvider.class, method = "getSpecRealTestUrl")
    KeyValueDto<Integer, String> getSpecRealTestUrl(int specId);

    @Select(" select id as [key] ,speed_url as [value] from spec_new with(nolock) where len(speed_url)>0 " +
            " UNION all " +
            " select id as [key] ,speed_url as [value] from CV_Spec with(nolock) where len(speed_url)>0 ")
    List<KeyValueDto<Integer, String>>getAllSpecRealTestUrl();

    /**
     * 车型白底图
     * @return
     */
    @Select("select specid as id,filepath as name from (select specid,filepath,row_number() over(partition by specid order by id desc) num  \n" +
            "\t\tfrom car_spec_photo WITH(NOLOCK) where typeid=52 AND IsDelete=0 )as photo\n" +
            "\t\twhere num=1\n" +
            "\t\tunion all\n" +
            "\t\tselect specid as id,PhotoPath as name from (select specid,PhotoPath,row_number() over(partition by specid order by PhotoId desc) num  \n" +
            "\t\tfrom CV_Photo WITH(NOLOCK)  where TypeClassId=52 )as CV_Photo\n" +
            "\t\twhere num=1")
    List<KeyValueDto<Integer,String>> getSpecPngLogo();

    @Select("select parent AS Name from spec_new a with(nolock)  where isForeignCar=1  group by parent")
    List<Integer> getAllSeriesForeignCar();

    @Select("SELECT  SpecId as [key],HorsePower  as [value] FROM (\n" +
            "            SELECT  SpecId, ISNULL(specEngineHP,0) as HorsePower FROM specview WITH(NOLOCK)\n" +
            "            UNION ALL\n" +
            "            SELECT  SpecId, ISNULL(HorsePower,0) as HorsePower FROM CV_SpecView WITH(NOLOCK)\n" +
            "          ) AS T")
    List<KeyValueDto<Integer,Integer>> getAllSpecHorsePower();

    @Select("SELECT  SpecId as [key],HorsePower  as [value] FROM (\n" +
            "            SELECT  SpecId, ISNULL(specEngineHP,0) as HorsePower FROM specview WITH(NOLOCK)\n" +
            "            UNION ALL\n" +
            "            SELECT  SpecId, ISNULL(HorsePower,0) as HorsePower FROM CV_SpecView WITH(NOLOCK)\n" +
            "          ) AS T  where specid = #{specid} ")
    KeyValueDto<Integer,Integer> getSpecHorsePower(int specid);

    @Select("SELECT  SpecId AS [key],FlowMode AS [value]  FROM (\n" +
            "            SELECT  SpecId , ISNULL(FlowMode,0) as FlowMode FROM specview WITH(NOLOCK)\n" +
            "            UNION ALL\n" +
            "            SELECT  SpecId, ISNULL(FlowMode,0) as FlowMode  FROM CV_SpecView WITH(NOLOCK)\n" +
            "          ) AS T")
    List<KeyValueDto<Integer,Integer>> getAllSpecFlowMode();

    @Select("SELECT  SpecId AS [key],FlowMode AS [value]  FROM (\n" +
            "            SELECT  SpecId , ISNULL(FlowMode,0) as FlowMode FROM specview WITH(NOLOCK)\n" +
            "            UNION ALL\n" +
            "            SELECT  SpecId, ISNULL(FlowMode,0) as FlowMode  FROM CV_SpecView WITH(NOLOCK)\n" +
            "          ) AS T where specid = #{specid} ")
    KeyValueDto<Integer,Integer> getSpecFlowMode(int specid);


    @Select(" select id as name from spec_new  where SpecState = 20 and datediff(day,timemarket,GETDATE())<=30\n" +
            "        union \n" +
            "        select id as name from CV_Spec where SpecState = 20 and datediff(day,timemarket,GETDATE())<=30")
    List<Integer> getNewCarSpecIds();

    @Select("      select id as [key],name as [value]  from (\n" +
            "\t\tSELECT id,IsTaxRelief as name FROM spec_new with(nolock) where specstate>=10 and specstate<=40 \n" +
            "\t\tUNION ALL \n" +
            "\t\tselect id,IsTaxRelief as name from CV_Spec with(nolock) where specstate>=10 and specstate<=40 )\n" +
            "\t\tas t where name>1\n")
    @AutoCache(expireIn = 60)
    List<KeyValueDto<Integer,Integer>> getSpecJianShui();


    @SelectProvider(value = SpecProvider.class, method = "getJianShuiList")
    List<KeyValueDto<Integer,Integer>> getSpecJianShuiList(List<Integer> specIds);

    @Select("SELECT Id as [key],model as [value] FROM Spec_new WITH (NOLOCK) \n" +
            "  UNION ALL \n" +
            "SELECT Id as [key],SpecName as [value] FROM CV_Spec WITH (NOLOCK) ;")
    @AutoCache(expireIn = 60)
    List<KeyValueDto<Integer,String>> getSpecAllName();
}

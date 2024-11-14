package com.autohome.car.api.data.popauto.providers;

import com.autohome.car.api.common.BaseConfig.Spec;

public class SpecConfigProvider {

    public String getSpecConfigs(int specId){

        if(Spec.isCvSpec(specId)){
            return "SELECT SpecId,0 configid,Item,Name,ItemValue, Porder as pordercls, Sorder as ordercls FROM CV_SpecParamView WITH(NOLOCK) WHERE SpecId = #{specId} ORDER BY Porder,Sorder;";
        }else{
            return " SELECT SpecId,configid,Item,Name,valu AS ItemValue, pordercls, ordercls FROM SpecConfig WITH(NOLOCK) WHERE SpecId = #{specId}  ORDER BY pordercls,ordercls;";
        }
    }

    public String getSpecConfigRelations(int specId){
        if(Spec.isCvSpec(specId)){
            return "SELECT SpecId,ItemId,ItemValueId as valueId FROM CV_ConfigSpecRelation WITH(NOLOCK) WHERE SpecId = #{specId}";
        }else{
            return "SELECT SpecId,ItemId,ItemValueId as valueId FROM ConfigSpecRelation WITH(NOLOCK) WHERE SpecId = #{specId}";
        }
    }

    public String getSpecConfigSubItems(int specId){
        if(Spec.isCvSpec(specId)){
            return "select SpecId,A.ItemId,A.SubItemId,A.SubValue from CV_ConfigSubItemSpecRelation  as A with(nolock) inner join ConfigSubItem as B with(Nolock) on A.SubItemId = B.Id where SpecId = #{specId} order by B.Sort;";
        }else{
            return "select SpecId,A.ItemId,A.SubItemId,A.SubValue from ConfigSubItemSpecRelation  as A with(nolock) inner join ConfigSubItem as B with(Nolock) on A.SubItemId = B.Id where SpecId = #{specId} order by B.Sort;";
        }
    }


    public String getSpecConfigSubItemValues(int specId){
        if(Spec.isCvSpec(specId)){
            return "SELECT A.specid,A.ItemId,A.ItemValueId , B.SubItemId,B.Value AS SubValue FROM cv_ConfigSpecRelation AS A WITH(NOLOCK) INNER JOIN ConfigSubItemValueRelation  AS B WITH(NOLOCK) ON A.ItemValueId = B.ItemValueId where SpecId = #{specId} " ;
        }else{
            return "SELECT A.specid,A.ItemId,A.ItemValueId , B.SubItemId,B.Value AS SubValue FROM ConfigSpecRelation AS A WITH(NOLOCK) INNER JOIN ConfigSubItemValueRelation  AS B WITH(NOLOCK) ON A.ItemValueId = B.ItemValueId where SpecId = #{specId}";
        }
    }

}

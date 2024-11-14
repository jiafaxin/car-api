package com.autohome.car.api.data.popauto.providers;

import com.autohome.car.api.common.BaseConfig.Spec;

public class ParamSubItemProvider {

    static final String GetSpecOilLabe = "select  A.specid as [key],B.SubParamName as [value] from ParamSpecSubItemValueRelation as A with(nolock) inner join ParamSubItem as B with(nolock) on A.SubParamId =B.SubParamId  where A.ParamId =57 and A.SubParamId <> 98";
    static final String GetSpecOilLabe4CV = "select  A.specid as [key],B.SubParamName as [value] from ParamSpecSubItemValueRelation_CV as A with(nolock) inner join ParamSubItem as B with(nolock) on A.SubParamId =B.SubParamId  where A.ParamId =57 and A.SubParamId <> 98";

    public String getSpecOilLabe(int specId){
        if(Spec.isCvSpec(specId)){
            return GetSpecOilLabe4CV.concat(" AND specid = #{specId}");
        }else{
            return GetSpecOilLabe.concat(" AND specid = #{specId}");
        }
    }

    public String getAllSpecOilLabe() {
        return GetSpecOilLabe.concat("\n UNION ALL \n").concat(GetSpecOilLabe4CV);
    }
}

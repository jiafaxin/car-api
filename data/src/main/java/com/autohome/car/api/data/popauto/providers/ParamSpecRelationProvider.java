package com.autohome.car.api.data.popauto.providers;

import com.autohome.car.api.common.BaseConfig.Spec;

public class ParamSpecRelationProvider {

    static final String GetSpecWheelBase = "select SpecId as [key] ,ParamValue as [value] from ParamSpecRelation with(nolock) where ParamId =17 and len(ParamValue)>0";
    static final String GetSpecWheelBase4CV = "select SpecId as [key],ParamValue as [value]  from ParamSpecRelation_CV with(nolock) where ParamId =17 and len(ParamValue)>0";

    public String getSpecWheelBase(int specId){
        if(Spec.isCvSpec(specId)){
            return GetSpecWheelBase4CV.concat(" AND SpecId = #{specId}");
        }else{
            return GetSpecWheelBase.concat(" AND SpecId = #{specId}");
        }
    }

    public String getAllSpecWheelBase(){
        return GetSpecWheelBase.concat("\nUNION ALL\n").concat(GetSpecWheelBase4CV);
    }
}

package com.autohome.car.api.data.popauto.providers;

import com.autohome.car.api.common.BaseConfig.Spec;
import org.apache.ibatis.annotations.Select;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class SpecProvider {

    public String getSpecRealTestUrl(int specId) {
        if (Spec.isCvSpec(specId)) {
            return "select id as [key],speed_url as  [value] from CV_Spec with(nolock) where len(speed_url)>0 AND id = #{specId}";
        }
        return "select id as [key] ,speed_url as [value] from spec_new with(nolock) where len(speed_url)>0 AND id = #{specId}";
    }

    public String getJianShuiList(List<Integer> specIds){
        return "      select id as [key],name as [value]  from (\n" +
                "\t\tSELECT id,IsTaxRelief as name FROM spec_new with(nolock) where specstate>=10 and specstate<=40 \n" +
                "\t\tUNION ALL \n" +
                "\t\tselect id,IsTaxRelief as name from CV_Spec with(nolock) where specstate>=10 and specstate<=40 )\n" +
                "\t\tas t where name>1 and id in ( " + Strings.join(specIds, ',')+")";
    }
}

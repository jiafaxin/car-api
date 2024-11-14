package com.autohome.car.api.data.popauto.providers;

import com.autohome.car.api.common.BaseConfig.Spec;
import org.aspectj.apache.bcel.generic.RET;

public class SpecPhotoProvider {

    public String getPngPhoto(int specId) {

        if(Spec.isCvSpec(specId))
            return getCVPngPhoto(specId);

        return String.format("" +
                        "select specid as id,filepath as name from (\n" +
                        "\tselect specid,filepath,row_number() over(partition by specid order by id desc) num  \n" +
                        "\tfrom car_spec_photo WITH(NOLOCK) \n" +
                        "\twhere typeid=52 AND IsDelete=0 %s\n" +
                        ")as photo where num=1",
                specId <= 0 ? "" : " AND specid = #{specId} "
        );
    }

    public String getCVPngPhoto(int specId){
        return String.format("" +
                "select specid as id,PhotoPath as name from (\n" +
                "\tselect specid,PhotoPath,row_number() over(partition by specid order by PhotoId desc) num  \n" +
                "\tfrom CV_Photo WITH(NOLOCK)  \n" +
                "\twhere TypeClassId=52 %s\n" +
                ")as CV_Photo where num=1",
                specId <= 0? "" : " AND specid = 1337"
        );
    }

    public String getAllPngLogo(){
        return "select specid as [key],filepath as [value] from (\n" +
                "\tselect specid,filepath,row_number() over(partition by specid order by id desc) num  \n" +
                "\tfrom car_spec_photo WITH(NOLOCK) \n" +
                "\twhere typeid=52 AND IsDelete=0\n" +
                ")as photo where num=1\n" +
                "\n" +
                "UNION ALL\n" +
                "\n" +
                "select specid as [key],PhotoPath as [value] from (\n" +
                "\tselect specid,PhotoPath,row_number() over(partition by specid order by PhotoId desc) num  \n" +
                "\tfrom CV_Photo WITH(NOLOCK)  \n" +
                "\twhere TypeClassId=52\n" +
                ")as CV_Photo where num=1";
    }


}

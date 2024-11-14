package com.autohome.car.api.data.popauto.providers;

import org.apache.logging.log4j.util.Strings;

public class AppPictureProvider {

    public String getAppNewPictureByTime(String editTime, int size){
        String sql = "with title as(select Top %s a.id, a.brandid, a.SeriesId,a.SpecId,Title,PublishTime,looptype,BigImg,DisplayType,\n" +
                "        isnull(bigimgtype,1) bigimgtype,edittime from AppNewPicture_Title a  where PublishTime<=GETDATE() %s  order by PublishTime desc\n" +
                "        )\n" +
                "        select  a.id, a.brandid, a.SeriesId,a.SpecId,Title,PublishTime,looptype,BigImg,DisplayType,\n" +
                "        b.PicTypeId,b.picpath ,b.picid,a.bigimgtype,edittime from title a inner join AppNewPicture_Pic b on a.Id=b.titleid\n" +
                "        order by PublishTime desc";

        String sqlwhere = "";
        if(Strings.isNotBlank(editTime)){
            sqlwhere = " AND edittime>'" + editTime + "'";
        }
        sql = String.format(sql, size, sqlwhere);
        return sql;
    }
}

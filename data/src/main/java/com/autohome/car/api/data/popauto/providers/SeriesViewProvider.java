package com.autohome.car.api.data.popauto.providers;

import com.autohome.car.api.common.SpecStateEnum;

import com.autohome.car.api.data.popauto.entities.SeriesOnlyElectricEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SeriesViewProvider {

    public String getSeriesInfoByLevelId(int levelId) {
        StringBuilder sb = new StringBuilder("SELECT TOP 11 SeriesId,SeriesPhotoNum FROM SeriesView WITH (NOLOCK) WHERE ");
        switch (levelId) {
            case 9:
                sb.append(" LevelId >= 16 AND LevelId <= 20 ");
                break;
            case 8:
                sb.append(" LevelId >= 21 AND LevelId <= 24 ");
                break;
            case 14:
            case 15:
                sb.append(" LevelId >= 14 AND LevelId <= 15 ");
                break;
            default:
                sb.append( " LevelId = #{levelId} ");
        }
        sb.append(" ORDER BY SeriesRank DESC ");

        return sb.toString();
    }

    public String getSeriesByPageLevelId(int levelId, SpecStateEnum state, int start, int end) {
        StringBuilder sb = new StringBuilder("SELECT A.seriesId as [key],A.SeriesIspublic as [value] FROM\n" +
                "   (\n" +
                "   SELECT SeriesId,SeriesIspublic,ROW_NUMBER() OVER(ORDER BY SeriesRank DESC) AS RowIndex FROM SeriesView WITH (NOLOCK) %s\n" +
                "    ) AS A\n" +
                "    WHERE A.RowIndex >= #{start} AND A.RowIndex <= #{end};");
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        switch (levelId) {
            case 9:
                where.append(" AND LevelId >= 16 AND LevelId <= 20 ");
                break;
            case 8:
                where.append(" AND LevelId=8 OR (LevelId >= 21 AND LevelId <= 24) ");
                break;
            case 14:
            case 15:
                where.append(" AND LevelId >= 14 AND LevelId <= 15 ");
                break;
            default:
                where.append( " AND LevelId = #{levelId} ");
        }
        switch (state)
        {
            case STOP_SELL:
                where.append( " AND SeriesIspublic=2 ");
                break;
            case SELL_3:
                where.append( " AND SeriesIspublic=0 ");
                break;
            case SELL_12:
                where.append(  " AND SeriesIspublic=1 ");
                break;
            case SELL_28:
                where.append(" AND SeriesIspublic>=1 ");
                break;
            case SELL_15:
                where.append( " AND SeriesIspublic<=1 ");
                break;
            case SELL_31:
                where.append( " ");
                break;
        }
        return String.format(sb.toString(), where.toString());
    }

    public String getSeriesCountByPageLevelId(int levelId, SpecStateEnum state) {
        StringBuilder sb = new StringBuilder("SELECT  COUNT(1) FROM SeriesView WITH (NOLOCK) %s");
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        switch (levelId) {
            case 9:
                where.append(" AND LevelId >= 16 AND LevelId <= 20 ");
                break;
            case 8:
                where.append(" AND LevelId=8 OR (LevelId >= 21 AND LevelId <= 24) ");
                break;
            case 14:
            case 15:
                where.append(" AND LevelId >= 14 AND LevelId <= 15 ");
                break;
            default:
                where.append( " AND LevelId = #{levelId} ");
        }
        switch (state)
        {
            case STOP_SELL:
                where.append( " AND SeriesIspublic=2 ");
                break;
            case SELL_3:
                where.append( " AND SeriesIspublic=0 ");
                break;
            case SELL_12:
                where.append(  " AND SeriesIspublic=1 ");
                break;
            case SELL_28:
                where.append(" AND SeriesIspublic>=1 ");
                break;
            case SELL_15:
                where.append( " AND SeriesIspublic<=1 ");
                break;
            case SELL_31:
                where.append( " ");
                break;
        }

        return String.format(sb.toString(), where.toString());
    }

    public String getSeriesHotCount(String seriesIspublic){
        if(StringUtils.isBlank(seriesIspublic)){
            return "SELECT  COUNT(1) FROM SeriesView WITH (NOLOCK)";
        }
        else {
            return "SELECT  COUNT(1) FROM SeriesView WITH (NOLOCK)  " + seriesIspublic;
        }
    }

    public String getSeriesHot(int start, int end, String seriesIspublic){
        if(StringUtils.isBlank(seriesIspublic)){
            return "SELECT A.seriesId,seriesPriceMin,seriesPriceMax,A.SeriesIspublic FROM\n" +
                    "(\n" +
                    "SELECT SeriesId,SeriesIspublic,seriesPriceMin,seriesPriceMax,ROW_NUMBER() OVER(ORDER BY SeriesRank DESC) AS RowIndex FROM SeriesView WITH (NOLOCK) \n" +
                    ") AS A\n" +
                    "WHERE A.RowIndex >= "+start+" AND A.RowIndex <= "+end;
        }
        else {
            return "SELECT A.seriesId,seriesPriceMin,seriesPriceMax,A.SeriesIspublic FROM\n" +
                    "(\n" +
                    "SELECT SeriesId,SeriesIspublic,seriesPriceMin,seriesPriceMax,ROW_NUMBER() OVER(ORDER BY SeriesRank DESC) AS RowIndex FROM SeriesView WITH (NOLOCK)  "+seriesIspublic+"\n" +
                    ") AS A\n" +
                    "WHERE A.RowIndex >= "+start+" AND A.RowIndex <= "+end;
        }
    }



}

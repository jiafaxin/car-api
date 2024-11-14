package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.LevelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarSpecLevelMapper {
    @Select("SELECT Id ,Name ,Dir,[Description] FROM car_spec_jb WITH(NOLOCK)\n" +
            "UNION ALL\n" +
            "SELECT 9 AS id,'SUV' AS name,null as Dir,null as [Description]")
    List<LevelEntity> getAllLevel();
}

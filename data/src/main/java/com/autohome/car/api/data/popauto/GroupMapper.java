package com.autohome.car.api.data.popauto;

import com.autohome.car.api.common.KeyValueDto;
import com.autohome.car.api.data.popauto.entities.GroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMapper {
    @Select("select CountryId,Country from [group] where  id = #{id}")
    GroupEntity getGroup(int id);

    @Select("select id,CountryId,Country from [group]")
    List<GroupEntity> getAllGroup();

    @Select("SELECT ID as [key],name as [value] FROM [Group] WITH(NOLOCK) WHERE Name = #{brandName}")
    KeyValueDto<Integer,String> getGroupByName(String brandName);
}

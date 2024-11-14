package com.autohome.car.api.data.popauto;

import com.autohome.car.api.data.popauto.entities.epiboly.EpibolyAiVideoOrderDetailEntity;
import com.autohome.car.api.data.popauto.entities.epiboly.EpibolyAiVideoOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EpibolyAiVideoMapper {

    @Select("select  A.orderId ,A.orderStatus,a.taskId,B.brandId,B.seriesId,B.specId,B.taskStatus\n" +
            "from EpibolyAiVideoOrder as A\n" +
            " join EpibolyAiVideoTask as B on A.taskId = B.taskId\n" +
            "where A.is_del = 0\n" +
            " and B.is_del = 0  order by A.created_stime desc")
    List<EpibolyAiVideoOrderEntity> getEpibolyAiVideoOrderAll();

    @Select("select sourceId,orderId,status,pointId,pointName " +
            "from EpibolyAiVideoOrderDetail where is_del = 0 and orderId = #{orderId} \n")
    List<EpibolyAiVideoOrderDetailEntity> getEpibolyAiVideoOrderDetailByOrderId(@Param("orderId") int orderId);


    @Select("select distinct orderId from EpibolyAiVideoOrder where is_del = 0")
    List<Integer> getOrderIdAll();

}

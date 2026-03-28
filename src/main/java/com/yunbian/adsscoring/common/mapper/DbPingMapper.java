package com.yunbian.adsscoring.common.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DbPingMapper {

    Integer ping();
}
package com.arch.monitor_data.controller;

import com.arch.monitor_data.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Api(tags = "数据接口——hezhengzhi.ty")
@RestController
@RequestMapping(value="/data", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Slf4j
public class DataController {

    @GetMapping(value = "/findData")
    @ApiOperation(value = "查询折线图数据", notes = "查询折线图数据")
    public ConcurrentHashMap <String, List<String>> findData() {
        return FileService.map ;
    }


}

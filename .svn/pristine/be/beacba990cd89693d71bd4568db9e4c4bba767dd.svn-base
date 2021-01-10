package com.jvli.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jvli.project.config.MailProperties;
import com.jvli.project.config.WebSiteProperties;
import com.jvli.project.entity.OrderRecord;
import com.jvli.project.mapper.OrderRecordMapper;
import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;

/**
 * created by 2020/12/31
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/test")
public class TestController {
	
	private static final Logger logger = LoggerFactory.getLogger(TestController.class); 
	
	@Autowired
	private OrderRecordMapper orderRecordMapper;
	
	@Autowired
	private WebSiteProperties webSiteProperties;
	
	@RequestMapping(value = "/print/property",method = RequestMethod.GET)
	public Map<String, Object> printProperty() {
		System.out.println(webSiteProperties);
		Map<String, Object> webSiteMap = new HashMap<>();
		webSiteMap.put("ip", webSiteProperties.getIp());
		webSiteMap.put("port", webSiteProperties.getPort());
		webSiteMap.put("name", webSiteProperties.getName());
		return webSiteMap;
	}
	
	@RequestMapping(value = "/printmsg",method = RequestMethod.GET)
	public BaseResponse rabbitmq() {
		// TODO Auto-generated method stub
		String msg = "rabbitmq的第二阶段-spring boot整合rabbitmq";
		return new BaseResponse<>(StatusCode.SUCCESS,msg);
	}
	
	/**
	 * 
	 * @Description: 获取订单列表
	 * @return BaseResponse<List<OrderRecord>> 
	 * @Date: 2020年12月31日  下午4:52:00
	 * @Author: luhongjun
	 */
	@RequestMapping(value = "/queryList",method = RequestMethod.GET)
	public BaseResponse<List<OrderRecord>> dataList() {
		  List<OrderRecord> orderList = orderRecordMapper.selectAll();
		  return new BaseResponse<>(StatusCode.SUCCESS,orderList);
	}
}

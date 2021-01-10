package com.jvli.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jvli.project.dto.LogDto;

@Service
public class CommonLogService {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonLogService.class);
	
	/**
	 * 通用的写日志服务逻辑
	 */
	public void insertLog(LogDto dto) {
		logger.info("接收到的系统日志数据：{}",dto);
	}
}

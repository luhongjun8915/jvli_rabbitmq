package com.jvli.project.service;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jvli.project.entity.Product;
import com.jvli.project.entity.ProductRobbingRecord;
import com.jvli.project.mapper.ProductMapper;
import com.jvli.project.mapper.ProductRobbingRecordMapper;

@Service
public class ConcurrencyService {

	private static final Logger logger = LoggerFactory.getLogger(ConcurrencyService.class);
	
	private static final String ProductNo = "product_10010";
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private ProductRobbingRecordMapper productRobbingRecordMapper;
	
	/**
	 * 处理抢单
	 */
	public void manageRabbiting(String mobile) {
		try {
			Product product = productMapper.selectByProductNo(ProductNo);
			if(product!=null && product.getTotal()>0) {
				int result=productMapper.updateTotal(product);
				if(result>0) {
					ProductRobbingRecord entity = new ProductRobbingRecord();
					entity.setMobile(mobile);
					entity.setProductId(product.getId());
					productRobbingRecordMapper.insertSelective(entity);
					
				}
			}
		} catch (Exception e) {
			logger.error("处理抢单发生异常：{}",mobile);
		}
	}
}

package com.jvli.project.listenerevent;

import org.springframework.context.ApplicationEvent;

public class PushOrderRecordEvent extends ApplicationEvent{
	
	private String orderNo;
	
	private String orderType;
	
	public PushOrderRecordEvent(Object source, String orderNo, String orderType) {
		super(source);
		this.orderNo = orderNo;
		this.orderType = orderType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	@Override
	public String toString() {
		return "PushOrderRecordEvent [orderNo=" + orderNo + ", orderType=" + orderType + "]";
	}
	
	
}

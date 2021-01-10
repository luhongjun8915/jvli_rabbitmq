package com.jvli.project.service;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitService {
	
	private static final Logger logger = LoggerFactory.getLogger(InitService.class);
	
	public static final int ThreadNum = 500;
	
	private static int mobile = 0;

	
	@Autowired
	private CommonMqService commonMqService;
	
	public void generateMultiThread() {
		logger.info("开始初始化线程数>>>>>>>>>>>>>>");
		try {
			CountDownLatch countDownLatch = new CountDownLatch(1);
			for (int i = 0; i <ThreadNum; i++) {
				new Thread(new RunThread(countDownLatch)).start();
			}
			countDownLatch.countDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class RunThread implements Runnable{
		
		private final CountDownLatch startLatch;
		
		public RunThread(CountDownLatch startLatch) {this.startLatch = startLatch;}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				startLatch.await();
				mobile = (int) ((Math.random()*9+1)*10000000);
				//new Random().nextInt(89999999)+10000000;
				commonMqService.sendRabbitingMsg(String.valueOf(mobile));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}
}

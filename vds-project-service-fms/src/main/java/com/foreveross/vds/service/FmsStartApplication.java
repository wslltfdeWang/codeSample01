package com.foreveross.vds.service;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.foreveross.vds.service.common.util.ConstantUtil;

//@SpringCloudApplication
@SpringBootApplication
@MapperScan("com.foreveross.vds.service.**.mapper")
@EnableTransactionManagement
// @EnableEurekaClient
public class FmsStartApplication {

	private static final Logger _logger = LoggerFactory
			.getLogger(FmsStartApplication.class);

	public static void main(String[] args) {
		ApplicationContext ac = SpringApplication.run(
				FmsStartApplication.class, args);

		ConstantUtil.setUrl();
		for (String profile : ac.getEnvironment().getActiveProfiles()) {
			_logger.info("当前服务运行环境为：");
			_logger.info("#######################################");
			_logger.info("#################  {}  ###############", profile);
			_logger.info("#######################################");
		}
	}

}

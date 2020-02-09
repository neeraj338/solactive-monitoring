package com.solactive.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = { "com.solactive.monitoring" })

public class SolactiveMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolactiveMonitoringApplication.class, args);
	}

}
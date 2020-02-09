package com.solactive.monitoring.config;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@Configuration
public class BeanConfiguration {

	@Bean
	@Qualifier("tickTaskExecutor")
	public TaskExecutor tickTaskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(10));
	}

	@Bean
	@Qualifier("statisticsTaskExecutor")
	public TaskExecutor statisticsTaskExecutor() {
		return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(10));
	}
}

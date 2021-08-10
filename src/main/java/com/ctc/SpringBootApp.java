package com.ctc;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableScheduling
@EnableAsync()
public class SpringBootApp { 

	
	// implements CommandLineRunner {
	
	//@Override
	//public void run(String... arg0) throws Exception {
		//TODO
	//}

	private static final Logger log = LoggerFactory.getLogger(SpringBootApp.class);

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringBootApp.class, args);
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(31);
		taskExecutor.setMaxPoolSize(100);
		
		//taskExecutor.setQueueCapacity(31);		
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setAwaitTerminationSeconds(10);
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		taskExecutor.initialize();
		
		return taskExecutor;
		
	}
}
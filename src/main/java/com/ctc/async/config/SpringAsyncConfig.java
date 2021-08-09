package com.ctc.async.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ctc.async.CustomAsyncExceptionHandler;

@Configuration
public class SpringAsyncConfig implements AsyncConfigurer {
	
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    	ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(5);
//        taskExecutor.setMaxPoolSize(10);
//        taskExecutor.setQueueCapacity(10);
//        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Override
    public SimpleAsyncTaskExecutor getAsyncExecutor() {
    	return new SimpleAsyncTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}

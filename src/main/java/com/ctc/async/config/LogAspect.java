package com.ctc.async.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

@Configuration
@Aspect
public class LogAspect {
	private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
	//@Around("execution(* * com.test.aop.controller.*(..))")
	@Around("@annotation(LogExecutionTime)")
	public Object executionAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		String method = joinPoint.getSignature().getName();
		log.info("===============================================");
		log.info("LogExecutionTime check method=" + method+ " start");
		log.info("===============================================");
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start(method);

		Object result = joinPoint.proceed();
		
		stopWatch.stop();
		log.info(stopWatch.prettyPrint()+"MS :"+stopWatch.getTotalTimeMillis()+"\nS:"+stopWatch.getTotalTimeSeconds());
		log.info("===============================================");
		log.info("LogExecutionTime check method=" + method+ " end");
		log.info("===============================================");
		return result;
	}
}

package com.ctc.async;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.ctc.download.util.FileDownload;
import com.ctc.download.util.GzipReader;

@Component
public class AsyncComponent {
	private static final Logger log = LoggerFactory.getLogger(AsyncComponent.class);

	private Map<String, StopWatch> stopWatchMap = new HashMap<String, StopWatch>();

	public StopWatch getStopWatch(String task) {
		return this.stopWatchMap.get(task);
	}

	public void start(String task) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start(task);
		this.stopWatchMap.put(task, stopWatch);
	}

	@Autowired
	ThreadPoolTaskExecutor taskExecutor;

	/**
	 * 파일 읽기 CompletableFuture를 이용한 비동기 구현
	 * 
	 * @param file
	 * @param localFilename
	 * @return
	 */
	public CompletableFuture<Void> asyncReadFile(Supplier<String> supplier) {

		return CompletableFuture.supplyAsync(supplier, taskExecutor).thenAccept((f) -> {

			// 비동기 각각 걸리는 시간 체크
			StopWatch stopWatch = this.getStopWatch(f);
			stopWatch.stop();
			log.info(stopWatch.prettyPrint() + "MS :" + stopWatch.getTotalTimeMillis() + "\nS:"
					+ stopWatch.getTotalTimeSeconds());

		}).exceptionally((ex) -> {
			log.error(ex.getMessage());
			return null;
		});
	}

	/**
	 * CompletableFuture를 이용한 비동기 구현
	 * 
	 * @param url
	 * @param localFilename
	 * @return
	 */
	public CompletableFuture<String> asyncDownloadUrl(String url, String localFilename) {

		return CompletableFuture.supplyAsync(() -> {
			this.start(url);
			try {
				FileDownload.downloadWithJavaNIO(url, localFilename);
			} catch (IOException e) {
				throw new CompletionException(e);
			}
			return localFilename;
		}, taskExecutor).exceptionally((ex) -> {
			log.error(ex.getMessage());
			return null;
		});

	}

	@Async
	public void asyncMethodWithVoidReturnType() {
		System.out.println("Execute method asynchronously. " + Thread.currentThread().getName());
	}

	@Async
	public Future<String> asyncMethodWithReturnType() {
		System.out.println("Execute method asynchronously " + Thread.currentThread().getName());
		try {
			Thread.sleep(5000);
			return new AsyncResult<>("hello world !!!!");
		} catch (final InterruptedException e) {

		}

		return null;
	}

	@Async("threadPoolTaskExecutor")
	public void asyncMethodWithConfiguredExecutor() {
		System.out.println("Execute method asynchronously with configured executor" + Thread.currentThread().getName());
	}

	@Async
	public void asyncMethodWithExceptions() throws Exception {
		throw new Exception("Throw message from asynchronous method. ");
	}

}

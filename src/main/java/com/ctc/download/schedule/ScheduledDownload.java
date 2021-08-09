package com.ctc.download.schedule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import com.ctc.async.AsyncComponent;
import com.ctc.async.config.LogExecutionTime;
import com.ctc.download.util.DateUtil;
import com.ctc.download.util.FileUtil;
import com.ctc.download.util.GzipReader;
//import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ScheduledDownload {
	private static final Logger log = LoggerFactory.getLogger(ScheduledDownload.class);
	@Autowired
	private AsyncComponent asyncComponent;
	
	@Autowired
	private KafkaTemplate<String, Object> template;

	//@Scheduled(fixedRate = 36000000)
	@LogExecutionTime
	public void readFiles() throws IOException {
		String dir = "./backup/spot_index/BTCUSD/";
		List<CompletableFuture<Void>> list = new ArrayList<CompletableFuture<Void>>();
		FileUtil.listFiles("./backup/spot_index/BTCUSD").forEach((filename) -> {
			log.info("readFileName : "+filename);
			
			list.add(asyncComponent.asyncReadFile(() -> {
				asyncComponent.start(filename);
				int[] line = { 0 };
				GzipReader.readGzip_BufferedReader(dir + filename).forEach(m -> {
					++line[0];
					//log.info(filename + ":" + (++line[0]) + ":" + m.toString());					
					this.template.send("BTCUSD-index-price",m.get("startAt"), m);
				});
				
				log.info(filename +" file line  count:" + (line[0]));
				
				return filename;
			}

			));//add
		});//forEach

		// 비동기 전체 함수 걸리는 시간 체크하기 위한 로직
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] array = list.stream().toArray(CompletableFuture[]::new);
		CompletableFuture.allOf(array).join();
	}

	@Scheduled(fixedRate = 36000000)
	@LogExecutionTime
	public void downloadFile() throws InterruptedException, ExecutionException {
		String endDate = "2019-10-10";//DateUtil.AddDay(DateUtil.now("yyyy-MM-dd"), -1, "yyyy-MM-dd");
		String startDate = "2019-09-30";

		List<CompletableFuture<Void>> list = new ArrayList<CompletableFuture<Void>>();

		while (!endDate.equals(startDate)) {

			startDate = DateUtil.AddDay(startDate, 1, "yyyy-MM-dd");

			String url = "https://public.bybit.com/spot_index/BTCUSD/BTCUSD" + startDate + "_index_price.csv.gz";
			String localFilename = "./backup/spot_index/BTCUSD/BTCUSD" + startDate + "_index_price.csv.gz";

			if (new File(localFilename).exists()) {
				continue;
			}

			list.add(asyncComponent.asyncDownloadUrl(url, localFilename).thenAccept((filename) -> {

				if (filename == null) {
					log.info("file download failed : " + filename);
				} else {
					log.info("file download success : " + filename);

					GzipReader.readGzip_BufferedReader(filename).forEach(m -> {
						this.template.send("BTCUSD-index-price",m.get("startAt"), m);
					});

					// 비동기 각각 걸리는 시간 체크
					StopWatch stopWatch = asyncComponent.getStopWatch(url);
					stopWatch.stop();
					log.info(stopWatch.prettyPrint() + "MS :" + stopWatch.getTotalTimeMillis() + "\nS:"
							+ stopWatch.getTotalTimeSeconds());

				}

			}).exceptionally((ex) -> {// thenAccept 에러 처리
				log.error(ex.getMessage());
				return null;
			})

			);

		} // while end

		// 비동기 전체 함수 걸리는 시간 체크하기 위한 로직
		@SuppressWarnings("unchecked")
		CompletableFuture<Void>[] array = list.stream().toArray(CompletableFuture[]::new);
		CompletableFuture.allOf(array).join();
	}

}

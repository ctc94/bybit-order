package com.ctc.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootApp {
	private static final Logger log = LoggerFactory.getLogger(SpringBootApp.class);

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringBootApp.class, args);
	}

	// private Gson gson = new Gson();

	@Bean
	public CommandLineRunner completableFuture() {
		return (args) -> {

			CompletableFuture<String> completableFuture = CompletableFuture.completedFuture("aaaa");

			completableFuture.thenAccept((s) -> {
				System.out.println("return : " + s);
			});

		};
	}

	// @Bean
	public CommandLineRunner getDockerEvent() {
		return (args) -> {

			StreamGobbler streamGobbler = new StreamGobbler(new String[] { "env" }, (out) -> {
				log.info(out);
			});
			System.out.println("========================= streamGobbler run ");
			Executors.newSingleThreadExecutor().submit(streamGobbler);

			// 도커머신 명치 리스트 수신 시작
			StreamGobbler streamGobbler1 = new StreamGobbler(
					new String[] { "sh", "-c", "echo \"$(docker-machine ls -q --filter state=Running)\"" }, (node) -> {
						log.info("streamGobbler1 : " + node);
						// 노드별 도커 이벤트 수신 시작
						StreamGobbler streamGobbler2 = new StreamGobbler(
								new String[] { "sh", "-c", "./docker_event.sh " + node }, (json) -> {
									log.info(node + " : " + json);
								});
						Executors.newSingleThreadExecutor().submit(streamGobbler2);
						// 도커 이벤트 수신 종료
					});
			Executors.newSingleThreadExecutor().submit(streamGobbler1);
			// 도커머신 명치 리스트 수신 종료

		};
	}
	
	private static class StreamGobbler implements Runnable {
		private String[] command;
		private Consumer<String> consumer;

		public StreamGobbler(String[] command, Consumer<String> consumer) {
			this.command = command;
			this.consumer = consumer;
		}

		public void run() {
			try {
				System.out.println("=============" + Arrays.asList(this.command).toString() + " run ==============");

				ProcessBuilder builder = new ProcessBuilder();

				builder.command(command);

				builder.directory(new File("/data/git/dockerEvent"));

				Process process = builder.start();

				new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8")).lines().forEach(consumer);
				int exitCode = process.waitFor();
				System.out.println(Arrays.asList(this.command).toString() + " exitCode " + exitCode);
				assert exitCode == 0;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
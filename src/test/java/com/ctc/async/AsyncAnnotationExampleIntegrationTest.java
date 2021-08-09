package com.ctc.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AsyncAnnotationExampleIntegrationTest {

    @Autowired
    private AsyncComponent asyncComponent;

    // tests

    @Test
    public void testAsyncAnnotationForMethodsWithVoidReturnType() {
        System.out.println("Start - invoking an asynchronous method. " + Thread.currentThread().getName());
        asyncComponent.asyncMethodWithVoidReturnType();
        System.out.println("End - invoking an asynchronous method. ");
    }

    @Test
    public void testAsyncAnnotationForMethodsWithReturnType() throws InterruptedException, ExecutionException {
        System.out.println("Start - invoking an asynchronous method. " + Thread.currentThread().getName());
        final Future<String> future = asyncComponent.asyncMethodWithReturnType();

        while (true) {
            if (future.isDone()) {
                System.out.println("Result from asynchronous process - " + future.get());
                break;
            }
            System.out.println("Continue doing something else. ");
            Thread.sleep(1000);
        }
    }

    @Test
    public void testAsyncAnnotationForMethodsWithConfiguredExecutor() {
        System.out.println("Start - invoking an asynchronous method. ");
        asyncComponent.asyncMethodWithConfiguredExecutor();
        System.out.println("End - invoking an asynchronous method. ");
    }

    @Test
    public void testAsyncAnnotationForMethodsWithException() throws Exception {
        System.out.println("Start - invoking an asynchronous method. ");
        //asyncComponent.asyncMethodWithExceptions();
        String url = "c:/Users/mir94/OneDrive/정리문서/bybit데이터/index_price/backup/BTCUSD2020-10-01_index_price.csv";
		asyncComponent.asyncDownloadUrl(url, "BTCUSD2020-10-01_index_price.csv");
		Thread.sleep(10000);
		System.out.println("End - invoking an asynchronous method. ");
        
    }

}

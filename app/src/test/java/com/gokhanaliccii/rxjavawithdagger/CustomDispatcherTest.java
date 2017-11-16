package com.gokhanaliccii.rxjavawithdagger;

import com.gokhanaliccii.rxjavawithdagger.model.User;
import com.gokhanaliccii.rxjavawithdagger.rest.RestAdapter;
import com.google.gson.Gson;

import org.junit.Rule;
import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Created by gokhan on 16/11/17.
 */

public class CustomDispatcherTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Test
    public void should_CallServerUntilMaxCallCount() {
        int failedRespondCount = 6;
        final int maxCallCount = 3;
        String dummyResponse = new Gson().toJson(new User("gokhan"));

        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody(dummyResponse);
        mockResponse.setResponseCode(500);
        for (int i = 0; i < failedRespondCount; i++) {
            mockWebServer.enqueue(mockResponse);
        }

        mockResponse.setResponseCode(200);
        mockWebServer.enqueue(mockResponse);

        String apiUrl = mockWebServer.url("/").toString();

        RestAdapter restAdapter = RestAdapter.getInstance(apiUrl);
        restAdapter.restClient()
                .getUser().retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.zipWith(Observable.range(1, maxCallCount), new BiFunction<Throwable, Integer, Object>() {
                    @Override
                    public Object apply(Throwable throwable, Integer integer) throws Exception {
                        return integer;
                    }
                });
            }
        }).subscribe();

        int requestCount = mockWebServer.getRequestCount();
        System.out.println("count:" + requestCount);
    }

    @Test
    public void should_CallUserServiceCorrectly() throws InterruptedException {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String path = request.getPath();
                System.out.println(path);

                if (path.equals("getUser")) {
                    String dummyResponse = new Gson().toJson(new User("gokhan"));
                    return new MockResponse().setBody(dummyResponse);
                }


                return null;
            }
        };

        mockWebServer.setDispatcher(dispatcher);

        String apiUrl = mockWebServer.url("/").toString();

        RestAdapter restAdapter = RestAdapter.getInstance(apiUrl);
        restAdapter.restClient().getUser().subscribe();


    }

}

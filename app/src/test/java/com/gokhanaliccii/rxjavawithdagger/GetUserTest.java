package com.gokhanaliccii.rxjavawithdagger;

import com.gokhanaliccii.rxjavawithdagger.di.RestClient;
import com.gokhanaliccii.rxjavawithdagger.model.User;
import com.gokhanaliccii.rxjavawithdagger.rest.RestAdapter;
import com.google.gson.Gson;

import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Created by gokhan on 12/11/17.
 */

public class GetUserTest {

    public static String TEST_SERVER = "/";

    private MockWebServer mockWebServer = new MockWebServer();

    @Before
    public void setUp() throws Exception {
        TEST_SERVER = mockWebServer.url(TEST_SERVER).toString();
//        mockWebServer.start();
    }

    @After
    public void close() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void should_CallServiceCorrectly() throws Exception {
        int single = 1;

        Gson gson = new Gson();
        String response = gson.toJson(new User("gokhan"));

        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody(response);

        mockWebServer.enqueue(mockResponse);

        TestObserver<User> testSubscriber = new TestObserver<>();

        RestClient restClient = RestAdapter.getInstance(TEST_SERVER).restClient();
        restClient.getUser().subscribe(testSubscriber);

        Assert.assertThat(mockWebServer.getRequestCount(), IsEqual.equalTo(single));
    }

    @Test
    public void should_CallServiceTwice() throws Exception {
        int single = 1;

        Gson gson = new Gson();
        String response = gson.toJson(new User("gokhan"));

        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(500);
        mockResponse.setBody(response);

        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));
        mockWebServer.enqueue(mockResponse.setResponseCode(501));


        TestObserver<User> testSubscriber = new TestObserver<>();

        RestClient restClient = RestAdapter.getInstance(TEST_SERVER).restClient();
        restClient.getUser().retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.zipWith(Observable.range(1, 5), new BiFunction<Throwable, Integer, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable, Integer integer) throws Exception {
                        return integer * 3;
                    }
                }).flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer integer) throws Exception {
                        System.out.println(integer);
                        return Observable.timer(integer, TimeUnit.SECONDS);
                    }
                });
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("error** " + throwable.getMessage());
            }
        }).subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribed");
            }

            @Override
            public void onNext(User user) {
                System.out.println("onNext");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("error");
            }

            @Override
            public void onComplete() {
                System.out.println("completed");

            }
        });


        Thread.sleep(30000);
    }

    @Test
    public void zipOperator() throws Exception {

        Observable<Integer> even = Observable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 2 == 0;
            }
        });

        Observable<Integer> odd = Observable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 2 != 0;
            }
        });

        Observable.zip(even, odd, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                return String.valueOf(integer + "-" + integer2);
            }
        }).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return Observable.range(1,2).flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer integer) throws Exception {
                        return Observable.timer(integer*4,TimeUnit.SECONDS);
                    }
                }).take(1);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
                System.out.println(System.currentTimeMillis());
            }
        });

        Thread.sleep(40000);
    }
}

package com.gokhanaliccii.rxjavawithdagger;

import com.gokhanaliccii.rxjavawithdagger.di.RestClient;
import com.gokhanaliccii.rxjavawithdagger.model.User;
import com.gokhanaliccii.rxjavawithdagger.rest.RestAdapter;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Created by gokhan on 25/12/17.
 */

public class SearchDebounceTest {

    public static final int SINGLE = 1;
    public static final int NONE = 0;

    public String serviceUrl;
    public RestClient restClient;

    public MockWebServer mockWebServer;

    @Before
    public void setUp() {
        mockWebServer = new MockWebServer();
        serviceUrl = mockWebServer.url("/").toString();
        restClient = RestAdapter.getInstance(serviceUrl).restClient();
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    public void should_CallServiceOnetimeCorrectly() {
        putMockResponses(1);

        restClient.searchUser("gokhan").subscribe();

        int requestCount = mockWebServer.getRequestCount();

        Assert.assertTrue(requestCount == SINGLE);
    }

    @Test
    public void should_WontCallServiceUntilSearchTextCount3() throws InterruptedException {
        putMockResponses(1);
        String searchTerm = "ab";

        io.reactivex.Observable.just(searchTerm).debounce(100, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.length() > 2;
                    }
                }).switchMap(new Function<String, io.reactivex.Observable<User>>() {
            @Override
            public Observable<User> apply(String s) throws Exception {
                return restClient.searchUser(s);
            }
        }).subscribeOn(Schedulers.io()).subscribe();

        waitInMilisecond(200);

        int requestCount = mockWebServer.getRequestCount();
        Assert.assertTrue(requestCount == NONE);
    }

    @Test
    public void should_CallServiceWhenSearchCharacterCount3() {
        putMockResponses(1);
        String searchTerm = "abc";

        io.reactivex.Observable.just(searchTerm).debounce(100, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.length() > 2;
                    }
                }).switchMap(new Function<String, io.reactivex.Observable<User>>() {
            @Override
            public Observable<User> apply(String s) throws Exception {
                return restClient.searchUser(s);
            }
        }).subscribeOn(Schedulers.io()).subscribe();

        waitInMilisecond(200);

        int requestCount = mockWebServer.getRequestCount();
        Assert.assertTrue(requestCount == SINGLE);
    }


    @Test
    public void should_CallServiceOneTimeWhenSequentiallyTextChanged() {
        putMockResponses(1);
        BehaviorSubject<String> searchItemTextListener = BehaviorSubject.create();

        searchItemTextListener.debounce(100, TimeUnit.MICROSECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.length() > 2;
                    }
                }).switchMap(new Function<String, Observable<User>>() {

            @Override
            public Observable<User> apply(String s) throws Exception {
                return restClient.searchUser(s);
            }
        }).subscribeOn(Schedulers.io()).subscribe();

        searchItemTextListener.onNext("g");
        searchItemTextListener.onNext("go");
        searchItemTextListener.onNext("gok");

        waitInMilisecond(200);

        int requestCount = mockWebServer.getRequestCount();
        Assert.assertTrue(requestCount == SINGLE);

    }

    @Test
    public void should_CallServiceTwoTimeWhenTextChangeOccurAfterDelayTimeout() {
        putMockResponses(2);

        BehaviorSubject<String> searchItemTextListener = BehaviorSubject.create();

        searchItemTextListener.debounce(100, TimeUnit.MICROSECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.length() > 2;
                    }
                }).switchMap(new Function<String, Observable<User>>() {

            @Override
            public Observable<User> apply(String s) throws Exception {
                return restClient.searchUser(s);
            }
        }).subscribeOn(Schedulers.io()).subscribe();

        searchItemTextListener.onNext("g");
        searchItemTextListener.onNext("go");
        searchItemTextListener.onNext("gok");

        waitInMilisecond(101);
        searchItemTextListener.onNext("gokh");
        waitInMilisecond(110);

        int requestCount = mockWebServer.getRequestCount();
        Assert.assertTrue(requestCount == 2);
    }

    @Test
    public void should_WaitTwoSecondBeforeApiCall() {

        waitInMilisecond(5000);

    }


    private void putMockResponses(int count) {
        while (count-- > 0) {
            mockWebServer.enqueue(dummyMockResponse());
        }
    }

    private MockResponse dummyMockResponse() {
        Gson gson = new Gson();
        String userJson = gson.toJson(new User("gokhan"));

        return new MockResponse().setBody(userJson);
    }

    private void waitInMilisecond(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

package com.gokhanaliccii.rxjavawithdagger;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by gokhan on 12/11/17.
 */


public class RxOperatorTest {

    private TestObserver<Integer> testObserver = new TestObserver();

    @Test
    public void should_SumValuesInRangeCorrectly() {
        int expected = 15;

        Observable.range(1, 5).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }

    @Test
    public void should_SumEvenNumbers1to5Correctly() {
        int expected = 6;

        Observable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 2 == 0;
            }
        }).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }

    @Test
    public void should_MapStringToIntegerCorrectly() {
        final Integer expected = 12;

        Observable.just(String.valueOf(12)).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return Integer.parseInt(s);
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }

    @Test
    public void should_SumFirstTwoNumbersCorrectly() {
        int expected = 3;

        Observable.range(1, 50).take(2).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }

    @Test
    public void should_SumLastNumberOnly() {
        int expected = 5;

        Observable.range(1, 5).skip(4).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }

    @Test
    public void should_ZipTwoObservablesCorrectly() throws Exception {
        TestObserver<String> stringTestSubscriber = new TestObserver<>();

        List<Observable<Integer>> obs = new LinkedList<>();

        Observable<Integer> oddNumberObservable =
                Observable.range(1, 30).subscribeOn(Schedulers.io()).filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {

                        String threadName = Thread.currentThread().getName();
                        if (integer == 4)
                            throw new NullPointerException();

                        System.out.println("called1(" + threadName + ") 1 *" + integer);
                        return integer % 2 != 0;
                    }
                }).doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                }).subscribeOn(Schedulers.io());

        Observable<Integer> evenNumberObservable = Observable.range(1, 10)
                //.delay(2, TimeUnit.SECONDS)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        String threadName = Thread.currentThread().getName();
                        System.out.println("called2(" + threadName + ") 2 *" + integer);

                        if (integer == 5)
                            throw new NullPointerException();

                        return integer % 2 == 0;
                    }
                }).onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable throwable) throws Exception {
                        System.out.println("hata 2");

                        return -1;
                    }
                }).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {

                        String threadName = Thread.currentThread().getName();


                        System.out.println("called2(" + threadName + ") 2 map *" + integer);
                        return integer;
                    }
                });

        Observable.mergeDelayError(oddNumberObservable,evenNumberObservable)
                .doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("fddfdf");
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                System.out.println("\t r:" + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("ugurcan cıkolatasız olmaz ");
            }
        });


     /*   oddNumberObservable.zipWith(evenNumberObservable,new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {

                String s = "?    l" + integer + "   r:" + integer2;
                System.out.println(s);
                return s;
            }
        }).subscribeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("birleşmemişs:"+s);
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }).subscribe();*/
/*
        Observable.zip(oddNumberObservable, evenNumberObservable, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {

                String s = "?    l" + integer + "   r:" + integer2;
                System.out.println(s);
                return s;
            }
        }).subscribeOn(Schedulers.io()).subscribe(stringTestSubscriber);*/

        Thread.sleep(300000);
    }

    @Test
    public void should_PrintDifferentTimeStampEveryTime() throws Exception {

        Observable<Long> just = Observable.rangeLong(1, 20).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) throws Exception {
                Thread.sleep(300);
                return aLong;
            }
        }).subscribeOn(Schedulers.computation());

        Observable<String> just2 =
                Observable.rangeLong(1, 20).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Exception {
                        Thread.sleep(300);

                        return "hey*" + aLong;
                    }
                }).subscribeOn(Schedulers.computation());

        Observable<Long> longSecond = Observable.interval(2, TimeUnit.SECONDS).doOnNext(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

                System.out.println("ii" + aLong);
            }
        });


        Thread.sleep(4000);
    }

    @Test
    public void publishSubject() throws Exception {
        PublishSubject<String> stringPublishSubject = PublishSubject.create();
        stringPublishSubject.subscribe(getObserver("first"));

        stringPublishSubject.onNext("1");
        stringPublishSubject.onNext("2");
        stringPublishSubject.onNext("3");

        stringPublishSubject.subscribe(getObserver("second"));
    }

    @Test
    public void publishSubject2() throws Exception {
        PublishSubject<String> stringPublishSubject = PublishSubject.create();
        stringPublishSubject.onNext("1");
        stringPublishSubject.subscribe(getObserver("first"));

        stringPublishSubject.onNext("2");
        stringPublishSubject.onNext("3");

        stringPublishSubject.subscribe(getObserver("second"));
    }

    @Test
    public void replaySubject() throws Exception {
        ReplaySubject replaySubject = ReplaySubject.create();
        replaySubject.onNext("1");
        replaySubject.subscribe(getObserver("first"));

        replaySubject.onNext("2");
        replaySubject.onComplete();
        replaySubject.onNext("3");

        replaySubject.subscribe(getObserver("second"));
    }

    @Test
    public void behaviourSubject() throws Exception {
        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();
        behaviorSubject.onNext("1");
        behaviorSubject.onNext("2");
        behaviorSubject.onComplete();
        behaviorSubject.subscribe(getObserver("f"));
    }

    @NonNull
    private Observer<String> getObserver(final String prefix) {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println(prefix + " onSubscribe");
            }

            @Override
            public void onNext(String s) {
                System.out.println(prefix + " onNext:" + s);

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(prefix + " onError:" + e.getMessage());

            }

            @Override
            public void onComplete() {
                System.out.println(prefix + " onComplete:");
            }
        };
    }
}

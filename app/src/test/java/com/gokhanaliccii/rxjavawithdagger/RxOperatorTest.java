package com.gokhanaliccii.rxjavawithdagger;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;

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
    public void should_SumFirstTwoNumbersCorrectly(){
        int expected = 3;

        Observable.range(1,50).take(2).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer+integer2;
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }

    @Test
    public void should_SumLastNumberOnly() {
        int expected = 5;

        Observable.range(1,5).skip(4).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }


}

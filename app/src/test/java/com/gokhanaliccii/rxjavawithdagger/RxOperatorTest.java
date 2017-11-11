package com.gokhanaliccii.rxjavawithdagger;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.TestObserver;

/**
 * Created by gokhan on 12/11/17.
 */


public class RxOperatorTest {

    @Test
    public void should_SumValuesInRangeCorrectly() {
        int expected = 15;

        TestObserver<Integer> testObserver = new TestObserver();

        Observable.range(1, 5).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(testObserver);

        testObserver.assertResult(expected);
    }

}

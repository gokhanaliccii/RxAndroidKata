package com.gokhanaliccii.rxjavawithdagger.di;


import com.gokhanaliccii.rxjavawithdagger.model.User;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by gokhan on 12/11/17.
 */

public interface RestClient {

    @GET("getUser")
    Observable<User> getUser();
}

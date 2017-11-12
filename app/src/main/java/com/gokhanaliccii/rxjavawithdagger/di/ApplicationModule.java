package com.gokhanaliccii.rxjavawithdagger.di;

import com.gokhanaliccii.rxjavawithdagger.rest.RestAdapter;

import javax.inject.Singleton;

import dagger.Module;

/**
 * Created by gokhan on 12/11/17.
 */

@Module
public interface ApplicationModule {


    @Singleton
    RestAdapter restAdapter();

}

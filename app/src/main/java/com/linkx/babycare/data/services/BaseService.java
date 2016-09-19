package com.linkx.babycare.data.services;

import android.util.Log;
import com.linkx.babycare.di.components.ApplicationComponent;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.Callable;

/**
 * Created by ulyx.yang on 2016/9/4.
 */
public class BaseService {
    ApplicationComponent applicationComponent;

    public BaseService(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;

    }

    protected <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                        } catch (Exception ex) {
                            Log.e("POKE", "Error loading from the database", ex);
                        }
                    }
                });
    }

}

package com.linkx.babycare.data.services;

import android.os.Looper;
import android.util.Log;
import com.linkx.babycare.data.models.Model;
import com.linkx.babycare.utils.IOUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;

import java.io.IOException;

public class BuruDataUpdateService {

    Observable<Boolean> asFileSinkerObservable(String fileName, Model model) {
        return Observable.defer(() -> {
            try {
                IOUtil.appendLine(fileName, model.toJson());
            } catch (IOException e) {
                throw OnErrorThrowable.from(e);
            }
            return Observable.just(true);
        });
    }

    public void updateFromUI(String fileName, Model model, Looper looper, Subscriber<Boolean> subscriber) {
        updateFromUI(fileName, model, looper).subscribe(subscriber);
    }

    public void updateFromUISliently(String fileName, Model model, Looper looper) {
        Subscriber<Boolean> subscriber = new Subscriber<Boolean>() {
                    String tag = Model.class.getSimpleName();
                    @Override public void onCompleted() {
                        Log.d(tag, "onCompleted():data=" + model.toJson());
                    }

                    @Override public void onError(Throwable e) {
                        Log.e(tag, "onError():data=" + model.toJson(), e);
                    }

                    @Override public void onNext(Boolean success) {
                        Log.d(tag, "onNext():data=" + model.toJson() + ",result=" + success);
                    }
                };
        updateFromUI(fileName, model, looper, subscriber);
    }

    public Observable<Boolean> updateFromUI(String fileName, Model model, Looper looper) {
        return asFileSinkerObservable(fileName, model)
                .subscribeOn(AndroidSchedulers.from(looper))
                .observeOn(AndroidSchedulers.mainThread());
    }

}

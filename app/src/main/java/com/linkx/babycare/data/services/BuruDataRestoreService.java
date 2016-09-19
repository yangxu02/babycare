package com.linkx.babycare.data.services;

import android.os.Looper;
import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.google.common.base.Strings;
import com.linkx.babycare.data.SerializerProvider;
import com.linkx.babycare.data.models.Model;
import com.linkx.babycare.utils.IOUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BuruDataRestoreService {

    Observable<Boolean> asFileBackupWriterObservable(String fileName, Map<String, Object> states) {
        return Observable.defer(() -> {
            try {
                String strStates = SerializerProvider.getInstance().writeValueAsString(states);
                IOUtil.writeLine(fileName, strStates);
            } catch (IOException e) {
                throw OnErrorThrowable.from(e);
            }
            return Observable.just(true);
        });
    }

    Observable<Map> asFileBackupReaderObservable(String fileName) {
        return Observable.defer(() -> {
            try {
                String content = IOUtil.readFirstLine(fileName);
                if (Strings.isNullOrEmpty(content)) {
                    return Observable.just(Collections.EMPTY_MAP);
                }
                JavaType type = SerializerProvider.getInstance().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
                return Observable.just(SerializerProvider.getInstance().readValue(content, type));
            } catch (IOException e) {
                throw OnErrorThrowable.from(e);
            }
        });
    }

    Observable<Boolean> asFileBackupCleanObservable(String fileName) {
        return Observable.defer(() -> {
            try {
                IOUtil.truncate(fileName);
                return Observable.just(true);
            } catch (IOException e) {
                throw OnErrorThrowable.from(e);
            }
        });
    }

    public void backup(String fileName, Map<String, Object> states, Looper looper) {
        Subscriber<Boolean> subscriber = new Subscriber<Boolean>() {
            String tag = "Backup";
            @Override public void onCompleted() {
                Log.d(tag, "onCompleted():data=" + states);
            }

            @Override public void onError(Throwable e) {
                Log.e(tag, "onError():data=" + states, e);
            }

            @Override public void onNext(Boolean success) {
                Log.d(tag, "onNext():data=" + states + ",result=" + success);
            }
        };
        asFileBackupWriterObservable(fileName, states)
                .subscribeOn(AndroidSchedulers.from(looper))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public Observable<Map> restore(String fileName, Looper looper) {
        return asFileBackupReaderObservable(fileName)
                .subscribeOn(AndroidSchedulers.from(looper))
                .observeOn(AndroidSchedulers.mainThread());
    }

     public void cleanup(String fileName, Looper looper) {
          Subscriber<Boolean> subscriber = new Subscriber<Boolean>() {
            String tag = "Cleanup";
            @Override public void onCompleted() {
                Log.d(tag, "onCompleted():file=" + fileName);
            }

            @Override public void onError(Throwable e) {
                Log.e(tag, "onError():file=" + fileName, e);
            }

            @Override public void onNext(Boolean success) {
                Log.d(tag, "onNext():file=" + fileName + ",result=" + success);
            }
        };
        asFileBackupCleanObservable(fileName)
                .subscribeOn(AndroidSchedulers.from(looper))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}

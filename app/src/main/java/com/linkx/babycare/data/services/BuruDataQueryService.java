package com.linkx.babycare.data.services;

import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.google.common.base.Charsets;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;
import com.linkx.babycare.data.models.*;
import com.linkx.babycare.utils.IOUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BuruDataQueryService {

    private  <T extends Model> List<T> asFileSource(String tag, String dayStr, Class<T> clazz) throws IOException, Model.MethodNotOverrideException {
        List<T> totalList = new ArrayList<T>();
        String fileName = IOUtil.dataFileName(tag, dayStr);
        File file = new File(fileName);
        if (file.exists()) {
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            for (String line : lines) {
                totalList.add(Model.fromJson(line, clazz));
            }
        }
        if (totalList.isEmpty()) {
            return totalList;
        }

        List<T> deletedList = new ArrayList<T>();
        String deletedFileName = fileName + ".deleted";
        File deletedFile = new File(deletedFileName);
        if (deletedFile.exists()) {
            List<String> lines = Files.readLines(deletedFile, Charsets.UTF_8);
            for (String line : lines) {
                deletedList.add(Model.fromJson(line, clazz));
            }
        }

        if (totalList.isEmpty()) {
            return totalList;
        }

        List<T> dataList = new ArrayList<>();
        for (T item : totalList) {
            String id = item.identity();
            boolean deleted = false;
            for (T itemDeleted: deletedList) {
                if (itemDeleted.identity().equals(id)) {
                    deleted = true;
                    break;
                }
            }
            if (deleted) {
                continue;
            }
            dataList.add(item);
        }

        return dataList;
    }

    public <T extends Model> Observable<List<T>> baseDetailObservable(String tag, Class<T> clazz) {
        return Observable.defer(() -> {
            try
            {
                List<T> dataList = asFileSource(tag, IOUtil.dayStr(System.currentTimeMillis()), clazz);
                return Observable.just(dataList);
            } catch (IOException | Model.MethodNotOverrideException e) {
                throw OnErrorThrowable.from(e);
            }
        });
    }

    public <T extends Model> void queryFromUISliently(String tag, Class<T> clazz, Looper looper) {
        Subscriber<List<T>> subscriber = new Subscriber<List<T>>() {
            String tag = clazz.getSimpleName();
            @Override
            public void onCompleted() {
                Log.d(tag, "onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(tag, "onError()", e);
            }

            @Override
            public void onNext(List<T> ts) {
                Log.d(tag, "onNext():data=" + ((Model)ts).toJson());
            }
        };

        queryFromUI(tag, clazz, looper, subscriber);
    }

    public <T extends Model> void queryFromUI(String tag, Class<T> clazz, Looper looper, Subscriber<List<T>> subscriber) {
        queryFromUI(tag, clazz, looper).subscribe(subscriber);
    }

    private <T extends Model> Observable<List<T>> queryFromUI(String tag, Class<T> clazz, Looper looper) {
        return baseDetailObservable(tag, clazz)
                .subscribeOn(AndroidSchedulers.from(looper))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T extends Model> void queryAndDisplay(String tag, Class<T> clazz, Looper looper,
                                                  ViewGroup container, Func1<T, View> func) {
        queryFromUI(tag, clazz, looper,
                new Subscriber<List<T>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("Query", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Query", "onError", e);
                    }

                    @Override
                    public void onNext(List<T> items) {
                        for (T item : items) {
                            container.addView(func.call(item));
                            Log.d("Query", "onNext:data=" + item.toJson());
                        }
                    }
                });
    }

    public Observable<List<AllDetail>> allDetailObservable() {
        return Observable.defer(() -> {
            List<AllDetail> dataList = new ArrayList<>();

            try {
                File dataDir = new File(IOUtil.dataFileDir());
                String curDay = IOUtil.dayStr(System.currentTimeMillis());
                Iterable<File> dayDirs = Files.fileTreeTraverser().children(dataDir);
                for (File dayDir : dayDirs) {
                    String day = dayDir.getName();
                    if (".".equals(day) || "..".equals(day) || curDay.equals(day)) {
                        continue;
                    }
                    String allDetailName = dayDir.getAbsolutePath() + '/' + "all";
                    File allDetailFile = new File(allDetailName);
                    if (!allDetailFile.exists()) {
                        List<BuruDetail> buruDetails = asFileSource("buru", day, BuruDetail.class);
                        List<DabianDetail> dabianDetails = asFileSource("dabian", day, DabianDetail.class);
                        List<SimpleDetail> xiaobianDetails = asFileSource("xiaobian", day, SimpleDetail.class);
                        List<SimpleDetail> huangdanDetails = asFileSource("huangdan", day, SimpleDetail.class);
                        List<SimpleDetail> tizhongDetails = asFileSource("tizhong", day, SimpleDetail.class);
                        List<SimpleDetail> tiwenDetails = asFileSource("tiwen", day, SimpleDetail.class);
                        AllDetail allDetail = AllDetail.create(UUID.randomUUID().toString(), day,
                                buruDetails, dabianDetails, xiaobianDetails, huangdanDetails, tizhongDetails, tiwenDetails);
                        dataList.add(allDetail);
                        IOUtil.appendLine(allDetailName, allDetail.toJson());
                        continue;
                    }
                    File allDetailDeletedFile = new File(allDetailName + ".deleted");
                    if (allDetailDeletedFile.exists()) {
                        continue;
                    }

                    dataList.add(Model.fromJson(Files.toString(allDetailFile, Charsets.UTF_8), AllDetail.class));
                }

                Collections.sort(dataList, (lhs, rhs) -> -1 * (Integer.parseInt(lhs.day()) - Integer.parseInt(rhs.day())));

            } catch (IOException | Model.MethodNotOverrideException e) {
                throw OnErrorThrowable.from(e);
            }
            return Observable.just(dataList);
        });
    }


    public Observable<List<AllDetail>> queryHistory(Looper looper) {

        return allDetailObservable()
                .subscribeOn(AndroidSchedulers.from(looper))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void queryHistoryAndDisplay(Looper looper, ViewGroup container, Func1<AllDetail, View> func) {
        allDetailObservable()
                .subscribeOn(AndroidSchedulers.from(looper))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AllDetail>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("Query", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Query", "onError", e);
                    }

                    @Override
                    public void onNext(List<AllDetail> items) {
                        for (AllDetail item : items) {
                            container.addView(func.call(item));
                            Log.d("Query", "onNext:data=" + item.toJson());
                        }
                    }
                });
    }

}

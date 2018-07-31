package com.harsom.baselib.activity.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


public class OnResultSupportFragment extends Fragment {
    private SparseArray<PublishSubject<ActivityResultInfo>> mSubjects;

    public OnResultSupportFragment() {
        mSubjects = new SparseArray<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Observable<ActivityResultInfo> startForResult(final Intent intent, final int requestCode) {
        PublishSubject<ActivityResultInfo> subject = PublishSubject.create();
        mSubjects.put(requestCode, subject);

        return subject.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {
                startActivityForResult(intent, requestCode);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //rxjava方式的处理
        PublishSubject<ActivityResultInfo> subject = mSubjects.get(requestCode);
        if (subject != null) {
            subject.onNext(new ActivityResultInfo(requestCode, resultCode, data));
            subject.onComplete();
        }
        mSubjects.remove(requestCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

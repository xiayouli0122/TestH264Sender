package com.harsom.baselib.activity.result;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Observable;

/**
 * 一种用于替换onActivityResult的方法，避免onActivityResult和startActivity分离不利于阅读
 */
public class ActivityOnResult {

    private static final String TAG = "ActivityOnResult";

    private OnResultFragment mResultFragment;
    private OnResultSupportFragment mResultSupportFragment;

    public static ActivityOnResult with(Activity activity) {
        return new ActivityOnResult(activity);
    }

    public static ActivityOnResult with(AppCompatActivity activity) {
        return new ActivityOnResult(activity);
    }

    public static ActivityOnResult with(Fragment fragment) {
        return new ActivityOnResult(fragment);
    }

    public static ActivityOnResult with(android.app.Fragment fragment) {
        return new ActivityOnResult(fragment);
    }

    private ActivityOnResult(AppCompatActivity activity) {
        mResultSupportFragment = getOnResultFragment(activity);
    }

    private ActivityOnResult(Fragment fragment) {
        mResultSupportFragment = getOnResultFragment(fragment);
    }

    private ActivityOnResult(Activity activity) {
        mResultFragment = getOnResultFragment(activity);
    }

    private ActivityOnResult(android.app.Fragment fragment) {
        mResultFragment = getOnResultFragment(fragment);
    }

    private OnResultSupportFragment getOnResultFragment(AppCompatActivity activity) {
        OnResultSupportFragment onResultFragment = findOnResultFragment(activity);
        if (onResultFragment == null) {
            onResultFragment = new OnResultSupportFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(onResultFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return onResultFragment;
    }

    private OnResultFragment getOnResultFragment(Activity activity) {
        OnResultFragment onResultFragment = findOnResultFragment(activity);
        if (onResultFragment == null) {
            onResultFragment = new OnResultFragment();
            android.app.FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(onResultFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return onResultFragment;
    }

    private OnResultSupportFragment getOnResultFragment(Fragment fragment) {
        OnResultSupportFragment onResultFragment = findOnResultFragment(fragment);
        if (onResultFragment == null) {
            onResultFragment = new OnResultSupportFragment();
            FragmentManager fragmentManager = fragment.getChildFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(onResultFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return onResultFragment;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private OnResultFragment getOnResultFragment(android.app.Fragment fragment) {
        OnResultFragment onResultFragment = findOnResultFragment(fragment);
        if (onResultFragment == null) {
            onResultFragment = new OnResultFragment();
            android.app.FragmentManager fragmentManager = fragment.getChildFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(onResultFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return onResultFragment;
    }

    private OnResultSupportFragment findOnResultFragment(AppCompatActivity activity) {
        return (OnResultSupportFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
    }

    private OnResultFragment findOnResultFragment(Activity activity) {
        return (OnResultFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    private OnResultSupportFragment findOnResultFragment(Fragment fragment) {
        return (OnResultSupportFragment) fragment.getChildFragmentManager().findFragmentByTag(TAG);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private OnResultFragment findOnResultFragment(android.app.Fragment fragment) {
        return (OnResultFragment) fragment.getChildFragmentManager().findFragmentByTag(TAG);
    }

    public Observable<ActivityResultInfo> startForResult(Class<?> clazz, int requestCode) {
        return startForResult(clazz, null, requestCode);
    }

    public Observable<ActivityResultInfo> startForResult(Class<?> clazz, Bundle bundle,  int requestCode) {
        Context context;
        if (mResultFragment != null) {
            context = mResultFragment.getActivity();
        } else if (mResultSupportFragment != null) {
            context = mResultSupportFragment.getContext();
        } else {
            context = null;
        }

        if (context == null) {
            return null;
        }

        Intent intent = new Intent(context, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return startForResult(intent, requestCode);
    }

    public Observable<ActivityResultInfo> startForResult(Intent intent, int requestCode) {
        if (mResultFragment != null) {
            return mResultFragment.startForResult(intent, requestCode);
        }

        if (mResultSupportFragment != null) {
            return mResultSupportFragment.startForResult(intent, requestCode);
        }
        return null;
    }
}

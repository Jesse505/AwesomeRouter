package com.android.jesse.router_api.core;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * 路由跳转管理类
 */
public final class PostCard {

    private Bundle mBundle;

    private int enterAnim = -1;
    private int exitAnim = -1;

    public PostCard() {
        this(null);
    }

    public PostCard(Bundle bundle) {
        this.mBundle = (null == bundle ? new Bundle() : bundle);
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public PostCard withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }

    public PostCard withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }

    public PostCard withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }

    public PostCard withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public PostCard withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    public PostCard withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }

    public PostCard withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }

    public PostCard withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }

    public PostCard withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }

    public PostCard withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
        return this;
    }

    public PostCard withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }

    public PostCard withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public PostCard withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public PostCard withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        mBundle.putSparseParcelableArray(key, value);
        return this;
    }

    public PostCard withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public PostCard withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    public PostCard withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mBundle.putCharSequenceArrayList(key, value);
        return this;
    }

    public PostCard withSerializable(@Nullable String key, @Nullable Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public PostCard withByteArray(@Nullable String key, @Nullable byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }

    public PostCard withShortArray(@Nullable String key, @Nullable short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }

    public PostCard withCharArray(@Nullable String key, @Nullable char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }

    public PostCard withFloatArray(@Nullable String key, @Nullable float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }

    public PostCard withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mBundle.putCharSequenceArray(key, value);
        return this;
    }

    public PostCard withBundle(@Nullable String key, @Nullable Bundle value) {
        mBundle.putBundle(key, value);
        return this;
    }

    /**
     * Set normal transition anim
     *
     * @param enterAnim enter
     * @param exitAnim  exit
     * @return current
     */
    public PostCard withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    public Object navigation(Context context) {
        return AwesomeRouter.getInstance().navigation(context, this);
    }
}


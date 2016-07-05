package com.yydcdut.rxmarkdown;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yydcdut.rxmarkdown.factory.AbsGrammarFactory;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yuyidong on 16/5/3.
 */
public class RxMarkdown {
    private static final String TAG = RxMarkdown.class.getName();
    private String mContent;
    private RxMDEditText mRxMDEditText;
    private Context mContext;
    private AbsGrammarFactory mAbsGrammarFactory;
    private RxMDConfiguration mRxMDConfiguration;

    private RxMarkdown(String content, Context context) {
        mContent = content;
        mContext = context;
    }

    private RxMarkdown(RxMDEditText rxMDEditText) {
        mRxMDEditText = rxMDEditText;
        mContext = mRxMDEditText.getContext();
    }

    public static RxMarkdown with(String content, Context context) {
        return new RxMarkdown(content, context);
    }

    public static RxMarkdown live(RxMDEditText rxMDEditText) {
        return new RxMarkdown(rxMDEditText);
    }

    public RxMarkdown config(RxMDConfiguration rxMDConfiguration) {
        mRxMDConfiguration = rxMDConfiguration;
        return this;
    }

    public RxMarkdown factory(AbsGrammarFactory absGrammarFactory) {
        mAbsGrammarFactory = absGrammarFactory;
        return this;
    }

    public Observable<CharSequence> intoObservable() {
        if (mContent != null) {
            return Observable.just(mContent)
                    .map(new Func1<String, CharSequence>() {
                        @Override
                        public CharSequence call(String s) {
                            if (mAbsGrammarFactory != null) {
                                RxMDConfiguration config = getRxMDConfiguration();
                                long time = System.currentTimeMillis();
                                CharSequence charSequence = mAbsGrammarFactory.parse(s, config);
                                if (config.isDebug()) {
                                    Log.i(TAG, "spend time --->" + (System.currentTimeMillis() - time));
                                }
                                return charSequence;
                            }
                            return s;
                        }
                    });
        } else {
            return Observable.just(mRxMDEditText)
                    .map(new Func1<RxMDEditText, CharSequence>() {
                        @Override
                        public CharSequence call(RxMDEditText rxMDEditText) {
                            if (mAbsGrammarFactory == null) {
                                return rxMDEditText.getText();
                            }
                            rxMDEditText.setFactoryAndConfig(mAbsGrammarFactory, getRxMDConfiguration());
                            return rxMDEditText.getText();
                        }
                    });
        }
    }

    @NonNull
    private RxMDConfiguration getRxMDConfiguration() {
        if (mRxMDConfiguration == null) {
            mRxMDConfiguration = new RxMDConfiguration.Builder(mContext).build();
        }
        return mRxMDConfiguration;
    }

}

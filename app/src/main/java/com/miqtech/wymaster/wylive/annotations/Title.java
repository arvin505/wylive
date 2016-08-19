package com.miqtech.wymaster.wylive.annotations;

import android.renderscript.Sampler;
import android.support.annotation.StringRes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiaoyi on 2016/8/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Title {
    /**
     * 标题id
     */
    @StringRes int titleId() default -1;

    String title() default "";

}

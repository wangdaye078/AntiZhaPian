package com.demo.antizha.util;

import android.app.Activity;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import com.demo.antizha.ui.HiCore;

import java.lang.reflect.Method;


public class NotchUtils {

    public static int liuhaiHeight(Activity activity) {
        if (!haveLiuhai(activity)) {
            return 0;
        }
        calcLiuhaiHeight();
        return 0;
    }

    public static boolean haveLiuhai(Activity activity) {
        return "1".equals(getPropertie("ro.miui.notch")) || isHuaWeiNotch() || isOppoNotch() || isVivoNotch() || getDisplayCutout(activity) != null;
    }

    public static int xiaomiliuhaiHeight() {
        int identifier = HiCore.app.getResources().getIdentifier("notch_height", "dimen", "android");
        if (identifier > 0) {
            return HiCore.app.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static boolean isHuaWeiNotch() {
        try {
            ClassLoader classLoader = HiCore.app.getClassLoader();
            Class class_HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method method_hasNotch = class_HwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) method_hasNotch.invoke(class_HwNotchSizeUtil);
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isOppoNotch() {
        return HiCore.app.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    public static boolean isVivoNotch() {
        try {
            ClassLoader classLoader = HiCore.app.getClassLoader();
            Class class_FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method_Feature = class_FtFeature.getMethod("isFeatureSupport", Integer.TYPE);
            return ((Boolean) method_Feature.invoke(class_FtFeature, new Object[]{32}));
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isHuawei() {
        return "huawei".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isOppo() {
        return "oppo".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isVivo() {
        return "vivo".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isXiaomi() {
        return "xiaomi".equalsIgnoreCase(Build.MANUFACTURER);
    }

    private static int calcLiuhaiHeight() {
        if (isXiaomi()) {
            return xiaomiliuhaiHeight();
        }
        if (isHuawei()) {
            return getHuaweiNotchSize()[0];
        }
        if (isOppo()) {
            return 80;
        }
        if (isVivo()) {
            return dp2px(27.0f);
        }
        return 0;
    }

    public static int[] getHuaweiNotchSize() {
        try {
            ClassLoader classLoader = HiCore.app.getClassLoader();
            Class class_HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method method_getNotchSize = class_HwNotchSizeUtil.getMethod("getNotchSize");
            return (int[]) method_getNotchSize.invoke(class_HwNotchSizeUtil);
        } catch (Exception unused) {
            return new int[]{0, 0};
        }
    }

    public static DisplayCutout getDisplayCutout(Activity activity) {
        WindowInsets rootWindowInsets;
        View decorView = activity.getWindow().getDecorView();
        if (decorView == null || Build.VERSION.SDK_INT < 28 || (rootWindowInsets = decorView.getRootWindowInsets()) == null) {
            return null;
        }
        return rootWindowInsets.getDisplayCutout();
    }

    public static int dp2px(float f) {
        return (int) ((f * HiCore.app.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static String getPropertie(String key) {
        if (!isXiaomi()) {
            return "0";
        }
        try {
            ClassLoader classLoader = HiCore.app.getClassLoader();
            Class class_SysProp = classLoader.loadClass("android.os.SystemProperties");
            Method method_SysProp = class_SysProp.getMethod("get", String.class, String.class);
            return (String) (method_SysProp.invoke(class_SysProp, key, "0"));
        } catch (Exception unused) {
            return "0";
        }
    }
}
package jp.eq_inc.testuiautomator.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SizeUnitUtil {
    private enum SizeUnitType {
        Dp("dp", "getPxFromDp"),
        Dip("dip", "getPxFromDp"),
        Percent("%", "getPxFromPercentForX", "getPxFromPercentForY"),
        Px("px", "getPxFromPx"),
        Sp("sp", "getPxFromSp"),;

        private String mUnitText;
        private String mMethodNameForX;
        private String mMethodNameForY;
        private String mSizeUnitFormat;

        SizeUnitType(String unitText, String... methodNameForXY) {
            mUnitText = unitText;
            mMethodNameForX = methodNameForXY[0];
            mMethodNameForY = methodNameForXY.length > 1 ? methodNameForXY[1] : methodNameForXY[0];
            mSizeUnitFormat = "(-?\\s*[1-9]+[0-9])*\\s*" + mUnitText;
        }

        public boolean matchUnit(String sizeValue) {
            return sizeValue.trim().matches(mSizeUnitFormat);
        }

        public Float getSize(String sizeValue) {
            Float ret = null;
            Pattern pattern = Pattern.compile(mSizeUnitFormat);
            Matcher matcher = pattern.matcher(sizeValue);

            if (matcher.matches()) {
                String valueText = matcher.group(1);
                try {
                    ret = Float.valueOf(valueText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            return ret;
        }

        static SizeUnitType getType(String sizeText) {
            SizeUnitType ret = null;

            for (SizeUnitType sizeUnitType : values()) {
                if (sizeUnitType.matchUnit(sizeText)) {
                    ret = sizeUnitType;
                    break;
                }
            }

            return ret;
        }
    }

    public static float getPositionX(Context context, String positionXText) {
        return getPosition(context, positionXText, "mMethodNameForX");
    }

    public static float getPositionY(Context context, String positionYText) {
        return getPosition(context, positionYText, "mMethodNameForY");
    }

    private static float getPosition(Context context, String positionText, String methodFieldName) {
        float ret = 0f;
        SizeUnitType sizeUnitType = SizeUnitType.getType(positionText);

        if (sizeUnitType != null) {
            Float valueFloat = sizeUnitType.getSize(positionText);

            if (valueFloat != null) {
                try {
                    Field methodField = SizeUnitType.class.getDeclaredField(methodFieldName);
                    methodField.setAccessible(true);
                    Method method = SizeUnitUtil.class.getDeclaredMethod((String) methodField.get(sizeUnitType), Context.class, float.class);
                    method.setAccessible(true);
                    ret = (float) method.invoke(null, context, valueFloat);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    private static float getPxFromDp(Context context, float dpValue) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(metrics);

        return dpValue * metrics.density;
    }

    private static float getPxFromPercentForX(Context context, float percentValue) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels * percentValue / 100;
    }

    private static float getPxFromPercentForY(Context context, float percentValue) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(metrics);

        return metrics.heightPixels * percentValue / 100;
    }

    private static float getPxFromPx(Context context, float pxValue) {
        return pxValue;
    }

    private static float getPxFromSp(Context context, float spValue) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(metrics);

        return spValue * metrics.scaledDensity;
    }
}

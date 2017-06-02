package jp.eq_inc.testuiautomator.util;

import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class UiObjectUtil {
    public static UiObject findUiObjectFromScrollable(UiObject scrollableUiObject, String targetText) {
        UiObject ret = null;

        try {
            if (scrollableUiObject.isScrollable()) {
                UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
                Rect scrollableUiObjectBounds = scrollableUiObject.getBounds();
                int centerXinBounds = scrollableUiObjectBounds.centerX();
                int centerYinBounds = scrollableUiObjectBounds.centerY();
                int width = scrollableUiObjectBounds.width();
                int height = scrollableUiObjectBounds.height();

                // 一旦左上にスクロールしてしまう
                int count = 1;
                while(true){
                    if(!device.swipe(
                            centerXinBounds,
                            centerYinBounds,
                            centerXinBounds + count,
                            centerYinBounds + count, 2)){
                        break;
                    }
                    count++;
                }

                do {
                    do{
                        for (int i = 0, childCount = scrollableUiObject.getChildCount(); i < childCount; i++) {
                            UiObject childObject = scrollableUiObject.getChild(new UiSelector().index(i));
                            if (UiObjectUtil.existTargetText(childObject, targetText, false)) {
                                ret = childObject;
                                break;
                            }
                        }
                    }while ((ret == null) && device.swipe(centerXinBounds, centerYinBounds, centerXinBounds - width, centerYinBounds, 10));

                    // 元の位置に戻す
                    while (device.swipe(centerXinBounds, centerYinBounds, centerXinBounds + width, centerYinBounds, 10));
                }while((ret == null) && device.swipe(centerXinBounds, centerYinBounds, centerXinBounds, centerYinBounds + height, 10));
            }
        } catch (UiObjectNotFoundException e) {

        }

        return ret;
    }

    public static String[] getAllText(UiObject uiObject) {
        ArrayList<String> ret = new ArrayList<String>();

        if (uiObject != null) {
            try {
                String uiObjectText = uiObject.getText();

                if (uiObjectText != null && uiObjectText.length() > 0) {
                    ret.add(uiObjectText);
                }

                int childUiObjectCount = uiObject.getChildCount();
                if (childUiObjectCount > 0) {
                    for (int i = 0; i < childUiObjectCount; i++) {
                        String[] tempRet = getAllText(uiObject.getChild(new UiSelector().index(i)));
                        if (tempRet.length > 0) {
                            for (String textOfChild : tempRet) {
                                ret.add(textOfChild);
                            }
                        }
                    }
                }
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }

        return ret.toArray(new String[ret.size()]);
    }

    public static boolean existTargetText(UiObject uiObject, String targetText, boolean ignoreCase) {
        boolean ret = false;

        if (uiObject != null) {
            try {
                String uiObjectText = uiObject.getText();

                if (uiObjectText != null && (ignoreCase ? uiObjectText.equalsIgnoreCase(targetText) : uiObjectText.equals(targetText))) {
                    ret = true;
                } else {
                    int childUiObjectCount = uiObject.getChildCount();
                    if (childUiObjectCount > 0) {
                        for (int i = 0; i < childUiObjectCount; i++) {
                            ret = existTargetText(uiObject.getChild(new UiSelector().index(i)), targetText, ignoreCase);
                            if (ret) {
                                break;
                            }
                        }
                    }
                }
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    public static UiObject[] getAllChildUiObject(UiObject uiObject, String targetText) {
        ArrayList<UiObject> ret = new ArrayList<UiObject>();

        if (uiObject != null) {
            try {
                String uiObjectText = uiObject.getText();

                if (uiObjectText != null && uiObjectText.equals(targetText)) {
                    ret.add(uiObject);
                }

                int childUiObjectCount = uiObject.getChildCount();
                if (childUiObjectCount > 0) {
                    for (int i = 0; i < childUiObjectCount; i++) {
                        UiObject[] tempRet = getAllChildUiObject(uiObject.getChild(new UiSelector().index(i)), targetText);
                        if (tempRet.length > 0) {
                            for (UiObject childUiObject : tempRet) {
                                ret.add(childUiObject);
                            }
                        }
                    }
                }
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }

        return ret.toArray(new UiObject[0]);
    }

    public static UiObject getChildUiObject(UiObject uiObject, String indexText) {
        UiObject ret = null;

        if (uiObject != null) {
            try {
                int index = Integer.valueOf(indexText);
                ret = getChildUiObject(uiObject, index);
            } catch (NumberFormatException e) {
            }
        }

        return ret;
    }

    public static UiObject getChildUiObject(UiObject uiObject, int index) {
        UiObject ret = null;

        if (uiObject != null) {
            try {
                int childUiObjectCount = uiObject.getChildCount();
                if ((childUiObjectCount > 0) && (index < childUiObjectCount)) {
                    ret = uiObject.getChild(new UiSelector().index(index));
                }
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    public static void dumpUiObject(UiObject uiObject) {
        StringBuilder dumpBuilder = new StringBuilder();
        innerDumpUiObject(uiObject, dumpBuilder, 0);
        Log.d(UiObjectUtil.class.getSimpleName(), dumpBuilder.toString());
    }

    private static void innerDumpUiObject(UiObject uiObject, StringBuilder dumpBuilder, int indent) {
        try {
            String text = uiObject.getText();
            StringBuilder indentBuilder = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                indentBuilder.append(" ");
            }
            dumpBuilder.append(indentBuilder).append("text: ").append(text).append("\n");

            int childCount = uiObject.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    innerDumpUiObject(uiObject.getChild(new UiSelector().index(i)), dumpBuilder, indent + 1);
                }
            }
        } catch (UiObjectNotFoundException e) {

        }
    }
}

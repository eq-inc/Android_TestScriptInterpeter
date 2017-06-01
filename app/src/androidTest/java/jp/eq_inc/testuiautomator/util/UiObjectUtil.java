package jp.eq_inc.testuiautomator.util;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import java.util.ArrayList;

public class UiObjectUtil {
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

    public static void dumpUiObject(UiObject uiObject){
        StringBuilder dumpBuilder = new StringBuilder();
        innerDumpUiObject(uiObject, dumpBuilder, 0);
        Log.d(UiObjectUtil.class.getSimpleName(), dumpBuilder.toString());
    }

    private static void innerDumpUiObject(UiObject uiObject, StringBuilder dumpBuilder, int indent){
        try {
            String text = uiObject.getText();
            StringBuilder indentBuilder = new StringBuilder();
            for(int i=0; i<indent; i++){
                indentBuilder.append(" ");
            }
            dumpBuilder.append(indentBuilder).append("text: ").append(text).append("\n");

            int childCount = uiObject.getChildCount();
            if(childCount > 0){
                for(int i=0; i<childCount; i++){
                    innerDumpUiObject(uiObject.getChild(new UiSelector().index(i)), dumpBuilder, indent + 1);
                }
            }
        }catch (UiObjectNotFoundException e){

        }
    }
}

package jp.eq_inc.testuiautomator.data;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import java.util.Arrays;

public class ConfigData {
    public enum SupportScreenRecordParameter {
        Size("size"),
        BitRate("bit-rate"),
        Bugreport("bugreport"),
        TimeLimit("time-limit"),
        ;

        String mParamName;

        SupportScreenRecordParameter(String paramName){
            mParamName = paramName;
        }

        public String paramName(){
            return mParamName;
        }
    }

    public enum ProcedureType {
        Click,
        ClickSystemKey,
        Drag,
        DumpWindowHierarchy,
        FreezeOrientateScreen,
        InputText,
        LongClick,
        ScreenShot,
        ScreenRotateLeft,
        ScreenRotateNatural,
        ScreenRotateRight,
        SelectItem,
        Sleep,
        StartScreenRecord,
        StopScreenRecord,
        Swipe,
        Test,
        UnFreezeOrientateScreen,
        ;

        public static ProcedureType value(String name) {
            ProcedureType[] values = ProcedureType.values();
            ProcedureType ret = null;

            for (ProcedureType tempValue : values) {
                if (tempValue.name().equalsIgnoreCase(name)) {
                    ret = tempValue;
                    break;
                }
            }

            return ret;
        }
    }

    public enum SystemKey {
        Back,
        Delete,
        DPadCenter,
        DPadDown,
        DPadLeft,
        DPadRight,
        DPadUp,
        Enter,
        Home,
        Menu,
        RecentApps,
        Search;

        public static SystemKey value(String name) {
            SystemKey[] values = SystemKey.values();
            SystemKey ret = null;

            for (SystemKey tempValue : values) {
                if (tempValue.name().equalsIgnoreCase(name)) {
                    ret = tempValue;
                    break;
                }
            }

            return ret;
        }
    }

    public enum ParameterType {
        Checkable,
        Checked,
        Clickable,
        Enabled,
        Focusable,
        Focused,
        Index,
        LongClickable,
        OffsetX,
        OffsetY,
        PositionX,
        PositionY,
        Quality,
        Scale,
        Scrollable,
        Selected,
        SizeX,
        SizeY,
        Steps,
        Suffix,
        Text,
        TimeMS,
        WaitHidePackage,
        WaitHideItemByResourceId,
        WaitHideItemByText,
        WaitShowPackage,
        WaitShowItemByResourceId,
        WaitShowItemByText,
        WaitTimeoutMS;

        public static ParameterType value(String name) {
            ParameterType[] values = ParameterType.values();
            ParameterType ret = null;

            for (ParameterType tempValue : values) {
                if (tempValue.name().equalsIgnoreCase(name)) {
                    ret = tempValue;
                    break;
                }
            }

            return ret;
        }
    }

    public TestData[] test;

    public static class TestData {
        public String testApplicationId;
        public String testActivity;
        public TestProcedure[] testProcedures;

        @Override
        public String toString() {
            return "TestData{" +
                    "testApplicationId='" + testApplicationId + '\'' +
                    ", testActivity='" + testActivity + '\'' +
                    ", testProcedures=" + Arrays.toString(testProcedures) +
                    '}';
        }
    }

    public static class TestProcedure {
        public String type;
        public TargetItem targetItem;
        public TestParameter[] testParams;

        public UiObject findUiObject(UiDevice device) {
            UiObject ret = null;
            UiSelector uiSelector = null;

            if (targetItem != null) {
                if (targetItem.itemResourceId != null) {
                    uiSelector = new UiSelector().resourceId(targetItem.itemResourceId);
                    ret = device.findObject(uiSelector);
                }

                if (ret == null) {
                    uiSelector = new UiSelector();
                    if (targetItem.itemText != null && targetItem.itemText.length() > 0) {
                        uiSelector = uiSelector.text(targetItem.itemText);
                    }
                    if (targetItem.itemClass != null && targetItem.itemClass.length() > 0) {
                        uiSelector = uiSelector.className(targetItem.itemClass);
                    }
                    ret = device.findObject(uiSelector);
                }
            }

            if(ret != null){
                try {
                    if(ret.isScrollable()){
                        ret = new UiScrollable(uiSelector);
                    }
                } catch (UiObjectNotFoundException e) {
                }
            }

            return ret;
        }

        public boolean needUiObject() {
            boolean ret = false;

            if (targetItem != null) {
                if (targetItem.itemResourceId != null) {
                    ret = true;
                } else if (targetItem.itemText != null || targetItem.itemClass != null) {
                    ret = true;
                }
            }

            return ret;
        }

        public TestParameter getParam(int index) {
            TestParameter ret = null;

            if (testParams != null && testParams.length > index) {
                ret = testParams[index];
            }

            return ret;
        }

        public TestParameter getParam(ParameterType type) {
            TestParameter ret = null;

            if (testParams != null && testParams.length > 0) {
                for (TestParameter tempParam : testParams) {
                    if (tempParam.name.equalsIgnoreCase(type.name())) {
                        ret = tempParam;
                        break;
                    }
                }
            }

            return ret;
        }

        @Override
        public String toString() {
            return "TestProcedure{" +
                    "type='" + type + '\'' +
                    ", targetItem=" + targetItem +
                    ", testParams=" + Arrays.toString(testParams) +
                    '}';
        }
    }

    public static class TargetItem {
        public String itemClass;
        public String itemText;
        public String itemResourceId;

        @Override
        public String toString() {
            return "TargetItem{" +
                    "itemClass='" + itemClass + '\'' +
                    ", itemText='" + itemText + '\'' +
                    ", itemResourceId='" + itemResourceId + '\'' +
                    '}';
        }
    }

    public static class TestParameter {
        public String name;
        public String value;

        @Override
        public String toString() {
            return "TestParameter{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}

package jp.eq_inc.testuiautomator;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.eq_inc.testuiautomator.data.ConfigData;
import jp.eq_inc.testuiautomator.exception.IllegalParamException;
import jp.eq_inc.testuiautomator.exception.TestAbortException;
import jp.eq_inc.testuiautomator.exception.UiAutomatorException;
import jp.eq_inc.testuiautomator.util.SizeUnitUtil;
import jp.eq_inc.testuiautomator.util.UiObjectUtil;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class TestDynamicUIAutomator {
    private static final String TAG = TestDynamicUIAutomator.class.getSimpleName();
    private static final String TEST_CONFIG_FILE_NAME = "ui_automator.json";
    private static final String EXTERNAL_TEST_CONFIG_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + TEST_CONFIG_FILE_NAME;

    private static final int LAUNCH_TIMEOUT = 5000;
    private static final int SCREEN_POLLING_INTERVAL_MS = 100;
    private UiDevice mDevice;
    private ConfigData mConfigData;
    private String mRunningScreenRecoderProcess;

    @Before
    public void start() {
        if (readConfig()) {
            // Initialize UiDevice instance
            mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

            // Start from the home screen
            mDevice.pressHome();

            // Wait for launcher
            final String launcherPackage = mDevice.getLauncherPackageName();
            assertThat(launcherPackage, notNullValue());
            mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
        }
    }

//    @After
//    public void stop() {
//        if ((mDevice != null) && (mRunningScreenRecoderProcess != null)) {
//            String processId = getProcessId(mRunningScreenRecoderProcess);
//
//            mRunningScreenRecoderProcess = null;
//            try {
//                mDevice.executeShellCommand("kill -9 " + processId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Test
    public void test() {
        if (mConfigData != null) {
            for (ConfigData.TestData test : mConfigData.test) {
                // Launch the app
                Context context = InstrumentationRegistry.getContext();
                final Intent intent = context.getPackageManager().getLaunchIntentForPackage(test.testApplicationId);

                if (intent != null) {
                    // Clear out any previous instances
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                    // Wait for the app to appear
                    mDevice.wait(Until.hasObject(By.pkg(test.testApplicationId).depth(0)), LAUNCH_TIMEOUT);

                    for (ConfigData.TestProcedure procedure : test.testProcedures) {
                        try {
                            ConfigData.ProcedureType procedureType = ConfigData.ProcedureType.value(procedure.type);
                            if (procedureType != null) {
                                String methodName = "procedure" + procedureType.name();
                                Method procedureMethod = getClass().getDeclaredMethod(methodName, ConfigData.TestProcedure.class);
                                procedureMethod.invoke(this, procedure);
                            } else {
                                UiAutomatorException.throwException(procedure, "unknown process type: " + procedure.type);
                            }
                        } catch (TestAbortException e) {
                            e.printStackTrace();
                            assertTrue(false);
                        } catch (UiAutomatorException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e(TAG, "not found application: " + test.testApplicationId);
                }
            }

            if (mRunningScreenRecoderProcess != null) {
                try {
                    mDevice.executeShellCommand("kill -9 " + mRunningScreenRecoderProcess);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mRunningScreenRecoderProcess = null;
            }
        }
    }

    private boolean readConfig() {
        boolean ret = false;
        File configFile = new File(EXTERNAL_TEST_CONFIG_FILE_PATH);
        InputStream configInputStream = null;

        if (configFile.exists()) {
            try {
                configInputStream = new FileInputStream(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            AssetManager assetManager = InstrumentationRegistry.getContext().getAssets();
            try {
                configInputStream = assetManager.open(TEST_CONFIG_FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (configInputStream != null) {
            try {
                Gson gson = new Gson();
                mConfigData = gson.fromJson(new InputStreamReader(configInputStream), ConfigData.class);
                ret = true;
            } finally {
                try {
                    configInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    private void procedureTest(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure != null) {
            if (procedure.needUiObject()) {
                UiObject item = procedure.findUiObject(mDevice);

                if (item != null) {
                    if ((procedure.testParams != null) && (procedure.testParams.length > 0)) {
                        for (ConfigData.TestParameter testParam : procedure.testParams) {
                            ConfigData.ParameterType paramType = ConfigData.ParameterType.value(testParam.name);

                            try {
                                switch (paramType) {
                                    case Checkable:
                                        Assert.assertEquals(item.isCheckable(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Checked:
                                        Assert.assertEquals(item.isChecked(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Clickable:
                                        Assert.assertEquals(item.isClickable(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Enabled:
                                        Assert.assertEquals(item.isEnabled(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Focusable:
                                        Assert.assertEquals(item.isFocusable(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Focused:
                                        Assert.assertEquals(item.isFocused(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case LongClickable:
                                        Assert.assertEquals(item.isLongClickable(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Scrollable:
                                        Assert.assertEquals(item.isScrollable(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Selected:
                                        Assert.assertEquals(item.isSelected(), (boolean) Boolean.valueOf(testParam.value));
                                        break;
                                    case Text:
                                        Assert.assertTrue(UiObjectUtil.existTargetText(item, testParam.value, false));
                                        break;
                                }
                            } catch (UiObjectNotFoundException e) {
                                IllegalParamException.throwException(e, procedure, "testParams has no parameters");
                            }
                        }
                    } else {
                        IllegalParamException.throwException(procedure, "testParams has no parameters");
                    }
                } else {
                    TestAbortException.throwException(procedure, "target UI item is not in screen: \n" + procedure);
                }
            }
        }
    }

    private void procedureClick(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure != null) {
            if (procedure.needUiObject()) {
                UiObject item = procedure.findUiObject(mDevice);

                if (item != null) {
                    try {
                        item.click();
                    } catch (UiObjectNotFoundException e) {
                        IllegalParamException.throwException(e, procedure, "ui object not found: " + procedure.toString());
                    }
                }
            } else {
                Context context = InstrumentationRegistry.getContext();
                ConfigData.TestParameter testParamPositionX = procedure.getParam(ConfigData.ParameterType.PositionX);
                ConfigData.TestParameter testParamPositionY = procedure.getParam(ConfigData.ParameterType.PositionY);

                if ((testParamPositionX != null) && (testParamPositionY != null)) {
                    String posXText = testParamPositionX.value;
                    String posYText = testParamPositionY.value;

                    float posX = SizeUnitUtil.getPositionX(context, posXText);
                    float posY = SizeUnitUtil.getPositionY(context, posYText);
                    mDevice.click((int) posX, (int) posY);
                }
            }
        }
    }

    private void procedureDrag(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure != null) {
            if (procedure.needUiObject()) {
                UiObject item = procedure.findUiObject(mDevice);

                if (item != null) {
                    try {
                        item.click();
                    } catch (UiObjectNotFoundException e) {
                        IllegalParamException.throwException(e, procedure, "ui object not found: " + procedure.toString());
                    }
                }
            }else{
                Context context = InstrumentationRegistry.getContext();
                ConfigData.TestParameter testParamPositionX = procedure.getParam(ConfigData.ParameterType.PositionX);
                ConfigData.TestParameter testParamPositionY = procedure.getParam(ConfigData.ParameterType.PositionY);
                ConfigData.TestParameter testParamSizeX = procedure.getParam(ConfigData.ParameterType.SizeX);
                ConfigData.TestParameter testParamSizeY = procedure.getParam(ConfigData.ParameterType.SizeY);

                if ((testParamPositionX != null) && (testParamPositionY != null) && (testParamSizeX != null) && (testParamSizeY != null)) {
                    String posXText = testParamPositionX.value;
                    String posYText = testParamPositionY.value;
                    String sizeXText = testParamSizeX.value;
                    String sizeYText = testParamSizeY.value;

                    float posX = SizeUnitUtil.getPositionX(context, posXText);
                    float posY = SizeUnitUtil.getPositionY(context, posYText);
                    float sizeX = SizeUnitUtil.getPositionX(context, sizeXText);
                    float sizeY = SizeUnitUtil.getPositionY(context, sizeYText);
                    int steps = (int) Math.ceil(Math.sqrt(Math.pow(sizeX - posX, 2) + Math.pow(sizeY - posY, 2))) / 10;

                    ConfigData.TestParameter testParamSteps = procedure.getParam(ConfigData.ParameterType.Steps);
                    if (testParamSteps != null) {
                        try {
                            steps = Integer.valueOf(testParamSteps.value);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    mDevice.drag((int) posX, (int) posY, (int) (posX + sizeX), (int) (posY + sizeY), steps);
                }
            }
        }
    }

    private void procedureLongClick(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure != null) {
            UiObject item = procedure.findUiObject(mDevice);

            if (item != null) {
                try {
                    item.longClick();
                } catch (UiObjectNotFoundException e) {
                    IllegalParamException.throwException(e, procedure, "ui object not found: " + procedure.toString());
                }
            }
        }
    }

    private void procedureScreenShot(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure != null) {
            File screenshotSaveDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Android/data/" + InstrumentationRegistry.getContext().getPackageName() + File.separator + "screenshots");

            if (!screenshotSaveDir.exists()) {
                if (!screenshotSaveDir.mkdirs()) {
                    try {
                        mDevice.executeShellCommand("mkdir -p " + screenshotSaveDir.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            StringBuilder commandBuilder = new StringBuilder();
            commandBuilder.append("screencap -p ").append(screenshotSaveDir.getAbsolutePath()).append(File.separator);
            commandBuilder.append(getCurrentDateText(true, "", "", "", ""));

            ConfigData.TestParameter testSuffixParam = procedure.getParam(ConfigData.ParameterType.Suffix);
            if ((testSuffixParam != null) && (testSuffixParam.value != null) && (testSuffixParam.value.length() > 0)) {
                commandBuilder.append("_").append(testSuffixParam.value).append(".png");
            } else {
                commandBuilder.append(".png");
            }

            try {
                mDevice.executeShellCommand(commandBuilder.toString());
            } catch (IOException e) {
                UiAutomatorException.throwException(e, procedure, e.toString());
            }
        }
    }

    private void procedureSelectItem(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        // Spinnerからのドロップダウンメニューの中身を選択する場合のルート
        // この場合は選択が困難なため、Dpadを疑似的に使用したようにふるまう。
        // ただし、最初のDpadDownはtouch -> keyモードへの切り替えとして消費されるので、1回多く押下させる
        ConfigData.TestParameter testParam = procedure.getParam(ConfigData.ParameterType.Index);
        if (testParam != null) {
            int keyDownDpadDownCount = 0;

            try {
                keyDownDpadDownCount = Integer.valueOf(testParam.value);

                for (int i = 0; i < (keyDownDpadDownCount + 1); i++) {
                    mDevice.pressDPadDown();
                }
                mDevice.pressDPadCenter();
            } catch (NumberFormatException e) {
                IllegalParamException.throwException(e, procedure, "value of Index parameter is not a integer number: " + procedure.toString());
            }
            return;
        }

        testParam = procedure.getParam(ConfigData.ParameterType.Text);
        if (testParam != null) {
            while (true) {
                mDevice.pressDPadDown();
                UiObject childUiObject = mDevice.findObject(new UiSelector().selected(true));
                if (childUiObject == null || !childUiObject.exists()) {
                    break;
                } else {
                    UiObjectUtil.dumpUiObject(childUiObject);
                    if (UiObjectUtil.existTargetText(childUiObject, testParam.value, false)) {
                        mDevice.pressDPadCenter();
                        break;
                    }
                }
            }

            return;
        }
    }

    private void procedureSleep(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure != null) {
            ConfigData.TestParameter param = procedure.getParam(ConfigData.ParameterType.TimeMS);
            if (param != null) {
                try {
                    float valueFloat = Float.valueOf(param.value);
                    Thread.sleep((int) valueFloat);
                } catch (NumberFormatException e) {
                    IllegalParamException.throwException(e, procedure, e.getLocalizedMessage());
                } catch (InterruptedException e) {
                    // 処理なし
                }

                return;
            }

            int timeoutMS = LAUNCH_TIMEOUT;
            ConfigData.TestParameter paramTimeoutMS = procedure.getParam(ConfigData.ParameterType.WaitTimeoutMS);
            if (paramTimeoutMS != null) {
                try {
                    float valueFloat = Float.valueOf(paramTimeoutMS.value);
                    timeoutMS = (int) valueFloat;
                } catch (NumberFormatException e) {
                    IllegalParamException.throwException(e, procedure, e.getLocalizedMessage());
                }
            }

            param = procedure.getParam(ConfigData.ParameterType.WaitShowPackage);
            if (param != null) {
                mDevice.wait(Until.hasObject(By.pkg(param.value).depth(0)), timeoutMS);
                return;
            }

            param = procedure.getParam(ConfigData.ParameterType.WaitShowItemByText);
            if (param != null) {
                int totalSleepIntervalMS = 0;

                while (totalSleepIntervalMS < timeoutMS) {
                    UiObject targetItem = mDevice.findObject(new UiSelector().text(param.value));
                    if (targetItem != null) {
                        break;
                    } else {
                        try {
                            Thread.sleep(SCREEN_POLLING_INTERVAL_MS);
                        } catch (InterruptedException e) {
                            // 処理なし
                        } finally {
                            totalSleepIntervalMS += SCREEN_POLLING_INTERVAL_MS;
                        }
                    }
                }
                return;
            }

            param = procedure.getParam(ConfigData.ParameterType.WaitShowItemByResourceId);
            if (param != null) {
                int totalSleepIntervalMS = 0;

                while (totalSleepIntervalMS < timeoutMS) {
                    UiObject targetItem = mDevice.findObject(new UiSelector().resourceId(param.value));
                    if ((targetItem != null) && targetItem.exists()) {
                        break;
                    } else {
                        try {
                            Thread.sleep(SCREEN_POLLING_INTERVAL_MS);
                        } catch (InterruptedException e) {
                            // 処理なし
                        } finally {
                            totalSleepIntervalMS += SCREEN_POLLING_INTERVAL_MS;
                        }
                    }
                }
                return;
            }
        }
    }

    private void procedureStartScreenRecord(final ConfigData.TestProcedure procedure) throws UiAutomatorException {
//        if (procedure != null) {
//            synchronized (TestDynamicUIAutomator.this) {
//                if (mRunningScreenRecoderProcess == null) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            synchronized (TestDynamicUIAutomator.this) {
//                                if (mRunningScreenRecoderProcess == null) {
//                                    mRunningScreenRecoderProcess = String.valueOf(Process.myPid());
//                                    mRunningScreenRecoderProcess = String.valueOf(Process.myTid());
//                                    File screenrecordSaveDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Android/data/" + InstrumentationRegistry.getContext().getPackageName() + File.separator + "screenrecords");
//
//                                    if (!screenrecordSaveDir.exists()) {
//                                        if (!screenrecordSaveDir.mkdirs()) {
//                                            try {
//                                                mDevice.executeShellCommand("mkdir -p " + screenrecordSaveDir.getAbsolutePath());
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//
//                                    StringBuilder commandBuilder = new StringBuilder();
//                                    commandBuilder.append("screenrecord ");
//
//                                    if ((procedure.testParams != null) && (procedure.testParams.length > 0)) {
//                                        for (ConfigData.TestParameter testParam : procedure.testParams) {
//                                            String lowerTestParamName = testParam.name.toLowerCase();
//                                            boolean matched = false;
//
//                                            for (ConfigData.SupportScreenRecordParameter param : ConfigData.SupportScreenRecordParameter.values()) {
//                                                if (lowerTestParamName.equals(param.paramName())) {
//                                                    matched = true;
//                                                    break;
//                                                }
//                                            }
//
//                                            if (matched) {
//                                                commandBuilder.append("--").append(lowerTestParamName).append(" ").append(testParam.value).append(" ");
//                                            }
//                                        }
//                                    }
//
//                                    commandBuilder.append(screenrecordSaveDir.getAbsolutePath()).append(File.separator);
//                                    commandBuilder.append(getCurrentDateText(true, "", "", "", ""));
//
//                                    ConfigData.TestParameter testSuffixParam = procedure.getParam(ConfigData.ParameterType.Suffix);
//                                    if ((testSuffixParam != null) && (testSuffixParam.value != null) && (testSuffixParam.value.length() > 0)) {
//                                        commandBuilder.append("_").append(testSuffixParam.value).append(".mp4");
//                                    } else {
//                                        commandBuilder.append(".mp4");
//                                    }
//
//                                    try {
//                                        mDevice.executeShellCommand(commandBuilder.toString());
//                                    } catch (IOException e) {
//                                        //UiAutomatorException.throwException(e, procedure, e.toString());
//                                    } finally {
//                                        mRunningScreenRecoderProcess = null;
//                                    }
//                                }
//                            }
//                        }
//                    }).start();
//                }
//            }
//        }
    }

    private void procedureStopScreenRecord(final ConfigData.TestProcedure procedure) throws UiAutomatorException {
//        if (mRunningScreenRecoderProcess != null) {
//            try {
//                mDevice.executeShellCommand("kill -9 " + mRunningScreenRecoderProcess);
//                mRunningScreenRecoderProcess = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void procedureSwipe(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure != null) {
            if (procedure.needUiObject()) {
                UiObject item = procedure.findUiObject(mDevice);

                if (item != null) {
                    ConfigData.TestParameter param = procedure.getParam(0);
                    if (param != null) {
                        ConfigData.ParameterType paramType = ConfigData.ParameterType.value(param.name);
                        String value = param.value;

                        if ((paramType != null) && (paramType == ConfigData.ParameterType.SizeX || paramType == ConfigData.ParameterType.SizeY)) {
                            try {
                                float valueFloat = Float.valueOf(value);
                                try {
                                    switch (paramType) {
                                        case SizeX:
                                            if (valueFloat >= 0) {
                                                item.swipeLeft((int) valueFloat);
                                            } else {
                                                valueFloat *= -1;
                                                item.swipeRight((int) valueFloat);
                                            }
                                            break;
                                        case SizeY:
                                            if (valueFloat >= 0) {
                                                item.swipeUp((int) valueFloat);
                                            } else {
                                                valueFloat *= -1;
                                                item.swipeDown((int) valueFloat);
                                            }
                                            break;
                                    }
                                } catch (UiObjectNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                Context context = InstrumentationRegistry.getContext();
                ConfigData.TestParameter testParamPositionX = procedure.getParam(ConfigData.ParameterType.PositionX);
                ConfigData.TestParameter testParamPositionY = procedure.getParam(ConfigData.ParameterType.PositionY);
                ConfigData.TestParameter testParamSizeX = procedure.getParam(ConfigData.ParameterType.SizeX);
                ConfigData.TestParameter testParamSizeY = procedure.getParam(ConfigData.ParameterType.SizeY);

                if ((testParamPositionX != null) && (testParamPositionY != null) && (testParamSizeX != null) && (testParamSizeY != null)) {
                    String posXText = testParamPositionX.value;
                    String posYText = testParamPositionY.value;
                    String sizeXText = testParamSizeX.value;
                    String sizeYText = testParamSizeY.value;

                    float posX = SizeUnitUtil.getPositionX(context, posXText);
                    float posY = SizeUnitUtil.getPositionY(context, posYText);
                    float sizeX = SizeUnitUtil.getPositionX(context, sizeXText);
                    float sizeY = SizeUnitUtil.getPositionY(context, sizeYText);
                    int steps = (int) Math.ceil(Math.sqrt(Math.pow(sizeX - posX, 2) + Math.pow(sizeY - posY, 2))) / 10;

                    ConfigData.TestParameter testParamSteps = procedure.getParam(ConfigData.ParameterType.Steps);
                    if (testParamSteps != null) {
                        try {
                            steps = Integer.valueOf(testParamSteps.value);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    mDevice.swipe((int) posX, (int) posY, (int) (posX + sizeX), (int) (posY + sizeY), steps);
                }
            }
        }
    }

    private void procedureClickSystemKey(ConfigData.TestProcedure procedure) throws UiAutomatorException {
        if (procedure.targetItem != null && procedure.targetItem.itemText != null) {
            switch (ConfigData.SystemKey.value(procedure.targetItem.itemText)) {
                case Back:
                    if (!mDevice.pressBack()) {
                        Log.e(TAG, "press back fail");
                    }
                    break;
                case Delete:
                    if (!mDevice.pressDelete()) {
                        Log.e(TAG, "press delete fail");
                    }
                    break;
                case DPadCenter:
                    if (!mDevice.pressDPadCenter()) {
                        Log.e(TAG, "press dpad center fail");
                    }
                    break;
                case DPadDown:
                    if (!mDevice.pressDPadDown()) {
                        Log.e(TAG, "press dpad down fail");
                    }
                    break;
                case DPadLeft:
                    if (!mDevice.pressDPadLeft()) {
                        Log.e(TAG, "press dpad left fail");
                    }
                    break;
                case DPadRight:
                    if (!mDevice.pressDPadRight()) {
                        Log.e(TAG, "press dpad right fail");
                    }
                    break;
                case DPadUp:
                    if (!mDevice.pressDPadUp()) {
                        Log.e(TAG, "press dpad up fail");
                    }
                    break;
                case Enter:
                    if (!mDevice.pressEnter()) {
                        Log.e(TAG, "press enter fail");
                    }
                    break;
                case Home:
                    if (!mDevice.pressHome()) {
                        Log.e(TAG, "press home fail");
                    }
                    break;
                case Menu:
                    if (!mDevice.pressMenu()) {
                        Log.e(TAG, "press menu fail");
                    }
                    break;
                case RecentApps:
                    try {
                        if (!mDevice.pressRecentApps()) {
                            Log.e(TAG, "press recent apps fail");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case Search:
                    if (!mDevice.pressSearch()) {
                        Log.e(TAG, "press search fail");
                    }
                    break;
            }
        }
    }

    private static String getCurrentDateText(boolean enableZeroPadding, String dateSeparator, String dateTimeSeparator, String timeSeparator, String milliSecSeparator) {
        Calendar currentCalendar = Calendar.getInstance();
        StringBuilder dateTextBuilder = new StringBuilder();
        String twoPhraseFormat = enableZeroPadding ? "%02d" : "%d";
        String threePhraseFormat = enableZeroPadding ? "%03d" : "%d";
        String fourPhraseFormat = enableZeroPadding ? "%04d" : "%d";

        dateTextBuilder
                .append(String.format(fourPhraseFormat, currentCalendar.get(Calendar.YEAR)))
                .append(dateSeparator)
                .append(String.format(twoPhraseFormat, currentCalendar.get(Calendar.MONTH)))
                .append(dateSeparator)
                .append(String.format(twoPhraseFormat, currentCalendar.get(Calendar.DAY_OF_MONTH)))
                .append(dateTimeSeparator)
                .append(String.format(twoPhraseFormat, currentCalendar.get(Calendar.HOUR_OF_DAY)))
                .append(timeSeparator)
                .append(String.format(twoPhraseFormat, currentCalendar.get(Calendar.MINUTE)))
                .append(timeSeparator)
                .append(String.format(twoPhraseFormat, currentCalendar.get(Calendar.SECOND)))
                .append(milliSecSeparator)
                .append(String.format(threePhraseFormat, currentCalendar.get(Calendar.MILLISECOND)));

        return dateTextBuilder.toString();
    }

    private static String getProcessId(String stdoutByBgProcessing) {
        String ret = null;

        Pattern pattern = Pattern.compile("\\[[0-9]*\\]\\s+([0-9]+)");
        pattern.split(stdoutByBgProcessing);
        Matcher matcher = pattern.matcher(stdoutByBgProcessing);
        if (matcher.matches()) {
            ret = matcher.group(1);
        }

        return ret;
    }
}

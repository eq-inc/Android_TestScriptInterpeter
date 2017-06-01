package jp.eq_inc.testuiautomator;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


//@RunWith(AndroidJUnit4.class)
//@SdkSuppress(minSdkVersion = 18)
public class TestARandUHAutomator {
    private static final String BASIC_SAMPLE_PACKAGE
            = "jp.eq_inc.aranduh";
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;

    //@Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
    }

    //@Test
    public void testArOnCamera() {
        UiObject startButton = mDevice.findObject(new UiSelector().text("AR on camera").className("android.widget.TextView"));
        try {
            startButton.click();
            Thread.sleep(5 * 1000);
            if (!mDevice.pressBack()) {
                android.util.Log.e(TestARandUHAutomator.class.getSimpleName(), "press back fail");
            }
            if (!mDevice.pressBack()) {
                android.util.Log.e(TestARandUHAutomator.class.getSimpleName(), "press back fail");
            }
            if (!mDevice.pressBack()) {
                android.util.Log.e(TestARandUHAutomator.class.getSimpleName(), "press back fail");
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

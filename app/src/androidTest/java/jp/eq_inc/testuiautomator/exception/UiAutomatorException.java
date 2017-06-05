package jp.eq_inc.testuiautomator.exception;

import jp.eq_inc.testuiautomator.data.ConfigData;

public class UiAutomatorException extends Exception {
    protected ConfigData.TestProcedure mCauseByProcedure;
    protected String mAdditionalText;

    public static void throwException(Exception causedException, ConfigData.TestProcedure procedure, String additionalText) throws UiAutomatorException {
        if (causedException != null) {
            causedException.printStackTrace();
        }
        throwException(procedure, additionalText);
    }

    public static void throwException(ConfigData.TestProcedure procedure, String additionalText) throws UiAutomatorException {
        throw new UiAutomatorException(procedure, additionalText);
    }

    protected UiAutomatorException(ConfigData.TestProcedure procedure, String additionalText) {
        if (procedure == null) {
            throw new NullPointerException("procedure == null");
        }
        mCauseByProcedure = procedure;
        mAdditionalText = additionalText;
    }

    @Override
    public String toString() {
        if (mAdditionalText != null && mAdditionalText.length() > 0) {
            return mAdditionalText + "\ncaused by : " + mCauseByProcedure.toString() + "\n" + super.toString();
        } else {
            return "caused by : " + mCauseByProcedure.toString() + "\n" + super.toString();
        }
    }
}

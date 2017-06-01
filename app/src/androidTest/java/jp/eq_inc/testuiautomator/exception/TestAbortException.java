package jp.eq_inc.testuiautomator.exception;

import jp.eq_inc.testuiautomator.data.ConfigData;

public class TestAbortException extends UiAutomatorException {
    public static void throwException(Exception causedException, ConfigData.TestProcedure procedure, String additionalText) throws TestAbortException {
        if (causedException != null) {
            causedException.printStackTrace();
        }
        throwException(procedure, additionalText);
    }

    public static void throwException(ConfigData.TestProcedure procedure, String additionalText) throws TestAbortException {
        throw new TestAbortException(procedure, additionalText);
    }

    private TestAbortException(ConfigData.TestProcedure procedure, String additionalText) {
        super(procedure, additionalText);
    }
}

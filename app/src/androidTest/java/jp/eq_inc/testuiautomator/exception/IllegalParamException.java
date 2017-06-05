package jp.eq_inc.testuiautomator.exception;

import jp.eq_inc.testuiautomator.data.ConfigData;

public class IllegalParamException extends UiAutomatorException {
    public static void throwException(Exception causedException, ConfigData.TestProcedure procedure, String additionalText) throws IllegalParamException {
        if (causedException != null) {
            causedException.printStackTrace();
        }
        throwException(procedure, additionalText);
        throw new IllegalParamException(procedure, additionalText);
    }

    public static void throwException(ConfigData.TestProcedure procedure, String additionalText) throws IllegalParamException {
        throw new IllegalParamException(procedure, additionalText);
    }

    private IllegalParamException(ConfigData.TestProcedure procedure, String additionalText) {
        super(procedure, additionalText);
    }
}

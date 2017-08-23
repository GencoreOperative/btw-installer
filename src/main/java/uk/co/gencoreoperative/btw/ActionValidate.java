package uk.co.gencoreoperative.btw;

import java.util.function.Function;

/**
 * Function which will check and optionally validate the result of an action that needs
 * to be performed.
 *
 * The intended use case for this function is to assist with command line like operations
 * where we need to ensure the system is in a sane state before performing the operation.
 */
public class ActionValidate {
    public void actionValidate(String input, Function<String, Boolean> check, Function<String, Void> perform) {
        Boolean result = check.apply(input);
//        if (!result)
    }
}

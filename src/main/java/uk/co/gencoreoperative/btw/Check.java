package uk.co.gencoreoperative.btw;

/**
 * Responsible for checking some state or information.
 */
public interface Check {
    /**
     * @return True if the check succeeded. Otherwise false.
     */
    boolean check();

    /**
     * @return A message describing the check.
     */
    String item();
}

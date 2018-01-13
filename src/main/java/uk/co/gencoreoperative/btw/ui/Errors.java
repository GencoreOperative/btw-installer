package uk.co.gencoreoperative.btw.ui;

public enum Errors {
    MC_HOME_NOT_FOUND("Minecraft home folder not found"),
    MC_ONE_FIVE_TWO_NOT_FOUND("Could not find the 1.5.2 version"),
    BTW_RELEASE_NOT_FOUND("Could not find the Better Than Wolves version");


    private String reason;

    Errors(String reason) {
        this.reason = reason;
    }

    /**
     * @return Human readable reason for the error.
     */
    public String getReason() {
        return reason;
    }
}

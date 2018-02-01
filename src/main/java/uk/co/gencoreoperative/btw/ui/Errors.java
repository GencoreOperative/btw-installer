package uk.co.gencoreoperative.btw.ui;

public enum Errors {
    MC_HOME_NOT_FOUND("Minecraft home folder not found"),
    MC_ONE_FIVE_TWO_NOT_FOUND("Unable to find in Minecraft home or from Majong servers"),
    BTW_RELEASE_NOT_FOUND("Could not find the Better Than Wolves version"),
    FAILED_TO_DELETE_INSTALLATION("Unable to delete previous installation."),
    FAILED_TC_CREATE_FOLDER("Unable to create installation folder."),
    FAILED_TO_WRITE_JSON("Failed to write JSON required for installation.");


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

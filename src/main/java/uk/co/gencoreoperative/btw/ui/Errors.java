/*
 * Copyright 2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
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

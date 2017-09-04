package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.ui.Item;

public enum Tasks {
    ONE_FIVE_TWO_EXISTS("version 1.5.2 exists"),
    PREVIOUS_REMOVED("previous installation removed"),
    CREATED_FOLDER("created installation folder"),
    COPIED_JSON("copy BetterThanWolves.json"),
    PATCH_WAS_SELECTED("patch file was selected"),
    COPIED_JAR("created BetterThanWolves.jar"),
    INSTALLATION_FOLDER("minecraft installation was selected");

    private Item item;

    Tasks(String description) {
        this.item = new Item(description);
    }

    public Item getTask() {
        return item;
    }
}

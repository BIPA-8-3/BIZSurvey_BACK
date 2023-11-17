package com.bipa.bizsurvey.global.common.storage;

public enum Folder {
    IMAGES("images/"),
    FILE("files/");
    private final String folderName;

    Folder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}

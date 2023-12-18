package com.bipa.bizsurvey.infra.s3;

public enum S3Folder {
    IMAGES("images/"),
    FILE("files/");
    private final String folderName;

    S3Folder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}

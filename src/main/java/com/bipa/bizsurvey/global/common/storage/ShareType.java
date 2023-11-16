package com.bipa.bizsurvey.global.common.storage;

public enum ShareType {
    INTERNAL("internal"),
    EXTERNAL("external");
    private final String type;

    ShareType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

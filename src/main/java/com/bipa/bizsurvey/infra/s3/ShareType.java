package com.bipa.bizsurvey.infra.s3;

public enum ShareType {
    INTERNAL("internal"),
    EXTERNAL("external");
    private final String type;

    ShareType(String type) {
        this.type = type;
    }

    public String getType() {
        return type + "-";
    }
}

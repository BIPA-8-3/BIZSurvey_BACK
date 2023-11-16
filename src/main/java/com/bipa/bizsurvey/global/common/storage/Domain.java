package com.bipa.bizsurvey.global.common.storage;

public enum Domain {
    SURVEY("survey/"),
    USER("user/"),
    COMMUNITY("community/");
    private final String domainName;

    Domain(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}

package com.bipa.bizsurvey.infra.s3;

public enum S3Domain {
    SURVEY("survey/"),
    USER("user/"),
    COMMUNITY("community/");
    private final String domainName;

    S3Domain(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}

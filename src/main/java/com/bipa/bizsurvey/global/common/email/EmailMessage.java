package com.bipa.bizsurvey.global.common.email;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class EmailMessage {
    private String to;
    private String subject;

    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    public void put(String key, Object value) {
        if(this.variables == null) {
            this.variables = new HashMap<>();
        }
        this.variables.put(key, value);
    }
}

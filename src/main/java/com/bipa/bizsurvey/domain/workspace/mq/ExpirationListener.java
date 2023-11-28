package com.bipa.bizsurvey.domain.workspace.mq;

import com.bipa.bizsurvey.domain.workspace.application.WorkspaceAdminService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Log4j2
public class ExpirationListener implements MessageListener {
    @Autowired
    private WorkspaceAdminService workspaceAdminService;
    private final String PREFIX = "INVITE-";

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String bodyMessage = new String(message.getBody());
        String[] arr = null;

        if(bodyMessage.startsWith(PREFIX)) {
            arr = bodyMessage.substring(PREFIX.length()).split("_");
            String id = arr[0];
            workspaceAdminService.expireToken(Long.parseLong(id));
        }
    }
}

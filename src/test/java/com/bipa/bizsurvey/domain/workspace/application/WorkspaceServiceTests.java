package com.bipa.bizsurvey.domain.workspace.application;


import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceDto;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.global.common.email.MailUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest
@Log4j2
public class WorkspaceServiceTests {
    @Autowired
    private WorkspaceService workspaceService;

    @Test
    public void testCreate() {
        WorkspaceDto.CreateRequest request = new WorkspaceDto.CreateRequest();

        request.setWorkspaceName("test");
        request.setWorkspaceType(WorkspaceType.PERSONAL);
        log.info(workspaceService.create(1L, request));
    }

    @Test
    public void testUpdate() {
        WorkspaceDto.UpdateRequest request = new WorkspaceDto.UpdateRequest();
        Long id = 2L;
        request.setWorkspaceName("testUpdate");

        workspaceService.update(id, request);
        log.info(workspaceService.readOne(id));
    }

    @Test
    public void testList() {
//        workspaceService.listWorkspaces(1L).stream().forEach(log::info);
    }

    @Test
    public void testDelete() throws Exception {
        return;
    }
}

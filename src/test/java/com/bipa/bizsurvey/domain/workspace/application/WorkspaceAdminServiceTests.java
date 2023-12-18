package com.bipa.bizsurvey.domain.workspace.application;

import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import com.bipa.bizsurvey.domain.workspace.enums.AdminType;
import com.bipa.bizsurvey.global.common.RedisService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class WorkspaceAdminServiceTests {
    @Autowired
    private WorkspaceAdminService workspaceAdminService;

    @Test
    public void testInvite() throws Exception {
        WorkspaceAdminDto.InviteRequest request = new WorkspaceAdminDto.InviteRequest();

        request.setWorkspaceId(1L);
        request.setEmail("hws6745@naver.com");
        request.setAdminType(AdminType.INVITED);
        workspaceAdminService.invite(request);
    }

    @Test
    public void testAcceptInvite() {
        WorkspaceAdminDto.AcceptRequest request = new WorkspaceAdminDto.AcceptRequest();
        String token ="test";
        
        request.setToken("INVITE-" + token);
        request.setUserId(3L);

        workspaceAdminService.acceptInvite(request);
    }

    @Test
    public void testList() {
        log.info(workspaceAdminService.list(1L).toString());
    }



    @Test
    public void testDelete() {
        workspaceAdminService.delete(9L);
    }

}

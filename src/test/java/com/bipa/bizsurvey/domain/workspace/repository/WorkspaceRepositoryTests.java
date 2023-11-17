package com.bipa.bizsurvey.domain.workspace.repository;


import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.global.config.TestConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TestConfig.class)
@Log4j2
public class WorkspaceRepositoryTests {

    @Autowired
    private WorkspaceRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetList() {
        log.info(repository.findWorkspacesByUserId(1L));
    }

    @Test
    public void testSave() {
        Workspace workspace =  Workspace.builder()
                .workspaceName("test")
                .workspaceType(WorkspaceType.COMPANY)
                .user(userRepository.findById(1L).get())
                .build();

        log.info(workspace);

        repository.save(workspace);
        log.info(workspace);
    }

    @Test
    public void testUpdate() {
        Workspace workspace = repository.findById(1L).get();
        workspace.updateWorkspace("test");
    }

    public void testDelete() {
        Workspace workspace = repository.findById(1L).get();
        workspace.delete();
        repository.save(workspace);
    }
}

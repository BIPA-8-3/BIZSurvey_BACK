package com.bipa.bizsurvey.domain.workspace.application;


import com.bipa.bizsurvey.domain.workspace.dto.ContactDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class ContactServiceTests {
    @Autowired
    private ContactService contactService;


    @Test
    public void testCreate() {
        ContactDto.CreateRequest request = new ContactDto.CreateRequest();

        request.setWorkspaceId(1L);
        request.setEmail("test@naver.com");
        request.setName("test");
        request.setRemark("test");
        log.info(contactService.create(request));
    }

    @Test
    public void testUpdate() {
        ContactDto.UpdateRequest request = new ContactDto.UpdateRequest();
        Long id = 2L;
        request.setName("testUpdate");
        request.setEmail("testUpdate@naver.com");
        request.setRemark("testUpdate");

        contactService.update(id, request);
        log.info(contactService.readOne(id));
    }

    @Test
    public void testList() {
        ContactDto.SearchRequest request = new ContactDto.SearchRequest();
        request.setWorkspaceId(1L);
        request.setKeyword("update");
        contactService.searchContacts(request).stream().forEach(log::info);
    }

    @Test
    public void testDelete() {
        Long id = 1L;
        contactService.delete(id);
    }
}

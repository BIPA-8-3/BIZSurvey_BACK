package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.Contact;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Log4j2
public class ContactRepositoryTests {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    public void testPaging() {
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "contact_id"));
        Page<Contact> page = contactRepository.searchContact(1L, "test", pageRequest);

        log.info(page.getTotalElements());
    }
}

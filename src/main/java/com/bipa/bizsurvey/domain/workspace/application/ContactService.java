package com.bipa.bizsurvey.domain.workspace.application;

import com.bipa.bizsurvey.domain.workspace.domain.Contact;
import com.bipa.bizsurvey.domain.workspace.domain.QContact;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.dto.ContactDto;
import com.bipa.bizsurvey.domain.workspace.repository.ContactRepository;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ContactService {
    private final ContactRepository contactRepository;
    private final WorkspaceRepository workspaceRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public ContactDto.Response create(ContactDto.CreateRequest request) {
        Workspace workspace =  workspaceRepository.findWorkspaceByIdAndDelFlagFalse(request.getWorkspaceId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스 입니다."));

        Contact contact = Contact.builder()
                .workspace(workspace)
                .name(request.getName())
                .email(request.getEmail())
                .remark(request.getRemark())
                .build();

        contactRepository.save(contact);

        return ContactDto.Response.builder()
                .id(contact.getId())
                .email(contact.getEmail())
                .name(contact.getName())
                .remark(contact.getRemark())
                .build();
    }

    @Transactional(readOnly = true)
    public ContactDto.Response readOne(Long id) {
        Contact contact = contactRepository.findByIdAndDelFlagFalse(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 연락처입니다."));
        return ContactDto.Response.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .remark(contact.getRemark())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ContactDto.Response> searchContacts(ContactDto.SearchRequest request) {
        String keyword = request.getKeyword() == null ? "" : request.getKeyword();

        QContact qContact = QContact.contact;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(qContact.delFlag.eq(false));
        booleanBuilder.and(qContact.name.contains(keyword).or(qContact.email.contains(keyword)));

        List<Contact> list = jpaQueryFactory.select(qContact)
                .from(qContact)
                .where(booleanBuilder)
                .fetch();

        return list.stream().map(e -> ContactDto.Response.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .email(e.getEmail())
                    .remark(e.getRemark())
                    .build())
                    .collect(Collectors.toList());
    }

    public void update(Long id, ContactDto.UpdateRequest request) {
        Contact contact = contactRepository.findByIdAndDelFlagFalse(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 연락처입니다."));
        contact.update(request.getName(), request.getEmail(), request.getRemark());
    }

    public void delete(Long id) {
        Contact contact = contactRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 연락처입니다."));
        contact.delete();
    }
}

package com.bipa.bizsurvey.domain.workspace.repository;

import com.bipa.bizsurvey.domain.workspace.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByIdAndDelFlagFalse(Long id);
    @Query("SELECT c FROM Contact AS c WHERE c.delFlag = false " +
            "AND c.workspace.id = :workspaceId " +
            "AND (c.name LIKE %:keyword% OR c.email LIKE %:keyword%)")
    List<Contact> searchContact(Long workspaceId, String keyword);
}

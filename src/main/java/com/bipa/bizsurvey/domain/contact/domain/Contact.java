package com.bipa.bizsurvey.domain.contact.domain;


import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "contact")
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;

    @Column(nullable = false)
    private String registeredName;

    @Column(nullable = false)
    private String registeredEmail;

    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;


}

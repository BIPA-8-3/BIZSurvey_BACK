package com.bipa.bizsurvey.domain.workspace.domain;

import com.bipa.bizsurvey.domain.workspace.enums.AdminType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "workspace_admin")
public class WorkspaceAdmin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_admin_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminType adminType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
}

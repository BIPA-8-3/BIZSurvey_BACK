package com.bipa.bizsurvey.domain.workspace.domain;

import com.bipa.bizsurvey.domain.workspace.enums.AdminType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "workspace_admin")
@ToString
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

//    @ColumnDefault("false")
//    @Column(insertable = false)
//    private Boolean inviteFlag = false;

    private String token;
    private LocalDateTime expirationDate;
    private String remark;

    @Builder
    public WorkspaceAdmin(Workspace workspace, User user, AdminType adminType, String token, String remark) {
        this.workspace = workspace;
        this.user = user;
        this.adminType = adminType;
        this.token = token;
        this.remark = remark;
    }

    @PrePersist
    public void prePersist() {
        if (expirationDate == null) {
            expirationDate = LocalDateTime.now().plusDays(3);
        }
    }

    public void acceptInvite(User user) {
        this.user = user;
//        this.inviteFlag = true;
        this.remark = null;
        this.expireToken();
    }

    public void expireToken() {
        this.token = null;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}

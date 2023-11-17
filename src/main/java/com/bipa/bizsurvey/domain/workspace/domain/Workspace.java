package com.bipa.bizsurvey.domain.workspace.domain;

import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.dto.request.post.UpdatePostRequest;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;
import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "workspace")
@ToString(exclude = "user")
public class Workspace extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    private String workspaceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceType workspaceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Workspace(String workspaceName, WorkspaceType workspaceType, User user) {
        this.workspaceName = workspaceName;
        this.workspaceType = workspaceType;
        this.user = user;
    }

    public void updateWorkspace(String workspaceName) {
        this.workspaceName = workspaceName;
    }

}

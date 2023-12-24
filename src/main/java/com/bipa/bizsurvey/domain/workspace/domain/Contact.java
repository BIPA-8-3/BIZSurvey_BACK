package com.bipa.bizsurvey.domain.workspace.domain;


import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;
import org.hibernate.jdbc.Work;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "contact")
@ToString(exclude = "workspace")
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Builder
    public Contact(String name, String email, Workspace workspace) {
        this.name = name;
        this.email = email;
        this.workspace = workspace;
    }
    
    public void update(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

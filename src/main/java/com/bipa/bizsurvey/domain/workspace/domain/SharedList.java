package com.bipa.bizsurvey.domain.workspace.domain;


import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "shared_list")
public class SharedList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shared_list_id")
    private Long id;

//    @Column(nullable = false)
//    private String url;

//    @Column(nullable = false)
//    private Boolean responseFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_survey_id")
    private SharedSurvey sharedSurvey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

//    private String remark;

    @Builder
    public SharedList(SharedSurvey sharedSurvey, Contact contact) {
        this.sharedSurvey = sharedSurvey;
        this.contact = contact;
    }

}

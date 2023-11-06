package com.bipa.bizsurvey.domain.user.domain;

import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String birthdate;

    private String password;

    private String company;

    @Enumerated(EnumType.STRING)
    private Plan planSubscribe;

    @Builder
    public User(String email, String name, String nickname, Gender gender, String birthdate, String password, String company, Plan planSubscribe) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.password = password;
        this.company = company;
        this.planSubscribe = planSubscribe;
    }
}

package com.bipa.bizsurvey.domain.user.domain;

import com.bipa.bizsurvey.domain.user.dto.mypage.UserAdditionalJoinRequest;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoUpdateRequest;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Getter
@ToString
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

    //@Column(nullable = false)
    private String birthdate;

    private String password;

    private String company;

    @Enumerated(EnumType.STRING)
    private Plan planSubscribe;

    private String forbiddenDate;

    private String provider;

    private String profile;

    @Builder
    public User(Long id, String email, String name, String nickname, Gender gender, String birthdate, String password, String company, Plan planSubscribe, String provider) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.gender = gender;
        this.birthdate = birthdate;
        this.password = password;
        this.company = company;
        this.planSubscribe = planSubscribe;
        this.provider = provider;
    }

    public void userInfoUpdate(UserInfoUpdateRequest request){
        this.nickname = request.getNickname();
        this.birthdate = request.getBirthdate();
    }

    public void additionalJoin(UserAdditionalJoinRequest request){
        this.nickname = request.getNickname();
        this.birthdate = request.getBirthdate();
    }

    public void userPlanUpdate(Plan plan){
        this.planSubscribe = plan;
    }

    public void passowordUpdate(String password){
        this.password = password;
    }

    public void forbiddenDateUpdate(String date){
        this.forbiddenDate = date;
    }

    public void profileUpdate(String profile){
        this.profile = profile;
    }
}


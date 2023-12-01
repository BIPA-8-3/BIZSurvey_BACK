package com.bipa.bizsurvey.domain.admin.dto.user;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.Data;

@Data
public class AdminUserResponse {
    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private String gender;
    private String birthdate;
    private String company;
    private String plan;
    private String regDate;

    public AdminUserResponse(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.gender = user.getGender().getValue();
        this.birthdate = user.getBirthdate();
        this.company = user.getCompany();
        this.plan = user.getPlanSubscribe().getValue();
        this.regDate = String.valueOf(user.getRegDate());
    }
}

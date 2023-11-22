package com.bipa.bizsurvey.domain.admin.application;

import com.bipa.bizsurvey.domain.admin.dto.user.UserSearchRequest;
import com.bipa.bizsurvey.domain.admin.dto.user.AdminUserResponse;
import com.bipa.bizsurvey.domain.user.domain.QUser;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserService {
    private final JPAQueryFactory jpaQueryFactory;
    public QUser qUser = new QUser("qUser");

    // 회원 목록
    public Page<?> getUserList(Pageable pageable, UserSearchRequest userSearchRequest) {

        BooleanBuilder conditions = buildConditions(userSearchRequest);


        List<User> users = jpaQueryFactory
                .select(qUser)
                .from(qUser)
                .where(qUser.delFlag.eq(false).and(conditions))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qUser.regDate.desc())
                .fetch();

        List<AdminUserResponse> result = users.stream()
                .map(AdminUserResponse::new)
                .collect(toList());
        return new PageImpl<>(result, pageable, result.size());
    }

    private BooleanExpression emailEq(String email) {
        return isEmpty(email) ? null : qUser.email.like("%" + email + "%");
    }

    private BooleanExpression nameEq(String name) {
        return isEmpty(name) ? null : qUser.name.like("%" + name + "%");
    }

    private BooleanExpression nicknameEq(String nickname) {
        return isEmpty(nickname) ? null : qUser.nickname.like("%" + nickname + "%");
    }

    private BooleanBuilder buildConditions(UserSearchRequest userSearchRequest) {
        BooleanBuilder conditions = new BooleanBuilder();

        if (userSearchRequest != null) {
            if (userSearchRequest.getEmail() != null) {
                conditions.and(emailEq(userSearchRequest.getEmail()));
            }
            if (userSearchRequest.getName() != null) {
                conditions.and(nameEq(userSearchRequest.getName()));
            }
            if (userSearchRequest.getNickname() != null) {
                conditions.and(nicknameEq(userSearchRequest.getNickname()));
            }
        }

        return conditions;
    }
}

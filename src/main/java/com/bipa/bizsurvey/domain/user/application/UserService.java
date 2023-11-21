package com.bipa.bizsurvey.domain.user.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bipa.bizsurvey.domain.community.domain.Post;
import com.bipa.bizsurvey.domain.community.domain.QPost;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostResponse;
import com.bipa.bizsurvey.domain.community.enums.PostType;
import com.bipa.bizsurvey.domain.community.repository.PostRepository;
import com.bipa.bizsurvey.domain.community.service.CommentService;
import com.bipa.bizsurvey.domain.user.domain.Claim;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserClaimResponse;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoResponse;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserInfoUpdateRequest;
import com.bipa.bizsurvey.domain.user.dto.mypage.UserPlanResponse;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.domain.user.repository.ClaimRepository;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.dto.*;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.global.common.RedisService;
import com.bipa.bizsurvey.global.config.jwt.JwtVO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ClaimRepository claimRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JPAQueryFactory jpaQueryFactory;
    private final CommentService commentService;
    public QPost p = new QPost("p");
    private final RedisService redisService;
    
    public void join(JoinRequest joinDto){
        User user = userRepository.save(joinDto.toEntity(passwordEncoder));
    }

    // 닉네임 중복 확인
    public void nickNameCheck(String nickname){
        Optional<User> nicknameUser = userRepository.findByNickname(nickname);
        if(nicknameUser.isPresent()){
            throw new UserException(UserExceptionType.ALREADY_EXIST_NICKNAME);
        }
    }

    //내정보 조회
    public UserInfoResponse userInfo(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserException(UserExceptionType.NON_EXIST_USER)
        );
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .planSubscribe(user.getPlanSubscribe())
                .name(user.getName())
                .gender(user.getGender())
                .birthdate(user.getBirthdate())
                .build();
    }

    //내정보 수정
    public String userInfoUpdate(LoginUser loginUser, UserInfoUpdateRequest request){
        User user = userRepository.findById(request.getId()).orElseThrow(
                () -> new UserException(UserExceptionType.NON_EXIST_USER)
        );
        user.userInfoUpdate(request);
        userRepository.save(user);

        return JWT.create()
                .withSubject("bank")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                .withClaim("id", loginUser.getLoginInfoRequest().getId())
                .withClaim("nickname", request.getNickname())
                .withClaim("role", loginUser.getPlan())
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
    }

    //플랜 조회
    public UserPlanResponse userPlan(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserException(UserExceptionType.NON_EXIST_USER)
        );
        return UserPlanResponse.builder()
                .planSubscribe(user.getPlanSubscribe())
                .build();
    }

    //플랜 가입
    public String planUpdate(LoginUser loginUser, Plan plan){
        User user = userRepository.findById(loginUser.getId()).orElseThrow(
                () -> new UserException(UserExceptionType.NON_EXIST_USER)
        );
        user.userPlanUpdate(plan);
        userRepository.save(user);

        return JWT.create()
                .withSubject("bank")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                .withClaim("id", loginUser.getLoginInfoRequest().getId())
                .withClaim("nickname", loginUser.getLoginInfoRequest().getNickname())
                .withClaim("email", loginUser.getLoginInfoRequest().getEmail())
                .withClaim("role", plan + "")
                .sign(Algorithm.HMAC512(JwtVO.SECRET));
    }

    //refresh Token 검증 및 Access Token 재발급
    public String accessTokenRefresh(String refreshToken){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(refreshToken.replace(JwtVO.TOKEN_PREFIX, ""));
        Long key = decodedJWT.getClaim("id").asLong();

        String value = redisService.getData(String.valueOf(key));
        String redisToken = value.replace("\"", "");
        String userToken = refreshToken.replace(JwtVO.TOKEN_PREFIX, "");

        if(userToken.equals(redisToken)){
            User user = userRepository.findById(Long.valueOf(key)).orElseThrow(
                    () -> new UserException(UserExceptionType.NON_EXIST_USER)
            );
            return JWT.create()
                    .withSubject("bank")
                    .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                    .withClaim("id", user.getId())
                    .withClaim("nickname", user.getNickname())
                    .withClaim("email", user.getEmail())
                    .withClaim("role", String.valueOf(user.getPlanSubscribe()))
                    .sign(Algorithm.HMAC512(JwtVO.SECRET));
        }else {
            throw new UserException(UserExceptionType.JWT_VERIFICATION);
        }
    }

    public void passwordUpdate(PasswordUpdateRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new UserException(UserExceptionType.NON_EXIST_EMAIL)
        );
        user.passowordUpdate(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    //신고한 내역
    public List<UserClaimResponse> userClaim(Long userId){

        List<Claim> claims = claimRepository.findByUserId(userId);

        return claims.stream()
                .map(UserClaimResponse::new)
                .collect(toList());
    }

    //커뮤니티 조회
    public Page<?> getPostList(Pageable pageable, Long id){
        List<Post> postList = jpaQueryFactory
                .select(p)
                .from(p)
                .where(p.delFlag.eq(false))
                .where(p.reported.eq(false))
                .where(p.postType.eq(PostType.COMMUNITY))
                .where(p.user.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostResponse> result = new ArrayList<>();

        for(Post post: postList){
            PostResponse postResponse = PostResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .count(post.getCount())
                    .nickname(post.getUser().getNickname())
                    .createTime(post.getRegDate().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm")))
                    .build();
            result.add(postResponse);
        }

        return new PageImpl<>(result, pageable, result.size());
    }








}
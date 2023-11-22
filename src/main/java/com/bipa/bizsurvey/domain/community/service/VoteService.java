package com.bipa.bizsurvey.domain.community.service;

import com.bipa.bizsurvey.domain.community.domain.*;
import com.bipa.bizsurvey.domain.community.dto.request.vote.CreateVoteAnswerRequest;
import com.bipa.bizsurvey.domain.community.dto.request.vote.CreateVoteRequest;
import com.bipa.bizsurvey.domain.community.dto.response.vote.AnswerPercentageResponse;
import com.bipa.bizsurvey.domain.community.dto.response.vote.VoteAnswerResponse;
import com.bipa.bizsurvey.domain.community.exception.voteException.VoteException;
import com.bipa.bizsurvey.domain.community.exception.voteException.VoteExceptionType;
import com.bipa.bizsurvey.domain.community.repository.VoteAnswerRepository;
import com.bipa.bizsurvey.domain.community.repository.VoteRepository;
import com.bipa.bizsurvey.domain.community.repository.VoteUserAnswerRepository;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.exception.UserException;
import com.bipa.bizsurvey.domain.user.exception.UserExceptionType;
import com.bipa.bizsurvey.domain.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {

        private final UserRepository userRepository;
        private final VoteRepository voteRepository;
        private final VoteAnswerRepository voteAnswerRepository;
        private final VoteUserAnswerRepository voteUserAnswerRepository;
        private final JPAQueryFactory jpaQueryFactory;
        private final PostService postService;
        public final QVote v = QVote.vote;
        public final QVoteAnswer va = QVoteAnswer.voteAnswer;
        private final QVoteUserAnswer userAnswer = QVoteUserAnswer.voteUserAnswer;

        public void createVote(Long userId, CreateVoteRequest createVoteRequest, Long postId){
                Post post = postService.findPost(postId);
                checkPermission(userId, post);

                // 최대 5개 까지
                if(createVoteRequest.getVoteAnswer().size() > 4)
                        throw new VoteException(VoteExceptionType.MAX_COUNT);



                Vote vote = Vote.builder()
                        .voteQuestion(createVoteRequest.getVoteQuestion())
                        .build();
                Vote save = voteRepository.save(vote);
                saveAllVoteAnswers(save, createVoteRequest.getVoteAnswer());
                post.setVoteId(save.getId());
        }

        // show vote answer
        public List<VoteAnswerResponse> showVoteAnswerList(Long postId, Long voteId){
                Vote vote = findVote(postId, voteId);
                List<VoteAnswer> voteAnswerList = jpaQueryFactory
                        .select(va)
                        .from(va)
                        .where(va.vote.eq(vote))
                        .where(va.delFlag.eq(false))
                        .fetch();

                List<VoteAnswerResponse> list = new ArrayList<>();
                for(VoteAnswer voteAnswer : voteAnswerList){
                        VoteAnswerResponse voteAnswerResponse = VoteAnswerResponse.builder()
                                .voteAnswerId(voteAnswer.getId())
                                .answer(voteAnswer.getAnswer())
                                .build();
                        list.add(voteAnswerResponse);
                }
                return list;
        }

        public void choseAnswer(Long userId, Long postId, Long voteId, Long voteAnswerId){
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));

                Vote vote = findVote(postId, voteId);

                checkExistOption(voteId, voteAnswerId); // 존재하는 선택란인지 확인
                checkAlreadyChose(user, vote); // 이미 투표한 유저인지 체크

                VoteAnswer voteAnswer = voteAnswerRepository.findById(voteAnswerId)
                        .orElseThrow(() -> new VoteException(VoteExceptionType.NON_EXIST_ANSWER));

                VoteUserAnswer voteUserAnswer = VoteUserAnswer.builder()
                        .user(user)
                        .vote(vote)
                        .answer(voteAnswer.getAnswer())
                        .build();

                voteUserAnswerRepository.save(voteUserAnswer);
        }


        // 모든 응답에 대한 퍼센테이지 구하는 메소드 제작
        public List<AnswerPercentageResponse> calculatePercentage(Long voteId) {

                List<VoteUserAnswer> voteUserAnswers = jpaQueryFactory
                        .select(userAnswer)
                        .from(userAnswer)
                        .where(userAnswer.vote.id.eq(voteId))
                        .fetch();

                List<VoteAnswer> voteAnswerList = jpaQueryFactory
                        .select(va)
                        .from(va)
                        .where(va.vote.eq(v))
                        .where(va.delFlag.eq(false))
                        .fetch();

                return calculate(voteUserAnswers.size(), voteAnswerList);
        }


        private void saveAllVoteAnswers(Vote vote, List<CreateVoteAnswerRequest> list){
                List<VoteAnswer> voteAnswerList = new ArrayList<>();

                for(CreateVoteAnswerRequest createVoteAnswerRequest : list){
                        VoteAnswer voteAnswer = VoteAnswer.builder()
                                .vote(vote)
                                .answer(createVoteAnswerRequest.getAnswer())
                                .build();
                        voteAnswerList.add(voteAnswer);
                }
                voteAnswerRepository.saveAll(voteAnswerList);
        }




        private Vote findVote(Long postId, Long voteId){
                Post post = postService.findPost(postId);
                Vote vote = null;
                if(!post.getDelFlag()) {
                        vote = voteRepository.findById(voteId)
                                .orElseThrow(() -> new VoteException(VoteExceptionType.NON_EXIST_VOTE));
                }
                return vote;
        }

        private void checkAlreadyChose(User user, Vote vote){
                if(voteUserAnswerRepository.existsByUserIdAndVoteId(user.getId(), vote.getId())){
                        throw new VoteException(VoteExceptionType.ALREADY_CHECK);
                }
        }


        private List<AnswerPercentageResponse> calculate(double totalCount, List<VoteAnswer> list){
                List<AnswerPercentageResponse> answerPercentageResponses = new ArrayList<>();
                for (VoteAnswer voteAnswer : list) {
                        List<VoteUserAnswer> selected = jpaQueryFactory
                                .select(userAnswer)
                                .from(userAnswer)
                                .where(userAnswer.answer.eq(voteAnswer.getAnswer()))
                                .fetch();

                        AnswerPercentageResponse response = AnswerPercentageResponse.builder()
                                .voteAnswerId(voteAnswer.getId())
                                .answer(voteAnswer.getAnswer())
                                .percentage((selected.size()/totalCount) * 100)
                                .build();
                        answerPercentageResponses.add(response);
                }
                return answerPercentageResponses;
        }

        // TODO : 자신이 생성한 게시물에서만 투표를 생성할 수 있음 => 막아주는 메소드 필요
        public void checkPermission(Long userId, Post post) {
                if(!Objects.equals(userId, post.getUser().getId())){
                        throw new UserException(UserExceptionType.NO_PERMISSION);
                }
        }

        // TODO : "존재하지 않는 선택지입니다" 만들어야 함
        private void checkExistOption(Long voteId, Long choseId){
                List<VoteAnswer> voteAnswerList = voteAnswerRepository.findAllByVoteId(voteId);
                if(voteAnswerList.size() < choseId){
                        throw new VoteException(VoteExceptionType.NON_EXIST_ANSWER);
                }
        }

}

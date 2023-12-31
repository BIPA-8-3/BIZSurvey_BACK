package com.bipa.bizsurvey.domain.community.application;

import com.bipa.bizsurvey.domain.community.domain.*;
import com.bipa.bizsurvey.domain.community.dto.request.vote.CreateVoteAnswerRequest;
import com.bipa.bizsurvey.domain.community.dto.request.vote.CreateVoteRequest;
import com.bipa.bizsurvey.domain.community.dto.response.vote.AnswerPercentageResponse;
import com.bipa.bizsurvey.domain.community.dto.response.vote.VoteAnswerResponse;
import com.bipa.bizsurvey.domain.community.dto.response.vote.VoteResponse;
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
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {
        //

        private final UserRepository userRepository;
        private final VoteRepository voteRepository;
        private final VoteAnswerRepository voteAnswerRepository;
        private final VoteUserAnswerRepository voteUserAnswerRepository;
        private final JPAQueryFactory jpaQueryFactory;
        private final PostService postService;
        public final QVote v = QVote.vote;
        public final QVoteAnswer va = QVoteAnswer.voteAnswer;
        private final QVoteUserAnswer userAnswer = QVoteUserAnswer.voteUserAnswer;

        public Long createVote(Long userId, CreateVoteRequest createVoteRequest){
                User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));
                Vote vote = Vote.builder()
                        .voteQuestion(createVoteRequest.getVoteQuestion())
                        .user(user)
                        .build();
                Vote save = voteRepository.save(vote);
                saveAllVoteAnswers(save, createVoteRequest.getVoteAnswer());

                return save.getId();
        }

        // show vote answer
        public VoteResponse showVoteAnswerList(Long postId, Long voteId){
                Vote vote = findVote(postId, voteId);

                if(vote == null){
                        return null;
                }

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

                return VoteResponse.builder()
                        .voteTitle(vote.getVoteQuestion())
                        .answerList(list)
                        .build();
        }

        public void choseAnswer(Long userId, Long postId, Long voteId, Long voteAnswerId){
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserException(UserExceptionType.NON_EXIST_USER));

                Vote vote = findVote(postId, voteId);



                VoteAnswer voteAnswer = voteAnswerRepository.findById(voteAnswerId)
                        .orElseThrow(() -> new VoteException(VoteExceptionType.NON_EXIST_ANSWER));

                VoteUserAnswer voteUserAnswer = VoteUserAnswer.builder()
                        .user(user)
                        .vote(vote)
                        .answer(voteAnswer.getAnswer())
                        .build();

                voteUserAnswerRepository.save(voteUserAnswer);
        }

        // deleteVote
        public void deleteVote(Long userId, Long voteId){
                Vote vote = voteRepository.findById(voteId).get();
                if(!vote.getUser().getId().equals(userId)){
                    throw new UserException(UserExceptionType.NO_PERMISSION);
                }else {
                        vote.setDelFlag(true);
                }
        }





        // 모든 응답에 대한 퍼센테이지 구하는 메소드 제작
        public List<AnswerPercentageResponse> calculatePercentage(Long voteId) {

                List<AnswerPercentageResponse> results = new ArrayList<>();

                List<VoteAnswer> voteAnswers = jpaQueryFactory
                        .select(va)
                        .from(va)
                        .where(va.vote.id.eq(voteId))
                        .fetch();

                for (VoteAnswer voteAnswer : voteAnswers) {
                       long count = jpaQueryFactory
                               .select(userAnswer)
                               .from(userAnswer)
                               .where(userAnswer.vote.id.eq(voteId))
                               .where(userAnswer.answer.eq(voteAnswer.getAnswer()))
                               .stream().count();
                       AnswerPercentageResponse answerPercentageResponse = AnswerPercentageResponse.builder()
                               .voteAnswerId(voteAnswer.getId())
                               .name(voteAnswer.getAnswer())
                               .value(count)
                               .build();
                       results.add(answerPercentageResponse);
                }
                return results;
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
                        vote = voteRepository.findByIdAndDelFlagIsFalse(voteId);
                }
                return vote;
        }

        public String checkAlreadyChose(Long userId, Long voteId){
                if(voteUserAnswerRepository.existsByUserIdAndVoteId(userId, voteId)){
                        return "cheked"; //
                }else {
                        return "non_checked"; //
                }
        }




        // TODO : 자신이 생성한 게시물에서만 투표를 생성할 수 있음 => 막아주는 메소드 필요
        public void checkPermission(Long userId, Post post) {
                if(!Objects.equals(userId, post.getUser().getId())){
                        throw new UserException(UserExceptionType.NO_PERMISSION);
                }
        }

        // TODO : "존재하지 않는 선택지입니다" 만들어야 함


}

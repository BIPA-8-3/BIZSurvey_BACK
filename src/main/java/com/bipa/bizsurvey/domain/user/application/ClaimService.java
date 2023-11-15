package com.bipa.bizsurvey.domain.user.application;

import com.bipa.bizsurvey.domain.user.dto.mypage.ClaimResponse;
import com.bipa.bizsurvey.domain.user.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimService {

    private final ClaimRepository claimRepository;

    //신고 내역 조회
    public List<ClaimResponse> claimList(Long id){
        return claimRepository.findAllByWithUser(id);
    }
}

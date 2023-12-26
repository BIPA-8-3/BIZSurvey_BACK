package com.bipa.bizsurvey.domain.workspace.application;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class SseService {
//
//    // 메시지 알림
//    public SseEmitter subscribe(Long userId) {
//
//        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
//        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
//
//        // 2. 연결
//        try {
//            sseEmitter.send(SseEmitter.event().name("connect"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 3. 저장
//        NotificationController.sseEmitters.put(userId, sseEmitter);
//
//        // 4. 연결 종료 처리
//        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(userId));	// sseEmitter 연결이 완료될 경우
//        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(userId));		// sseEmitter 연결에 타임아웃이 발생할 경우
//        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(userId));		// sseEmitter 연결에 오류가 발생할 경우
//
//        return sseEmitter;
//    }

}

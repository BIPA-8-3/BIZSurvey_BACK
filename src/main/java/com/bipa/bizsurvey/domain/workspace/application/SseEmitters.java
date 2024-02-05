//package com.bipa.bizsurvey.domain.workspace.application;
//
//import com.bipa.bizsurvey.domain.workspace.dto.EventDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//@Service
//@RequiredArgsConstructor
//@Log4j2
//@Transactional
//public class SseEmitters {
//    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap();
//
//    public SseEmitter add(Long workspaceId) {
//        SseEmitter emitter = new SseEmitter(60 * 5000L);
//        log.info("왜 안되냐고");
//
//        List<SseEmitter> emitterList = getEmitterList(workspaceId);
//        log.info("0-workspaceId: {}", workspaceId);
//        emitters.put(workspaceId, emitterList);
//
//        log.info("================================================");
//        log.info("1-workspaceId: {}", workspaceId);
//        log.info("2-emitter: {}", emitter);
//        log.info("3-emitterList.size(): {}", emitterList.size());
//        log.info("4-emitters.size(): {}", emitters.size());
//        log.info("================================================");
//        emitter.onCompletion(() -> {
//            log.info("onCompletion callback");
//            handleCompletion(workspaceId, emitter);
//        });
//
//        emitter.onTimeout(() -> {
//            log.info("onTimeout callback");
//        });
//
//        emitter.onError((error) -> {
//            log.info("onError callback: ", error);
//            handleCompletion(workspaceId, emitter);
//        });
//        emitterList.add(emitter);
//        log.info("5-emitterList.size(): {}", emitterList.size());
//
//        return emitter;
//    }
//
//    @Async
//    public void handleCompletion(Long workspaceId, SseEmitter emitter) {
//        log.info("onCompletion callback");
//        removeEmitter(workspaceId, emitter);
//    }
//    public void sendEvent(EventDto event) {
//        log.info(11111);
//        List<SseEmitter> list = getEmitterList(event.getWorkspaceId());
//        String name = event.getName();
//        String errorMessage = event.getErrorMessage();
//        Object response = event.getResponse();
//        log.info(11111 - 1);
//        list.forEach(emiiter -> {
//            try {
//                log.info(11111 - 2);
//                emiiter.send(SseEmitter.event()
//                        .name(name)
//                        .data(response));
//                log.info(11111 - 3);
//            } catch (Exception e) {
//                log.info("오류남오류남");
//                log.info(e.getMessage());
//                log.error(e.getMessage());
//                list.remove(emiiter);
////                throw new RuntimeException(errorMessage);
//            }
//        });
//    }
//
//    // 삭제 메소드
//    private void removeEmitter(Long workspaceId, SseEmitter emitter) {
//        List<SseEmitter> list = getEmitterList(workspaceId);
//        list.remove(emitter);
//
//        if (list.size() == 0) {
//            emitters.remove(workspaceId);
//        }
//
//        log.info("emitter removed: {}", emitter);
//        log.info("emitter list size: {}", list.size());
//    }
//
//    private List<SseEmitter> getEmitterList(Long workspaceId) {
//        List<SseEmitter> list = emitters.get(workspaceId);
//
//        if (list == null) {
//            list = new CopyOnWriteArrayList<>();
//        }
//
//        return list;
//    }
//}


package com.bipa.bizsurvey.domain.workspace.application;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.domain.workspace.dto.EventDto;
import com.bipa.bizsurvey.domain.workspace.dto.SseEmitterDto;
import com.bipa.bizsurvey.domain.workspace.dto.WorkspaceAdminDto;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceAdminRepository;
import com.bipa.bizsurvey.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class SseEmitters {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap();
    private final WorkspaceAdminRepository workspaceAdminRepository;
    private final WorkspaceRepository workspaceRepository;

    public SseEmitter add(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 5000L);
        emitters.put(userId, emitter);
        log.info("1-new emitter added: {}", userId);
        log.info("2-new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            removeEmitter(userId, emitter); // 만료되면 리스트에서 삭제
        });

        // 타임아웃 발생 시 브라우저에서 재 연결 요청을 보냄,
        // 이때 새로운 Emitter 객체를 다시 생성함.
        // 기존에 Emiiter를 제거 하여야함
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
//            emitter.complete();
        });

        return emitter;
    }
//
//    // 관리자 초대 이벤트 전송
//    public void sendEvent(EventDto event) {
//        List<SseEmitter> list = getEmitterList(event.getWorkspaceId());
//        String name = event.getName();
//        String errorMessage = event.getErrorMessage();
////        Object response = event.getResponse();
//        list.forEach(emiiter -> {
//            try {
//                emiiter.send(SseEmitter.event()
//                        .name(name)
//                        .data(event));
//            } catch (Exception e) {
//                log.error(e.getMessage());
//                log.error(errorMessage);
//                list.remove(emiiter);
//                removeEmitter
//            // throw new RuntimeException(errorMessage);
//            }
//        });
//    }


    // 관리자 초대 이벤트 전송
    public void sendEvent(EventDto event) {
        Map<Long, SseEmitter> map = getEmitterList(event.getWorkspaceId());
        String name = event.getName();
        String errorMessage = event.getErrorMessage();
//        Object response = event.getResponse();

        emitters.entrySet().stream().forEach(a -> System.out.println(a.getKey()));

        if(event.isDelFlag() && event.getId() != null) {
            SseEmitter delEmitter = emitters.get(event.getId());
            if(delEmitter != null) {
                map.put(event.getId(), delEmitter);
            }
        }

        map.entrySet().stream().forEach(entry -> {
            SseEmitter emitter = entry.getValue();
            try {
                emitter.send(SseEmitter.event()
                        .name(name)
                        .data(event));
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error(errorMessage);
                removeEmitter(entry.getKey(), emitter);
            }
        });
    }

    // 삭제 메소드
    private void removeEmitter(Long userId, SseEmitter emitter) {
        emitters.remove(userId, emitter);
        log.info("emitter removed: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
    }


    //     특정 워크스페이스에 속한 관리자들의 SseEmiiter 가져옴
    private Map<Long, SseEmitter> getEmitterList(Long workspaceId) {
        List<Long> userList = workspaceAdminRepository.findUserByWorkspaceId(workspaceId);
        userList.stream().forEach(log::info);
        Optional<Workspace> workspace = workspaceRepository.findWorkspaceByIdAndDelFlagFalse(workspaceId);
        Long owner = workspace.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스 입니다.")).getUser().getId();
        userList.add(owner);

//        List<SseEmitter> sseEmiiterList = this.emitters.entrySet().stream().filter(entry -> userList.contains(entry.getKey()))
//                .map(entry -> entry.getValue())
//                .collect(Collectors.toList());
//
//        if (sseEmiiterList == null) {
//            sseEmiiterList = new CopyOnWriteArrayList<>();
//        }

        Map<Long, SseEmitter> result = new ConcurrentHashMap();

        result.putAll(this.emitters.entrySet().stream().filter(entry -> userList.contains(entry.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));

        log.info(result.size());
        return result;
    }

////     특정 워크스페이스에 속한 관리자들의 SseEmiiter 가져옴
//    private List<SseEmitter> getEmitterList(Long workspaceId) {
//        List<Long> userList = workspaceAdminRepository.findUserByWorkspaceId(workspaceId);
//        userList.stream().forEach(log::info);
//        Optional<Workspace> workspace = workspaceRepository.findWorkspaceByIdAndDelFlagFalse(workspaceId);
//        Long owner = workspace.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 워크스페이스 입니다.")).getUser().getId();
//        userList.add(owner);
//
////        List<SseEmitter> sseEmiiterList = this.emitters.entrySet().stream().filter(entry -> userList.contains(entry.getKey()))
////                .map(entry -> entry.getValue())
////                .collect(Collectors.toList());
////
////        if (sseEmiiterList == null) {
////            sseEmiiterList = new CopyOnWriteArrayList<>();
////        }
//
//        Map<Long, SseEmitter> result = new ConcurrentHashMap();
//
//        result.putAll(this.emitters.entrySet().stream().filter(entry -> userList.contains(entry.getKey()))
//                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));
//
//        log.info(result.size());
//        return result;
//    }
}

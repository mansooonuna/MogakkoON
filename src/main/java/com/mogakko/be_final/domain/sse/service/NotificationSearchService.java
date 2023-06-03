package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSearchService {
    private final NotificationRepository notificationRepository;
    public ResponseEntity<Message> getMyNotification(UserDetailsImpl userDetails){
        Members receiver = userDetails.getMember();
        List<Notification> notificationList = notificationRepository.findAllByReceiver(receiver);

        if(!notificationList.isEmpty()){
            return new ResponseEntity<>(new Message("알림 조회 완료", notificationList), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new Message("알림이 없습니다.", null), HttpStatus.OK);
        }


    }
}
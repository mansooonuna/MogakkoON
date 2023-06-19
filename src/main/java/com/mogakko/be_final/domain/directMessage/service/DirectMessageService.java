package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.dto.response.DirectMessageSearchResponseDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.util.BadWordFiltering;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DirectMessageService {
    private final BadWordFiltering badWordFiltering;
    private final NotificationSendService notificationSendService;
    private final MembersRepository membersRepository;
    private final DirectMessageRepository directMessageRepository;

    // 쪽지 전송
    @Transactional
    public ResponseEntity<Message> sendDirectMessage(Members member, DirectMessageSendRequestDto directMessageSendRequestDto) {
        Members messageReceiver = membersRepository.findByNickname(directMessageSendRequestDto.getMessageReceiverNickname()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        String messageContent = badWordFiltering.checkBadWord(directMessageSendRequestDto.getContent());

        if (messageReceiver.getNickname().equals(member.getNickname())) {
            return new ResponseEntity<>(new Message("자신에게는 쪽지를 보낼 수 없습니다.", null), HttpStatus.BAD_REQUEST);
        }

        if (messageContent.isEmpty()) {
            return new ResponseEntity<>(new Message("내용을 입력해 주세요.", null), HttpStatus.BAD_REQUEST);
        }

        DirectMessage directMessage = new DirectMessage(member, messageReceiver, messageContent, false);
        directMessageRepository.save(directMessage);
        notificationSendService.sendMessageReceivedNotification(member, messageReceiver);

        return new ResponseEntity<>(new Message("쪽지 전송 성공", null), HttpStatus.OK);
    }

//    @Transactional
//    public ResponseEntity<Message> deleteDirectMessage(DirectMessageDeleteRequestDto requestDto, Members member) {
//        List<Long> dmList = requestDto.getDirectMessageList();
//
//        for (Long dm : dmList) {
//            DirectMessage directMessage = findDirectMessageById(dm);
//            directMessageRepository.delete(directMessage);
//        }
//        return new ResponseEntity<>(new Message("쪽지 삭제 성공", null), HttpStatus.OK);
//    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> searchReceivedMessage(Members member) {
        List<DirectMessageSearchResponseDto> messageList = new ArrayList<>();

        List<DirectMessage> list = directMessageRepository.findAllByReceiver(member);

        for (DirectMessage directMessage : list) {
            DirectMessageSearchResponseDto message = new DirectMessageSearchResponseDto(directMessage);
            messageList.add(message);
        }


        if (messageList.isEmpty()) {
            return new ResponseEntity<>(new Message("도착한 쪽지가 없습니다.", null), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new Message("쪽지 목록 조회 완료", messageList), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> searchSentMessage(Members member) {
        List<DirectMessageSearchResponseDto> messageList = new ArrayList<>();

        List<DirectMessage> list = directMessageRepository.findAllBySender(member);

        for (DirectMessage directMessage : list) {
            DirectMessageSearchResponseDto message = new DirectMessageSearchResponseDto(directMessage);
            messageList.add(message);
        }

        if (messageList.isEmpty()) {
            return new ResponseEntity<>(new Message("보낸 쪽지가 없습니다.", null), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("쪽지 목록 조회 완료", messageList), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> readDirectMessage(Members member, Long messageId) {
        DirectMessage findMessage = findDirectMessageById(messageId);
        if (member == findMessage.getReceiver()) {
            findMessage.markRead();
            return new ResponseEntity<>(new Message("쪽지 조회 완료", findMessage), HttpStatus.OK);
        } else {
            throw new CustomException(ErrorCode.INTERNAL_SERER_ERROR);
        }
    }

    /**
     * Method
     */

    private DirectMessage findDirectMessageById(Long id) {
        return directMessageRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND)
        );
    }
}
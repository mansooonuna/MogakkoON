package com.mogakko.be_final.domain.sse.dto;

import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Schema(description = "알림 Dto")
@Getter
@NoArgsConstructor
public class NotificationResponseDto {
    private Long receiverId;
    private String content;
    private String url;
    private String receiverNickname;
    private String senderNickname;
    private NotificationType notificationType;
    private String createdAt;
    private boolean readStatus;
    private String senderProfileUrl;



    public NotificationResponseDto(Notification notification, String senderProfileUrl) {
        LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = time.format(formatter);
        this.receiverId = notification.getReceiverId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
        this.receiverNickname = notification.getReceiverNickname();
        this.senderNickname = notification.getSenderNickname();
        this.notificationType = notification.getType();
        this.createdAt = formattedNow;
        this.readStatus = notification.isReadStatus();
        this.senderProfileUrl = senderProfileUrl;
    }

    public NotificationResponseDto(Notification notification) {
        LocalDateTime time = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = time.format(formatter);
        this.receiverId = notification.getReceiverId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
        this.receiverNickname = notification.getReceiverNickname();
        this.senderNickname = notification.getSenderNickname();
        this.notificationType = notification.getType();
        this.createdAt = formattedNow;
        this.readStatus = notification.isReadStatus();
    }

}

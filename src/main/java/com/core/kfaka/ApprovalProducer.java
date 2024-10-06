package com.core.kfaka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApprovalProducer {
    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public void sendApprovalNotification (Long employeeId, String message, Long approvalId ) {
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message(message)
                .employeeId(employeeId)
                .typeId(approvalId)
                .type(NotificationMessage.NotificationType.APPROVAL)
                .build();
        kafkaTemplate.send("approval-topic",notificationMessage);
    }

    public void sendDocumentNotification (Long employeeId, String message, Long documentId ) {
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message(message)
                .employeeId(employeeId)
                .typeId(documentId)
                .type(NotificationMessage.NotificationType.DOCUMENT)
                .build();
        kafkaTemplate.send("document-topic",notificationMessage);
    }
}

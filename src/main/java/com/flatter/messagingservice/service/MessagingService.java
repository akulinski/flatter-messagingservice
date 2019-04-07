package com.flatter.messagingservice.service;

import com.flatter.messagingservice.domain.Conversation;
import com.flatter.messagingservice.domain.Message;
import com.flatter.messagingservice.domain.Participant;
import com.flatter.messagingservice.repository.ConversationRepository;
import com.flatter.messagingservice.repository.MessageRepository;
import com.flatter.messagingservice.repository.ParticipantRepository;
import com.flatter.messagingservice.service.kafka.MessageConsumerChannel;
import domain.MessageDTO;
import domain.events.MessageSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@EnableBinding(MessageConsumerChannel.class)
public class MessagingService {
    private final Logger log = LoggerFactory.getLogger(MessagingService.class);

    private final ParticipantRepository participantRepository;

    private final ConversationRepository conversationRepository;

    private final MessageRepository messageRepository;

    public MessagingService(ParticipantRepository participantRepository, ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.participantRepository = participantRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }


    @StreamListener(target = MessageConsumerChannel.CHANNEL)
    public void handleNewMessage(MessageSentEvent messageSentEvent) {
        log.debug("Got message {}", messageSentEvent.toString());

        MessageDTO messageDTO = messageSentEvent.getMessageDTO();

        Message message = setUpMessageFromDTO(messageDTO);

        Optional<Participant> senderOptional = participantRepository.getByLogin(messageDTO.getSender());
        Optional<Participant> receiverOptional = participantRepository.getByLogin(messageDTO.getReceiver());

        Participant sender;
        Participant receiver;

        if (senderOptional.isPresent()) {
            sender = senderOptional.get();
        } else {
            sender = new Participant();
            sender.setLogin(messageDTO.getSender());
            participantRepository.save(sender);
        }

        if (receiverOptional.isPresent()) {
            receiver = receiverOptional.get();
        } else {
            receiver = new Participant();
            receiver.setLogin(messageDTO.getReceiver());
            participantRepository.save(receiver);
        }

        Optional<Conversation> conversationOptional = conversationRepository.findByParticipantsContainingSenderAndReceiver(messageDTO.getSender(), messageDTO.getReceiver());

        if (conversationOptional.isPresent()) {
            sender.setConversation(conversationOptional.get());
            receiver.setConversation(conversationOptional.get());
            message.setConversation(conversationOptional.get());
        } else {
            Conversation conversation = new Conversation();
            conversationRepository.save(conversation);
            sender.setConversation(conversation);
            receiver.setConversation(conversation);
            message.setConversation(conversation);
        }

        messageRepository.save(message);
        participantRepository.save(receiver);
        participantRepository.save(sender);
    }

    private Message setUpMessageFromDTO(MessageDTO messageDTO) {
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setIsSeen(false);
        message.setSender(messageDTO.getSender());
        message.setReceiver(messageDTO.getReceiver());
        return message;
    }
}

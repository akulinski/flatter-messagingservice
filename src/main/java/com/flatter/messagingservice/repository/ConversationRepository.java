package com.flatter.messagingservice.repository;

import com.flatter.messagingservice.domain.Conversation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Conversation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query(
        value = "select *" +
            "from conversation c " +
            " WHERE c.id =" +
            "      (select p1.conversation_id" +
            "       from participant p1" +
            "       where p1.login = ?1" +
            "         AND p1.conversation_id =" +
            "             (select p2.conversation_id from participant p2 where p2.login = ?2))",
        nativeQuery = true
    )
    Optional<Conversation> findByParticipantsContainingSenderAndReceiver(String sender,String receiver);
}

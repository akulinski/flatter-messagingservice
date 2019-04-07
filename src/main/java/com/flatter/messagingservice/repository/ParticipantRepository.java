package com.flatter.messagingservice.repository;

import com.flatter.messagingservice.domain.Participant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Participant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> getByLogin(String login);
}

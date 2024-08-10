package com.example.demo.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.profile.Profile;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findBySenderAndRecipientOrderByTimestampDesc(Profile sender, Profile recipient);

  List<Message> findByRecipientOrderByTimestampDesc(Profile recipient);

  @Query("SELECT m FROM Message m WHERE (m.sender.id = :user1Id AND m.recipient.id = :user2Id) OR (m.sender.id = :user2Id AND m.recipient.id = :user1Id) ORDER BY m.timestamp")
  List<Message> findConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

  @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.recipient.id = :userId ORDER BY m.timestamp")
  List<Message> findAllByUserIdInvolved(@Param("userId") Long userId);

}
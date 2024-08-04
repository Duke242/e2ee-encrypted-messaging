package com.example.demo.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.profile.Profile;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findBySenderAndRecipientOrderByTimestampDesc(Profile sender, Profile recipient);

  List<Message> findByRecipientOrderByTimestampDesc(Profile recipient);
}
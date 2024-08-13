package com.example.demo.profile;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  @Query("SELECT p FROM Profile p WHERE p.email = ?1")
  Optional<Profile> findProfileByEmail(String email);

  @Query("SELECT p.id FROM Profile p WHERE p.email = ?1")
  Optional<Long> findIdByEmail(String email);

  @Query("SELECT p.email FROM Profile p")
  Optional<List<String>> findAllEmails();

}

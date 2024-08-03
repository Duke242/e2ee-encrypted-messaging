package com.example.demo.profile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  @Query("SELECT p FROM Profile p where p.email = ?1")
  Optional<Profile> findProfileByEmail(String email);
}

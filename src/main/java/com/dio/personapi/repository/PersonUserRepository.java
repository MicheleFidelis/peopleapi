package com.dio.personapi.repository;

import com.dio.personapi.entities.PersonUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonUserRepository extends JpaRepository<PersonUser, Long> {
   PersonUser findByUsername(String username);
}

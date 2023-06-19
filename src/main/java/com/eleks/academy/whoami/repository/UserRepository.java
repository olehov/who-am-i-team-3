package com.eleks.academy.whoami.repository;

import com.eleks.academy.whoami.dmo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

}

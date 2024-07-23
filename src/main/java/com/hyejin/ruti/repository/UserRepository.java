package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByUserEmail(String userEmail);

}

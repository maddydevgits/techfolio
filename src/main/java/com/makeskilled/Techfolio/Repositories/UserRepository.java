package com.makeskilled.Techfolio.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeskilled.Techfolio.Models.UserModel;


public interface UserRepository extends JpaRepository<UserModel,Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    UserModel findByUsername(String username);
    
}

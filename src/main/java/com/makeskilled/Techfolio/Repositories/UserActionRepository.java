package com.makeskilled.Techfolio.Repositories;

import com.makeskilled.Techfolio.Models.UserActionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepository extends JpaRepository<UserActionModel, Long> {
    
    UserActionModel findByUserIdAndProjectId(Long userId, Long projectId);

}
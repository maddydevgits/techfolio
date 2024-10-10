package com.makeskilled.Techfolio.Repositories;

import com.makeskilled.Techfolio.Models.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentModel, Long> {
    List<CommentModel> findByProjectId(Long projectId);
}
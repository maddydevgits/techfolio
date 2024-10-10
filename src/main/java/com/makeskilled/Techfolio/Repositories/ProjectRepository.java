package com.makeskilled.Techfolio.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.makeskilled.Techfolio.Models.ProjectModel;

public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {
    List<ProjectModel> findByUsername(String username); // Find projects by username
}
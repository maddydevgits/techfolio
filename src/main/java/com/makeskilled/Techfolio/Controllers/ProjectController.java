package com.makeskilled.Techfolio.Controllers;

import com.makeskilled.Techfolio.Models.CommentModel;
import com.makeskilled.Techfolio.Models.ProjectModel;
import com.makeskilled.Techfolio.Models.UserActionModel;
import com.makeskilled.Techfolio.Models.UserModel;
import com.makeskilled.Techfolio.Repositories.CommentRepository;
import com.makeskilled.Techfolio.Repositories.ProjectRepository;
import com.makeskilled.Techfolio.Repositories.UserActionRepository;
import com.makeskilled.Techfolio.Repositories.UserRepository;
import com.makeskilled.Techfolio.Services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private UserActionRepository userActionRepo;

    @Autowired
    EmailService emailService;


    // Define the directory to store uploaded videos
    private static final String VIDEO_UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploaded_videos").toString();

    @PostMapping("/upload")
    public String uploadProject(
            @RequestParam("project_name") String projectName,
            @RequestParam("description") String description,
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam("github_link") String githubLink,
            @RequestParam("deployed_url") String deployedUrl,
            HttpSession session, // Get session to retrieve username
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username"); // Retrieve the logged-in user's username

        // Create new project instance and populate fields
        ProjectModel project = new ProjectModel();
        project.setProjectName(projectName);
        project.setDescription(description);
        project.setGithubLink(githubLink);
        project.setDeployedUrl(deployedUrl);
        project.setUsername(username); // Set the username in the project model

        // Ensure the upload directory exists
        File uploadDir = new File(VIDEO_UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // Create the directory if it doesn't exist
        }

        // Handle video file upload if it exists
        if (!videoFile.isEmpty()) {
            try {
                // Clean the filename to avoid security issues
                String cleanFilename = Paths.get(videoFile.getOriginalFilename()).getFileName().toString();
                
                // Save the video file to the specified directory
                Path videoPath = Paths.get(VIDEO_UPLOAD_DIR, cleanFilename);
                Files.write(videoPath, videoFile.getBytes());

                // Store the relative path to the video in the project model
                String videoUrl = "http://localhost:8094/uploaded_videos/" + videoFile.getOriginalFilename();
                project.setVideoPath(videoUrl);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Failed to upload project video.");
                return "redirect:/upload";
            }
        }

        // Save project to the repository
        projectRepo.save(project);

        String subject = "Project Uploaded Successfully!";
        //String body = "Dear " + username + ",\n\nYour project '" + projectName + "' has been uploaded successfully.\nYou can view it here: <link_to_project_page>\n\nBest Regards,\nTechfolio Team";
        UserModel user=userRepo.findByUsername(username);

        String projectUploadHtml = "<!DOCTYPE html>"
                                    + "<html lang=\"en\">"
                                    + "<head>"
                                    + "    <meta charset=\"UTF-8\">"
                                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                    + "    <title>Project Uploaded Successfully</title>"
                                    + "    <style>"
                                    + "        body {"
                                    + "            font-family: Arial, sans-serif;"
                                    + "            background-color: #f4f4f4;"
                                    + "            color: #333;"
                                    + "        }"
                                    + "        .container {"
                                    + "            max-width: 600px;"
                                    + "            margin: 0 auto;"
                                    + "            background-color: #fff;"
                                    + "            padding: 20px;"
                                    + "            border-radius: 10px;"
                                    + "            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);"
                                    + "        }"
                                    + "        h1 {"
                                    + "            color: #7a5fb8;"
                                    + "        }"
                                    + "        p {"
                                    + "            font-size: 16px;"
                                    + "        }"
                                    + "        .button {"
                                    + "            display: inline-block;"
                                    + "            background-color: #7a5fb8;"
                                    + "            color: #fff;"
                                    + "            padding: 10px 15px;"
                                    + "            text-decoration: none;"
                                    + "            border-radius: 5px;"
                                    + "            margin-top: 20px;"
                                    + "        }"
                                    + "    </style>"
                                    + "</head>"
                                    + "<body>"
                                    + "    <div class=\"container\">"
                                    + "        <h1>Project Uploaded Successfully!</h1>"
                                    + "        <p>Dear " + username + ",</p>"
                                    + "        <p>Your project \"<strong>" + projectName + "</strong>\" has been uploaded successfully to Techfolio.</p>"
                                    + "        <p>Thank you for contributing to the community!</p>"
                                    + "        <p>Best Regards,<br>The Techfolio Team</p>"
                                    + "    </div>"
                                    + "</body>"
                                    + "</html>";
        
        String response=emailService.sendEmail(user.getEmail(),user.getUsername(),subject, projectUploadHtml);
        System.out.println(response);

        // Flash message and redirect to success page
        redirectAttributes.addFlashAttribute("message", "Project uploaded successfully!");
        return "redirect:/projects/myprojects"; // Redirect to user's projects page
    }

    // Method to display projects by username
    @GetMapping("/myprojects")
    public String viewProjects(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        List<ProjectModel> projects = projectRepo.findByUsername(username);
        model.addAttribute("projects", projects);
        return "user_projects"; // Thymeleaf template to display user's projects
    }

    @GetMapping("/details/{id}")
    public String viewProjectDetails(@PathVariable Long id, Model model) {
        ProjectModel project = projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID: " + id));
        model.addAttribute("project", project);
        return "project_details";  // Thymeleaf template to display the project details
    }

    @PostMapping("/submitComment")
    @ResponseBody
    public String submitComment(
            @RequestParam("projectId") String projectId,
            @RequestParam("commentText") String commentText,
            HttpSession session) {

        String username = (String) session.getAttribute("username"); // Get username from session

        Long projectId1 = Long.valueOf(projectId);

        if (username != null && !commentText.isEmpty()) {
            // Create and save the comment
            CommentModel comment = new CommentModel();
            comment.setUsername(username);
            comment.setComment(commentText);
            comment.setProjectId(projectId1);
            comment.setCommentDate(new Date());

            commentRepo.save(comment);
            ProjectModel project = projectRepo.findById(projectId1).orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
            UserModel projectOwner = userRepo.findByUsername(project.getUsername());

            String subject = "New Comment on Your Project!";
            // String body = "Dear " + projectOwner.getUsername() + ",\n\nYour project '" + project.getProjectName() + "' has received a new comment from " + username + ":\n\n\"" + commentText + "\"\n\nBest Regards,\nTechfolio Team";
            String commentHtml = "<!DOCTYPE html>"
                                + "<html lang=\"en\">"
                                + "<head>"
                                + "    <meta charset=\"UTF-8\">"
                                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                + "    <title>New Comment on Your Project</title>"
                                + "    <style>"
                                + "        body {"
                                + "            font-family: Arial, sans-serif;"
                                + "            background-color: #f4f4f4;"
                                + "            color: #333;"
                                + "        }"
                                + "        .container {"
                                + "            max-width: 600px;"
                                + "            margin: 0 auto;"
                                + "            background-color: #fff;"
                                + "            padding: 20px;"
                                + "            border-radius: 10px;"
                                + "            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);"
                                + "        }"
                                + "        h1 {"
                                + "            color: #7a5fb8;"
                                + "        }"
                                + "        p {"
                                + "            font-size: 16px;"
                                + "        }"
                                + "        .button {"
                                + "            display: inline-block;"
                                + "            background-color: #7a5fb8;"
                                + "            color: #fff;"
                                + "            padding: 10px 15px;"
                                + "            text-decoration: none;"
                                + "            border-radius: 5px;"
                                + "            margin-top: 20px;"
                                + "        }"
                                + "        .comment {"
                                + "            background-color: #f9f9f9;"
                                + "            padding: 10px;"
                                + "            border-radius: 5px;"
                                + "            margin-top: 10px;"
                                + "            font-style: italic;"
                                + "        }"
                                + "    </style>"
                                + "</head>"
                                + "<body>"
                                + "    <div class=\"container\">"
                                + "        <h1>New Comment on Your Project</h1>"
                                + "        <p>Dear " + projectOwner.getUsername() + ",</p>"
                                + "        <p>Your project \"<strong>" + project.getProjectName() + "</strong>\" has received a new comment from " + username + ":</p>"
                                + "        <div class=\"comment\">"
                                + "            \"" + commentText + "\""
                                + "        </div>"
                                + "        <p>Best Regards,<br>The Techfolio Team</p>"
                                + "    </div>"
                                + "</body>"
                                + "</html>";
            String response=emailService.sendEmail(projectOwner.getEmail(),projectOwner.getUsername(), subject, commentHtml);
            System.out.println(response);

            return "Comment submitted successfully!";
        } else {
            return "Failed to submit comment. Please try again.";
        }
    }

    // Optional: A method to get comments for a project
    @GetMapping("/comments/{projectId}")
    @ResponseBody
    public List<CommentModel> getComments(@PathVariable Long projectId) {
        return commentRepo.findByProjectId(projectId);
    }

    @PostMapping("/{projectId}/{action}")
    @ResponseBody
    public Map<String, Object> likeProject(@PathVariable String projectId, @PathVariable String action, HttpSession session) {
        String username = (String) session.getAttribute("username");
        UserModel user = userRepo.findByUsername(username);
        Long projectId1 = Long.valueOf(projectId);
        ProjectModel project = projectRepo.findById(projectId1).orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the user has already liked/disliked the project
        UserActionModel userAction = userActionRepo.findByUserIdAndProjectId(user.getId(), projectId1);

        if (userAction != null) {
            // User already liked or disliked, update if action differs
            if (!userAction.getAction().equals(action)) {
                userAction.setAction(action);  // Update action
                userActionRepo.save(userAction);
                
                if (action.equals("like")) {
                    project.setLikes(project.getLikes() + 1);
                    project.setDislikes(project.getDislikes() - 1);
                } else {
                    project.setLikes(project.getLikes() - 1);
                    project.setDislikes(project.getDislikes() + 1);
                }
            } else {
                // Return error message if the same action is being tried
                return Map.of("success", false, "message", "You already " + action + "d this project.");
            }
        } else {
            // If it's the user's first action, add the like or dislike
            UserActionModel newAction = new UserActionModel();
            newAction.setUser(user);
            newAction.setProject(project);
            newAction.setAction(action);
            userActionRepo.save(newAction);

            if (action.equals("like")) {
                project.setLikes(project.getLikes() + 1);
            } else {
                project.setDislikes(project.getDislikes() + 1);
            }
        }

        projectRepo.save(project);

        ProjectModel project1 = projectRepo.findById(projectId1).orElseThrow(() -> new RuntimeException("Project not found"));
        UserModel projectOwner = userRepo.findByUsername(project1.getUsername());

        String subject = "Your Project Has Been Liked!";
        String body = "Dear " + projectOwner.getUsername() + ",\n\nYour project '" + project1.getProjectName() + "' received a like from " + username + ".\n\nBest Regards,\nTechfolio Team";
        String likeDislikeHtml = "<!DOCTYPE html>"
                                + "<html lang=\"en\">"
                                + "<head>"
                                + "    <meta charset=\"UTF-8\">"
                                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                + "    <title>Your Project Received a Like/Dislike</title>"
                                + "    <style>"
                                + "        body {"
                                + "            font-family: Arial, sans-serif;"
                                + "            background-color: #f4f4f4;"
                                + "            color: #333;"
                                + "        }"
                                + "        .container {"
                                + "            max-width: 600px;"
                                + "            margin: 0 auto;"
                                + "            background-color: #fff;"
                                + "            padding: 20px;"
                                + "            border-radius: 10px;"
                                + "            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);"
                                + "        }"
                                + "        h1 {"
                                + "            color: #7a5fb8;"
                                + "        }"
                                + "        p {"
                                + "            font-size: 16px;"
                                + "        }"
                                + "        .button {"
                                + "            display: inline-block;"
                                + "            background-color: #7a5fb8;"
                                + "            color: #fff;"
                                + "            padding: 10px 15px;"
                                + "            text-decoration: none;"
                                + "            border-radius: 5px;"
                                + "            margin-top: 20px;"
                                + "        }"
                                + "    </style>"
                                + "</head>"
                                + "<body>"
                                + "    <div class=\"container\">"
                                + "        <h1>Your Project Received a " + action + "</h1>"
                                + "        <p>Dear " + projectOwner.getUsername() + ",</p>"
                                + "        <p>Your project \"<strong>" + project1.getProjectName() + "</strong>\" has received a " + action + " from " + username + ".</p>"
                                + "        <p>Keep up the great work!</p>"
                                + "        <p>Best Regards,<br>The Techfolio Team</p>"
                                + "    </div>"
                                + "</body>"
                                + "</html>";
        emailService.sendEmail(projectOwner.getEmail(), projectOwner.getUsername(), subject, likeDislikeHtml);

        // Similarly for dislikes
        if (action.equals("dislike")) {
            subject = "Your Project Has Been Disliked!";
            body = "Dear " + projectOwner.getUsername() + ",\n\nYour project '" + project1.getProjectName() + "' received a dislike from " + username + ".\n\nBest Regards,\nTechfolio Team";

            emailService.sendEmail(projectOwner.getEmail(), projectOwner.getUsername(), subject, body);
        }

        // Return updated like and dislike counts
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("likes", project.getLikes());
        response.put("dislikes", project.getDislikes());
        return response;
    }

    @PostMapping("/like")
    public ResponseEntity<?> likeProject(@RequestParam String projectId) {

        Long projectId1=Long.valueOf(projectId);
        ProjectModel project = projectRepo.findById(projectId1)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        
        project.setLikes(project.getLikes() + 1);  // Increment like count
        projectRepo.save(project);
        
        return ResponseEntity.ok(new LikeDislikeResponse(project.getLikes(), project.getDislikes()));
    }

    @PostMapping("/dislike")
    public ResponseEntity<?> dislikeProject(@RequestParam String projectId) {

        Long projectId1=Long.valueOf(projectId);
        ProjectModel project = projectRepo.findById(projectId1)
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        project.setDislikes(project.getDislikes() + 1);  // Increment dislike count
        projectRepo.save(project);

        return ResponseEntity.ok(new LikeDislikeResponse(project.getLikes(), project.getDislikes()));
    }

    // Create a response class for like/dislike updates
    public static class LikeDislikeResponse {
        private int likes;
        private int dislikes;

        public LikeDislikeResponse(int likes, int dislikes) {
            this.likes = likes;
            this.dislikes = dislikes;
        }

        public int getLikes() {
            return likes;
        }

        public int getDislikes() {
            return dislikes;
        }
    }
}
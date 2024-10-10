package com.makeskilled.Techfolio.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.makeskilled.Techfolio.Models.ProjectModel;
import com.makeskilled.Techfolio.Models.UserModel;
import com.makeskilled.Techfolio.Repositories.ProjectRepository;
import com.makeskilled.Techfolio.Repositories.UserRepository;
import com.makeskilled.Techfolio.Services.EmailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;




@Controller
public class MainController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    ProjectRepository projectRepo;
    
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(){
        return "signup";
    }

    @PostMapping("/signupForm")
    public String signupForm(UserModel user,Model form) {
        
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("Password: " + user.getPassword());

        if (userRepo.existsByUsername(user.getUsername())) {
            form.addAttribute("message", "Username already exists. Please choose another username.");
            return "signup"; // Return to the registration page with the message
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            form.addAttribute("message", "Email already exists. Please use another email.");
            return "signup"; // Return to the registration page with the message
        }

        userRepo.save(user);
        String subject = "Welcome to Techfolio";
        String body = "<div><b>Thank you for registering with Techfolio.</b></div>";
        String response = emailService.sendEmail(user.getEmail(), user.getUsername(), subject, body);
        System.out.println(response);

        form.addAttribute("message", "Registration successful! You can now log in.");
        return "redirect:/login";
    }

    @PostMapping("/loginForm")
    public String loginForm(HttpSession session,UserModel user,Model form) {
        
        System.out.println("Username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());

        if(userRepo.existsByUsername(user.getUsername())==false){
            form.addAttribute("message", "Username doesn't exist, register first");
            return "signup";
        }

        UserModel dbuser = userRepo.findByUsername(user.getUsername());
        if (dbuser != null && dbuser.getPassword().equals(user.getPassword())) {
            // Store username in the session
            session.setAttribute("username", user.getUsername());
            
            form.addAttribute("message", "Login successful! Welcome " + user.getUsername());
            return ("redirect:/dashboard");
        } else {
            form.addAttribute("message", "Invalid username or password. Please try again.");
            return "login";  // Return to login page with an error message
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        List<ProjectModel> publicProjects = projectRepo.findAll(); // Assuming all projects are public
        model.addAttribute("projects", publicProjects);
        return "projects";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Invalidate the session to log out the user
        return "redirect:/login";  // Redirect to the login page
    }

    @GetMapping("/feedback")
    public String feedbackPage() {
        return "feedback";
    }

    @GetMapping("/features")
    public String featuresPage(){
        return "features";
    }
    
    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

}

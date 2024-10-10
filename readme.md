
# **Techfolio Project**

## **Overview**

Techfolio is a platform where users can upload, showcase, and share their technical projects. Users can like, dislike, comment on projects, and explore public projects. Techfolio aims to create a collaborative community for developers to present their work and get feedback from peers.

---

## **Features**

- **Project Submission:** Users can upload projects with details such as project name, description, GitHub link, deployed URL, and an optional project video.
- **Public & Private Projects:** Users can view their own projects as well as explore public projects uploaded by others.
- **Like/Dislike Functionality:** Users can interact with projects by liking or disliking them, with counts displayed dynamically.
- **Commenting System:** Users can comment on projects, and the project owners will be notified of any new comments.
- **Email Notifications:** Users will receive email notifications for important actions like project uploads, new comments, likes/dislikes.
- **Responsive Design:** The UI is optimized for a great user experience across different devices and screen sizes.

---

## **Tech Stack**

- **Backend:** Spring Boot
- **Frontend:** HTML5, Bootstrap, Thymeleaf
- **Database:** MySQL
- **Mailing Service:** ZeptoMail
- **Version Control:** Git, GitHub

---

## **Installation & Setup**

### **Prerequisites**

- Java 8 or above
- Maven
- MySQL
- ZeptoMail API credentials for email notifications
- Git

### **Steps to Run the Project**

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/techfolio.git
   cd techfolio
   ```

2. **Configure MySQL**
   - Create a database named `techfolio`.
   - Update the `application.properties` file with your MySQL credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/techfolio
     spring.datasource.username=your_db_username
     spring.datasource.password=your_db_password
     ```

3. **Configure ZeptoMail**
   - Add your ZeptoMail API credentials in `application.properties`:
     ```properties
     zeptomail.apiKey=your_zeptomail_api_key
     zeptomail.fromEmail=your_email@example.com
     ```

4. **Run the Application**
   - Use Maven to build and run the project:
     ```bash
     mvn clean install
     mvn spring-boot:run
     ```

5. **Access the Application**
   - Open your browser and navigate to `http://localhost:8094`.

---

## **Project Structure**

- **Controllers:** Manage incoming requests, handle business logic, and return views or data.
- **Models:** Define the entities like `ProjectModel`, `CommentModel`, and `UserActionModel`.
- **Repositories:** Data access layer to interact with the database using Spring Data JPA.
- **Templates:** Thymeleaf templates for dynamic rendering of web pages.
- **Static Resources:** CSS, JavaScript, and uploaded project videos.

---

## **API Endpoints**

### **Project-related Endpoints**

- `POST /projects/upload` – Upload a new project.
- `GET /projects/myprojects` – View the projects uploaded by the current user.
- `GET /projects/details/{id}` – View the detailed page for a project.

### **Comment-related Endpoints**

- `POST /projects/submitComment` – Submit a comment on a project.
- `GET /projects/comments/{projectId}` – Get all comments for a project.

### **Like/Dislike Endpoints**

- `POST /projects/{projectId}/like` – Like a project.
- `POST /projects/{projectId}/dislike` – Dislike a project.

---

## **Email Notifications**

ZeptoMail is integrated into the system to send email notifications for the following events:

- **Project Upload:** Users receive an email confirming the successful upload of their project.
- **New Comment:** Users are notified when someone comments on their project.
- **Like/Dislike:** Users are notified when someone likes or dislikes their project.

---

## **Contributing**

If you want to contribute to Techfolio, feel free to submit a pull request or raise issues for any bugs or feature requests.

---

## **License**

This project is licensed under the MIT License.

---

## **Contact**

If you have any questions or suggestions, please feel free to reach out at [maddy@makeskilled.com](mailto:maddy@makeskilled.com).

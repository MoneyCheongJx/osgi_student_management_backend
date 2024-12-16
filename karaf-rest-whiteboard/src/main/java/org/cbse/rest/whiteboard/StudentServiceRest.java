package org.cbse.rest.whiteboard;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.cbse.karaf.rest.api.Student;
import org.cbse.karaf.rest.api.StudentService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Path("/student")
@Component(service = StudentServiceRest.class, property = { "osgi.jaxrs.resource=true" })
public class StudentServiceRest implements StudentService {

    @Reference(target = "(osgi.jndi.service.name=postgres)")
    private DataSource dataSource;
    
    private final Map<Long, Student> students = new LinkedHashMap<>();
	
	public StudentServiceRest(){
		students.put(1L, new Student() {{
			setId(1L);
			setFirstName("Hoong");
			setLastName("Dao Jing");
            setStudentId("U1234567/1");
            setEmail("u1234567@siswa.um.edu.my");
            setFaculty("Faculty of Computer Science and Information Technology");
            setBachelor("Bachelor of Computer Science (Artificial Intelligence)");
		}});
	}

    // Testing api only
    @Path("/test")
    @Produces("application/json")
    @GET
    public String test() {
        String a = "temp";
        try{
            Connection connection = dataSource.getConnection();
            a = connection.getSchema();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("First Name: ").append(rs.getString("first_name")).append("\n");
                sb.append("Last Name: ").append(rs.getString("last_name")).append("\n");
                sb.append("Student ID: ").append(rs.getString("student_id")).append("\n");
                sb.append("Email: ").append(rs.getString("email")).append("\n");
                sb.append("Faculty: ").append(rs.getString("faculty")).append("\n");
                sb.append("Bachelor: ").append(rs.getString("bachelor")).append("\n\n");
            }
            a = sb.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "" + a;
    }

    @Override
    @Path("/")
    @Produces("application/json")
    @GET
    public Collection<Student> list() {

        try{
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY id ASC");
            
            // Clear linkedHashMap
            students.clear();

            // Transform database data to Student entity in linkedHashMap
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getLong("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setStudentId(rs.getString("student_id"));
                student.setEmail(rs.getString("email"));
                student.setFaculty(rs.getString("faculty"));
                student.setBachelor(rs.getString("bachelor"));
    
                String fetchCoursesQuery = "SELECT c.course_name " + 
                "FROM enrollments e " + 
                "JOIN courses c ON e.course_id = c.id " + 
                "WHERE e.student_id = ? " + 
                "ORDER BY c.course_name ASC";

                PreparedStatement courseStmt = connection.prepareStatement(fetchCoursesQuery);
                courseStmt.setString(1, student.getStudentId());
       
                ResultSet courseRs = courseStmt.executeQuery();
                List<String> courses = new ArrayList<>();
                while (courseRs.next()) {
                    courses.add(courseRs.getString("course_name"));
                }
                student.setCourses(courses.toArray(new String[0]));

                students.put(student.getId(), student);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return students.values();
    }

    @Override
    @Path("/{id}")
    @Produces("application/json")
    @GET
    public Student get(@PathParam("id") Long id) {
        try{
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY id ASC");
            
            // Clear linkedHashMap
            students.clear();

            // Transform database data to Student entity in linkedHashMap
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getLong("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setStudentId(rs.getString("student_id"));
                student.setEmail(rs.getString("email"));
                student.setFaculty(rs.getString("faculty"));
                student.setBachelor(rs.getString("bachelor"));
    
                String fetchCoursesQuery = "SELECT c.course_name " + 
                "FROM enrollments e " + 
                "JOIN courses c ON e.course_id = c.id " + 
                "WHERE e.student_id = ? " + 
                "ORDER BY c.course_name ASC";

                PreparedStatement courseStmt = connection.prepareStatement(fetchCoursesQuery);
                courseStmt.setString(1, student.getStudentId());
       
                ResultSet courseRs = courseStmt.executeQuery();
                List<String> courses = new ArrayList<>();
                while (courseRs.next()) {
                    courses.add(courseRs.getString("course_name"));
                }
                student.setCourses(courses.toArray(new String[0]));

                students.put(student.getId(), student);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return students.get(id);
    }
    
    @Override
    @Path("/")
    @Consumes("application/json")
    @POST
    public boolean add(Student student) {

        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO students (id, first_name, last_name, student_id, email, faculty, bachelor) " + "VALUES (?, ?, ?, ?, ?, ?, ?)");

            // Set the values from the Student object to the SQL placeholders
            pstmt.setLong(1, student.getId());         
            pstmt.setString(2, student.getFirstName()); 
            pstmt.setString(3, student.getLastName());  
            pstmt.setString(4, student.getStudentId()); 
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getFaculty());
            pstmt.setString(7, student.getBachelor());

            // Execute the SQL operation
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s) into the database.");

            pstmt.close();
            connection.close();

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    @Path("/")
    @Consumes("application/json")
    @PUT
    public boolean update(@QueryParam("id") Long id, Student student) {

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE students SET first_name = ?, last_name = ?, student_id = ?, email = ?, faculty = ?, bachelor = ? WHERE id = ?"
                );
    
            // Set the values from the Student object to the SQL placeholders
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setString(3, student.getStudentId());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getFaculty());
            pstmt.setString(6, student.getBachelor());
            pstmt.setLong(7, id);
    
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated " + rowsAffected + " row(s) in the database.");
            } else {
                System.out.println("No student found with ID " + id);
                return false; 
            }
    
            pstmt.close();
            connection.close();
    
        } catch (Exception e) {
            e.printStackTrace();
            return false; 
        }
  
        return true;
    }

    @Override
    @Path("/{id}")
    @DELETE
    public boolean remove(@PathParam("id") Long id) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "DELETE FROM students WHERE id = ?"
            );
    
            pstmt.setLong(1, id);
    
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deleted " + rowsAffected + " row(s) in the database.");
            } else {
                System.out.println("No student found with ID " + id);
                return false;
            }
            pstmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }
}


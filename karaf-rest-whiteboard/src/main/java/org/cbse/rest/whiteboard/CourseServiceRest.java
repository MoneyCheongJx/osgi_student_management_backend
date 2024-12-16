package org.cbse.rest.whiteboard;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
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

import org.cbse.karaf.rest.api.Course;
import org.cbse.karaf.rest.api.CourseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Path("/course")
@Component(service = CourseServiceRest.class, property = { "osgi.jaxrs.resource=true" })
public class CourseServiceRest implements CourseService {

    @Reference(target = "(osgi.jndi.service.name=postgres)")
    private DataSource dataSource;
    
    private final Map<Long, Course> courses = new LinkedHashMap<>();
	
	public CourseServiceRest(){
	
	}

    @Override
    @Path("/")
    @Produces("application/json")
    @GET
    public Collection<Course> list() {

        try{
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT c.id AS course_id, " +
            "c.course_name, " +
            "c.course_code, " +
            "c.credit_hour, " +
            "c.occurrence, " +
            "c.academic_session, " +
            "c.semester, " +
            "c.day, " +
            "c.time_start, " +
            "c.time_end, " +
            "c.lecturer_name, " +
            "c.target, " +
            "COUNT(e.id) AS actual, " +
            "c.activity, " +
            "c.registration_status " +
            "FROM courses c " +
            "LEFT JOIN enrollments e ON e.course_id = c.id " +
            "GROUP BY c.id " +
            "ORDER BY c.id ASC");
                        
            // Clear linkedHashMap
            courses.clear();

            // Transform database data to Course entity in linkedHashMap
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getLong("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCreditHour(rs.getInt("credit_hour"));
                course.setOccurrence(rs.getInt("occurrence"));
                course.setAcademicSession(rs.getString("academic_session"));
                course.setSemester(rs.getString("semester"));
                course.setDay(rs.getString("day"));
                course.setTimeStart(rs.getString("time_start"));
                course.setTimeEnd(rs.getString("time_end"));
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setTarget(rs.getInt("target"));
                course.setActual(rs.getInt("actual"));
                course.setActivity(rs.getString("activity"));
                course.setRegistrationStatus(rs.getString("registration_status"));
    
                courses.put(course.getId(), course);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return courses.values();
    }

    @Override
    @Path("/{id}")
    @Produces("application/json")
    @GET
    public Course get(@PathParam("id") Long id) {
        try{
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT c.id AS course_id, " +
            "c.course_name, " +
            "c.course_code, " +
            "c.credit_hour, " +
            "c.occurrence, " +
            "c.academic_session, " +
            "c.semester, " +
            "c.day, " +
            "c.time_start, " +
            "c.time_end, " +
            "c.lecturer_name, " +
            "c.target, " +
            "COUNT(e.id) AS actual, " +
            "c.activity, " +
            "c.registration_status " +
            "FROM courses c " +
            "LEFT JOIN enrollments e ON e.course_id = c.id " +
            "GROUP BY c.id " +
            "ORDER BY c.id ASC");
            
            // Clear linkedHashMap
            courses.clear();

            // Transform database data to Course entity in linkedHashMap
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getLong("course_id"));
                course.setCourseName(rs.getString("course_name"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCreditHour(rs.getInt("credit_hour"));
                course.setOccurrence(rs.getInt("occurrence"));
                course.setAcademicSession(rs.getString("academic_session"));
                course.setSemester(rs.getString("semester"));
                course.setDay(rs.getString("day"));
                course.setTimeStart(rs.getString("time_start"));
                course.setTimeEnd(rs.getString("time_end"));
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setTarget(rs.getInt("target"));
                course.setActual(rs.getInt("actual"));
                course.setActivity(rs.getString("activity"));
                course.setRegistrationStatus(rs.getString("registration_status"));
    
                courses.put(course.getId(), course);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return courses.get(id);
    }
    
    @Override
    @Path("/")
    @Consumes("application/json")
    @POST
    public boolean add(Course course) {

        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO courses (id, course_name, course_code, credit_hour, occurrence, academic_session, semester, day, time_start, time_end, lecturer_name, target, actual, activity, registration_status) " 
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );

            // Set the values from the Course object to the SQL placeholders
            pstmt.setLong(1, course.getId());         
            pstmt.setString(2, course.getCourseName()); 
            pstmt.setString(3, course.getCourseCode());  
            pstmt.setInt(4, course.getCreditHour()); 
            pstmt.setInt(5, course.getOccurrence());
            pstmt.setString(6, course.getAcademicSession());
            pstmt.setString(7, course.getSemester());
            pstmt.setString(8, course.getDay());
            pstmt.setString(9, course.getTimeStart());
            pstmt.setString(10, course.getTimeEnd());
            pstmt.setString(11, course.getLecturerName());
            pstmt.setInt(12, course.getTarget());
            pstmt.setInt(13, course.getActual());
            pstmt.setString(14, course.getActivity());
            pstmt.setString(15, course.getRegistrationStatus());

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
    public boolean update(@QueryParam("id") Long id, Course course) {

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE courses SET course_name = ?, course_code = ?, credit_hour = ?, occurrence = ?, academic_session = ?, semester = ?, day = ?, time_start = ?, time_end = ?, lecturer_name = ?, target = ?, actual = ?, activity = ?, registration_status = ? WHERE id = ?"
                );
    
            // Set the values from the Student object to the SQL placeholders
            pstmt.setString(1, course.getCourseName()); 
            pstmt.setString(2, course.getCourseCode());  
            pstmt.setInt(3, course.getCreditHour()); 
            pstmt.setInt(4, course.getOccurrence());
            pstmt.setString(5, course.getAcademicSession());
            pstmt.setString(6, course.getSemester());
            pstmt.setString(7, course.getDay());
            pstmt.setString(8, course.getTimeStart());
            pstmt.setString(9, course.getTimeEnd());
            pstmt.setString(10, course.getLecturerName());
            pstmt.setInt(11, course.getTarget());
            pstmt.setInt(12, course.getActual());
            pstmt.setString(13, course.getActivity());
            pstmt.setString(14, course.getRegistrationStatus());
            pstmt.setLong(15, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated " + rowsAffected + " row(s) in the database.");
            } else {
                System.out.println("No course found with ID " + id);
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
                "DELETE FROM courses WHERE id = ?"
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


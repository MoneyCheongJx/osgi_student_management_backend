package org.cbse.rest.whiteboard;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

import org.cbse.karaf.rest.api.Enrollment;
import org.cbse.karaf.rest.api.EnrollmentService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Path("/enrollment")
@Component(service = EnrollmentServiceRest.class, property = { "osgi.jaxrs.resource=true" })
public class EnrollmentServiceRest implements EnrollmentService {

    @Reference(target = "(osgi.jndi.service.name=postgres)")
    private DataSource dataSource;
    
    private final Map<Long, Enrollment> enrollments = new LinkedHashMap<>();
	
	public EnrollmentServiceRest(){

	}

    @Override
    @Path("/")
    @Produces("application/json")
    @GET
    public Collection<Enrollment> list() {

        try{
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM enrollments ORDER BY id ASC");
            
            // Clear linkedHashMap
            enrollments.clear();

            // Transform database data to Enrollment entity in linkedHashMap
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setId(rs.getLong("id"));
                enrollment.setCourseId(rs.getLong("course_id"));
                enrollment.setStudentId(rs.getString("student_id"));

                Timestamp timestamp = rs.getTimestamp("enrollment_date");
                OffsetDateTime enrollmentDate = timestamp.toInstant().atOffset(ZoneOffset.UTC);
                enrollment.setEnrollmentDate(enrollmentDate);   

                enrollments.put(enrollment.getId(), enrollment);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return enrollments.values();
    }

    @Override
    @Path("/{id}")
    @Produces("application/json")
    @GET
    public Enrollment get(@PathParam("id") Long id) {
        try{
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM enrollments ORDER BY id ASC");
            
            // Clear linkedHashMap
            enrollments.clear();

            // Transform database data to Enrollment entity in linkedHashMap
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setId(rs.getLong("id"));
                enrollment.setCourseId(rs.getLong("course_id"));
                enrollment.setStudentId(rs.getString("student_id"));

                Timestamp timestamp = rs.getTimestamp("enrollment_date");
                OffsetDateTime enrollmentDate = timestamp.toInstant().atOffset(ZoneOffset.UTC);
                enrollment.setEnrollmentDate(enrollmentDate);
    
                enrollments.put(enrollment.getId(), enrollment);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return enrollments.get(id);
    }
    
    @Override
    @Path("/")
    @Consumes("application/json")
    @POST
    public boolean add(Enrollment enrollment) {

        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO enrollments (course_id, student_id, enrollment_date) " + "VALUES (?, ?, ?)");

            // Set the values from the Enrollment object to the SQL placeholders
            pstmt.setLong(1, enrollment.getCourseId()); 
            pstmt.setString(2, enrollment.getStudentId());
            
            
            LocalDateTime now = LocalDateTime.now();
            Timestamp currentTimestamp = Timestamp.valueOf(now);
            pstmt.setTimestamp(3, currentTimestamp); 

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
    public boolean update(@QueryParam("id") Long id, Enrollment enrollment) {

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE enrollments SET course_id = ?, student_id = ?, enrollment_date = ? WHERE id = ?"
                );
    
            // Set the values from the Enrollment object to the SQL placeholders
            pstmt.setLong(1, enrollment.getCourseId());
            pstmt.setString(2, enrollment.getStudentId());
            
            LocalDateTime now = LocalDateTime.now();
            Timestamp currentTimestamp = Timestamp.valueOf(now);
            pstmt.setTimestamp(3, currentTimestamp); 

            pstmt.setLong(4, id);
    

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated " + rowsAffected + " row(s) in the database.");
            } else {
                System.out.println("No enrollment record found with ID " + id);
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
                "DELETE FROM enrollments WHERE id = ?"
            );
    
            pstmt.setLong(1, id);
    
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deleted " + rowsAffected + " row(s) in the database.");
            } else {
                System.out.println("No enrollment record found with ID " + id);
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


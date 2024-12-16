# CBSE OSGi Student Management Assignment - Apache Karaf with JAX-RS

`Created by Jin Xuan`  
`Last updated by Cheong Jin Xuan on 16/12/2024`

## Abstract

This example shows how to use JAX-RS to implement a REST service integrated with Supabase database.

This example uses blueprint to deal with the jaxrs-server and Apache CXF as the implementation of the JAXRS specification.

It implements a `StudentService`,`CourseService`, `EnrollmentService` with a REST implementation. 

The "client" bundle uses the `StudentService`,`CourseService`, `EnrollmentService`  with a REST client stub.

## Artifacts

* **karaf-rest-example-api** is a common bundle containing the `Student`, `Course`, `Enrollment` POJO and the `StudentService`,`CourseService`, `EnrollmentService` interface.   
* **karaf-rest-example-whiteboard** is a whiteboard bundle providing the `BookingServiceRest` implementation of the `BookingService` interface.
* **karaf-rest-example-features** provides a Karaf features repository used for the deployment.
## Assignment Overview

The Student Management Assignment is designed for staff to handle student, course nd enrollment data in an educational institution, such as a school or university. 

This OSGi service is accessible via the following API endpoints and is readily for easy integration with a frontend application.


### Base URL
The service is available at http://localhost:8181

### API Endpoints
#### Student API Group

- GET /student    
  Retrieve the list of all students details.

- GET /student/:id    
  Get details of a specific student by their ID.

- POST /student    
  Add a new student.

- PUT /student    
  Edit details of an existing student.

- DELETE /student/:id    
  Delete a specific student by their ID.

#### Course API Group

- GET /course    
  Retrieve the list of all course details.

- GET /course/:id    
  Get details of a specific course by their ID.

- POST /course    
  Add a new course.

- PUT /course   
  Edit details of an existing course.
  
- DELETE /course/:id    
  Delete a specific course by their ID.

  #### Enrollment API Group

- GET /enrollment 
  Retrieve the list of all enrollment records.

- POST /enrollment  
  Add a new enrollment record.

- PUT /enrollment  
  Edit details of an enrollment record.
  
- DELETE /enrollment/:id  
  Delete a specific enrollment record by their ID.

## Prerequisite

- Java 8
- Apache Karaf 4.3

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

## Feature and Deployment

1. First, download [Apache Karaf 4.3](https://karaf.apache.org/download)

2. Extract the archive to a folder of your choice.

3. Create a file named `org.ops4j.datasource-postgres.cfg` in the `etc` folder of Karaf with the following content:

```properties
osgi.jdbc.driver.class=org.postgresql.Driver

# Connection details
# Enter your postgres db url eg. jdbc:postgresql://db.cgbszhdymelhvqkhanqs.supabase.co:5432/postgres
jdbc.url=jdbc:postgresql://<host>:<port>/<database>
jdbc.user=<username>
jdbc.password=<password>
dataSourceName=postgres
```

4. Start Karaf:

```bash
./<karaf_location>/bin/karaf debug
```


5. Install all the required feature:

```
karaf@root()> feature:install pax-jdbc-postgresql
karaf@root()> feature:install pax-jdbc-config
karaf@root()> feature:install pax-jdbc-pool-dbcp2 
```

6. Verify that the datasource is created:

```
karaf@root()> service:list DataSource
```

You should see the `DataSource` service registered with all the properties you have set in the `org.ops4j.datasource-postgres.cfg` file.

7. Add our own feature repository:

```
karaf@root()> feature:repo-add mvn:org.cbse/karaf-rest-features/LATEST/xml
```

Then, you can install the service provider feature:

```
karaf@root()> feature:install karaf-rest-whiteboard
```

8. You can now test RESTful API implementation at:

```
http://localhost:8181/
```

9. Everytime you make a change to the code, you need to rebuild the bundles and update them in Karaf:

First rebuild your code:

```bash
mvn clean install
```

Find your bundle id through:
```
karaf@root()> bundle:list | grep karaf-rest
```

Look for the bundle id (left most number) of the bundle you want to update and then update it using:

```
karaf@root()> update <bundle_id>
```

Eg. if you changed something in `karaf-rest-whiteboard` bundle, look for the bundle id of `karaf-rest-whiteboard` and then update it using:
```
karaf@root()> update <bundle_id of karaf-rest-whiteboard>
```

You can also just uninstall the bundle and install it again but it takes longer:

```
karaf@root()> feature:uninstall karaf-rest-whiteboard
karaf@root()> feature:install karaf-rest-whiteboard
```

## Resetting Karaf
If you messed up and want to reset Karaf to its initial state, you can delete the `data` folder in the Karaf directory and restart Karaf.

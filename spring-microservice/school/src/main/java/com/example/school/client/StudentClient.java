package com.example.school.client;

import com.example.school.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "student-service", url = "${application.config.students-url}") // Ensure this URL is set in your application properties
public interface StudentClient {

    @GetMapping("/school/{school-id}") //going to the student service to get all students by school id
    List<Student> findAllStudentsBySchool(@PathVariable("school-id") Integer schoolId);
}

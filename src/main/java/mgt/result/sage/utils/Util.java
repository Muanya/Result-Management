package mgt.result.sage.utils;

import mgt.result.sage.dto.CourseData;
import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.entity.Course;
import mgt.result.sage.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Util {

    public List<UserDetail> getUserDetails(List<? extends User> users) {
        List<UserDetail> details = new ArrayList<>();
        for (User user : users) {
            UserDetail detail = getUserDetailFromUser(user);

            details.add(detail);

        }

        return details;
    }

    public UserDetail getUserDetailFromUser(User user) {
        UserDetail detail = new UserDetail();

        detail.setId(user.getId());
        detail.setFirstName(user.getFirstName());
        detail.setLastName(user.getLastName());
        detail.setEmail(user.getEmail());
        detail.setRole(user.getRole());
        return detail;
    }

    public CourseData getCourseData(Course course) {
        return CourseData.builder()
                .id(course.getId())
                .code(course.getCode())
                .title(course.getTitle())
                .creditUnit(course.getCreditUnit())
                .build();
    }

    public String getStudentFullName(User student) {
        if (student == null) return "";
        String firstName = Optional.ofNullable(student.getFirstName()).orElse("");
        String lastName = Optional.ofNullable(student.getLastName()).orElse("");
        return (firstName + " " + lastName).trim();
    }


}

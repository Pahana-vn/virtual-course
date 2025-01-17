package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {


    @Query("SELECT COUNT(lp) FROM LearningProgress lp WHERE lp.student.id = :studentId AND lp.completed = :completed")
    int countByStudentIdAndCompleted(@Param("studentId") Long studentId, @Param("completed") boolean completed);


    @Query("SELECT lp FROM LearningProgress lp WHERE lp.student.id = :studentId")
    List<LearningProgress> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT lp FROM LearningProgress lp WHERE lp.student.id = :studentId AND lp.completed = true")
    List<LearningProgress> findCompletedCoursesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT lp FROM LearningProgress lp WHERE lp.student.id = :studentId AND lp.progressPercentage < 100 AND lp.completed = false")
    List<LearningProgress> findActiveCoursesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT lp FROM LearningProgress lp WHERE lp.student.id = :studentId")
    List<LearningProgress> findAllCoursesByStudentId(@Param("studentId") Long studentId);
}
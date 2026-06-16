package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.TrainingLectureAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingLectureAttachmentRepo extends JpaRepository<TrainingLectureAttachment, Long> {

    List<TrainingLectureAttachment> findByOrganizationIdAndLectureIdAndActiveTrue(
            Long organizationId,
            Long lectureId
    );

    Optional<TrainingLectureAttachment> findByIdAndOrganizationIdAndLectureId(
            Long id,
            Long organizationId,
            Long lectureId
    );
}
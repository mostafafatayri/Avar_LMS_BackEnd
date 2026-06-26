package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import com.fatayriTech.avarLMS.enums.TrainingProgressStatus;
import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.*;
import com.fatayriTech.avarLMS.request.training.MarkTrainingItemProgressRequest;
import com.fatayriTech.avarLMS.request.training.SubmitMyTrainingQuizRequest;
import com.fatayriTech.avarLMS.response.myTraining.*;
import com.fatayriTech.avarLMS.response.training.TrainingLectureAttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MyTrainingContentService {
    private final TrainingQuizAttemptRepo quizAttemptRepo;
    private final TrainingDisplayModuleItemRepo moduleItemRepo;
    private final TrainingProgressRepo trainingProgressRepo;
    private final TrainingQuizQuestionRepo questionRepo;
    private final TrainingQuizOptionRepo optionRepo;
    private final TrainingLectureRepo lectureRepo;
    private final TrainingLectureAttachmentRepo lectureAttachmentRepo;
    private final TrainingVideoRepo videoRepo;
    private final TrainingQuizRepo quizRepo;

    public MyTrainingContentResponse getContent(
            Long organizationId,
            Long userId,
            Long moduleItemId
    ) {
        TrainingDisplayModuleItem moduleItem = findModuleItem(
                organizationId,
                moduleItemId
        );

        Optional<TrainingProgress> progress =
                trainingProgressRepo.findByOrganizationIdAndUserIdAndModuleItemId(
                        organizationId,
                        userId,
                        moduleItemId
                );

        return mapContent(moduleItem, progress.orElse(null));
    }

    @Transactional
    public MyTrainingProgressResponse markProgress(
            Long organizationId,
            Long userId,
            Long employeeId,
            Long moduleItemId,
            MarkTrainingItemProgressRequest request
    ) {
        TrainingDisplayModuleItem moduleItem = findModuleItem(
                organizationId,
                moduleItemId
        );

        TrainingDisplayModule module = moduleItem.getModule();
        Long trainingCatalogueId = module.getTrainingCatalogue().getId();

        TrainingProgress progress =
                trainingProgressRepo.findByOrganizationIdAndUserIdAndModuleItemId(
                        organizationId,
                        userId,
                        moduleItemId
                ).orElseGet(() -> TrainingProgress.builder()
                        .organizationId(organizationId)
                        .userId(userId)
                        .employeeId(employeeId)
                        .trainingCatalogueId(trainingCatalogueId)
                        .moduleId(module.getId())
                        .moduleItemId(moduleItem.getId())
                        .itemType(moduleItem.getItemType())
                        .itemRefId(moduleItem.getItemRefId())
                        .status(TrainingProgressStatus.NOT_STARTED)
                        .progressPercentage(0)
                        .passed(false)
                        .build()
                );

        TrainingProgressStatus newStatus =
                request.getStatus() != null
                        ? request.getStatus()
                        : TrainingProgressStatus.COMPLETED;

        progress.setStatus(newStatus);

        if (progress.getStartedAt() == null) {
            progress.setStartedAt(LocalDateTime.now());
        }

        if (request.getProgressPercentage() != null) {
            progress.setProgressPercentage(request.getProgressPercentage());
        }

        if (request.getScore() != null) {
            progress.setScore(request.getScore());
        }

        if (request.getPassed() != null) {
            progress.setPassed(request.getPassed());
        }

        if (newStatus == TrainingProgressStatus.IN_PROGRESS) {
            if (progress.getProgressPercentage() == null ||
                    progress.getProgressPercentage() == 0) {
                progress.setProgressPercentage(1);
            }
        }

        if (newStatus == TrainingProgressStatus.COMPLETED) {
            progress.setProgressPercentage(100);
            progress.setPassed(true);
            progress.setCompletedAt(LocalDateTime.now());
        }

        return mapProgress(trainingProgressRepo.save(progress));
    }

    private TrainingDisplayModuleItem findModuleItem(
            Long organizationId,
            Long moduleItemId
    ) {
        TrainingDisplayModuleItem moduleItem = moduleItemRepo
                .findById(moduleItemId)
                .orElseThrow(() -> new RuntimeException("Module item not found"));

        if (!organizationId.equals(moduleItem.getOrganizationId())) {
            throw new RuntimeException("Module item not found in this organization");
        }

        if (!Boolean.TRUE.equals(moduleItem.getActive())) {
            throw new RuntimeException("Module item is inactive");
        }

        return moduleItem;
    }

    private MyTrainingContentResponse mapContent(
            TrainingDisplayModuleItem moduleItem,
            TrainingProgress progress
    ) {
        TrainingDisplayModule module = moduleItem.getModule();

        MyTrainingContentResponse.MyTrainingContentResponseBuilder builder =
                MyTrainingContentResponse.builder()
                        .trainingCatalogueId(module.getTrainingCatalogue().getId())
                        .moduleId(module.getId())
                        .moduleTitle(module.getTitle())
                        .moduleItemId(moduleItem.getId())
                        .itemType(moduleItem.getItemType())
                        .itemRefId(moduleItem.getItemRefId())
                        .progress(progress != null ? mapProgress(progress) : null);

        if (moduleItem.getItemType() == TrainingDisplayItemType.LECTURE) {
            TrainingLecture lecture = lectureRepo.findById(moduleItem.getItemRefId())
                    .orElseThrow(() -> new RuntimeException("Lecture not found"));

            List<TrainingLectureAttachmentResponse> attachments =
                    lectureAttachmentRepo
                            .findByOrganizationIdAndLectureIdAndActiveTrue(
                                    lecture.getOrganizationId(),
                                    lecture.getId()
                            )
                            .stream()
                            .map(this::mapLectureAttachment)
                            .toList();

            return builder
                    .title(lecture.getTitle())
                    .description(lecture.getDescription())
                    .attachments(attachments)
                    .build();
        }

        if (moduleItem.getItemType() == TrainingDisplayItemType.VIDEO) {
            TrainingVideo video = videoRepo.findById(moduleItem.getItemRefId())
                    .orElseThrow(() -> new RuntimeException("Video not found"));

            return builder
                    .title(video.getTitle())
                    .description(video.getDescription())
                    .videoUrl(video.getVideoUrl())
                    .videoType(video.getVideoType())
                    .durationMinutes(video.getDurationMinutes())
                    .build();
        }

        if (moduleItem.getItemType() == TrainingDisplayItemType.QUIZ) {
            TrainingQuiz quiz = quizRepo.findById(moduleItem.getItemRefId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            return builder
                    .title(quiz.getTitle())
                    .description(quiz.getDescription())
                    .passingScore(quiz.getPassingScore())
                    .timeLimitMinutes(quiz.getTimeLimitMinutes())
                    .maxAttempts(quiz.getMaxAttempts())
                    .build();
        }

        throw new RuntimeException("Unsupported content type");
    }

    private TrainingLectureAttachmentResponse mapLectureAttachment(
            TrainingLectureAttachment attachment
    ) {
        return TrainingLectureAttachmentResponse.builder()
                .id(attachment.getId())
                .organizationId(attachment.getOrganizationId())
                .lectureId(attachment.getLecture().getId())
                .fileName(attachment.getFileName())
                .contentType(attachment.getContentType())
                .fileSize(attachment.getFileSize())
                .fileUrl(attachment.getFileUrl())
                .active(attachment.getActive())
                .creationDate(attachment.getCreationDate())
                .modificationDate(attachment.getModificationDate())
                .build();
    }

    private MyTrainingProgressResponse mapProgress(TrainingProgress progress) {
        return MyTrainingProgressResponse.builder()
                .id(progress.getId())
                .organizationId(progress.getOrganizationId())
                .userId(progress.getUserId())
                .employeeId(progress.getEmployeeId())
                .trainingCatalogueId(progress.getTrainingCatalogueId())
                .moduleId(progress.getModuleId())
                .moduleItemId(progress.getModuleItemId())
                .itemType(progress.getItemType())
                .itemRefId(progress.getItemRefId())
                .status(progress.getStatus())
                .progressPercentage(progress.getProgressPercentage())
                .score(progress.getScore())
                .passed(progress.getPassed())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .build();
    }

    public MyTrainingQuizResponse getQuiz(
            Long organizationId,
            Long userId,
            Long moduleItemId
    ) {
        TrainingDisplayModuleItem moduleItem = findModuleItem(
                organizationId,
                moduleItemId
        );

        if (moduleItem.getItemType() != TrainingDisplayItemType.QUIZ) {
            throw new RuntimeException("This module item is not a quiz");
        }

        TrainingQuiz quiz = quizRepo.findById(moduleItem.getItemRefId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int maxAttempts = quiz.getMaxAttempts() != null
                ? quiz.getMaxAttempts()
                : 1;

        boolean unlimitedAttempts = maxAttempts == 0;

        long attemptsUsedLong =
                quizAttemptRepo.countByOrganizationIdAndUserIdAndModuleItemId(
                        organizationId,
                        userId,
                        moduleItemId
                );

        int attemptsUsed = Math.toIntExact(attemptsUsedLong);

        List<TrainingQuizAttempt> attempts =
                quizAttemptRepo.findByOrganizationIdAndUserIdAndModuleItemIdOrderByAttemptNumberDesc(
                        organizationId,
                        userId,
                        moduleItemId
                );

        TrainingQuizAttempt lastAttempt =
                attempts.isEmpty() ? null : attempts.get(0);

        boolean alreadyPassed =
                lastAttempt != null && Boolean.TRUE.equals(lastAttempt.getPassed());

        boolean canAttempt =
                !alreadyPassed && (unlimitedAttempts || attemptsUsed < maxAttempts);

        boolean blocked =
                !alreadyPassed && !canAttempt;

        int attemptsRemaining = unlimitedAttempts
                ? -1
                : Math.max(maxAttempts - attemptsUsed, 0);

        String attemptMessage;

        if (alreadyPassed) {
            attemptMessage = "You already passed this quiz.";
        } else if (canAttempt && attemptsUsed > 0) {
            attemptMessage = "You can retry this quiz.";
        } else if (canAttempt) {
            attemptMessage = "You can start this quiz.";
        } else {
            attemptMessage = "No attempts remaining. Please contact your trainer or LMS administrator.";
        }

        List<MyTrainingQuizQuestionResponse> questions = List.of();

        if (canAttempt) {
            questions =
                    questionRepo.findByOrganizationIdAndQuizIdAndActiveTrueOrderByDisplayOrderAsc(
                                    organizationId,
                                    quiz.getId()
                            )
                            .stream()
                            .map(question -> {
                                List<MyTrainingQuizOptionResponse> options =
                                        optionRepo.findByOrganizationIdAndQuestionIdAndActiveTrueOrderByDisplayOrderAsc(
                                                        organizationId,
                                                        question.getId()
                                                )
                                                .stream()
                                                .map(option -> MyTrainingQuizOptionResponse.builder()
                                                        .id(option.getId())
                                                        .questionId(question.getId())
                                                        .optionText(option.getOptionText())
                                                        .displayOrder(option.getDisplayOrder())
                                                        .build()
                                                )
                                                .toList();

                                return MyTrainingQuizQuestionResponse.builder()
                                        .id(question.getId())
                                        .questionText(question.getQuestionText())
                                        .questionType(question.getQuestionType())
                                        .points(question.getPoints())
                                        .displayOrder(question.getDisplayOrder())
                                        .options(options)
                                        .build();
                            })
                            .toList();
        }

        return MyTrainingQuizResponse.builder()
                .moduleItemId(moduleItem.getId())
                .quizId(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passingScore(quiz.getPassingScore())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .maxAttempts(maxAttempts)
                .attemptsUsed(attemptsUsed)
                .attemptsRemaining(attemptsRemaining)
                .canAttempt(canAttempt)
                .alreadyPassed(alreadyPassed)
                .blocked(blocked)
                .lastScore(lastAttempt != null ? lastAttempt.getScore() : null)
                .lastPassed(lastAttempt != null ? lastAttempt.getPassed() : null)
                .attemptMessage(attemptMessage)
                .questions(questions)
                .build();
    }

    @Transactional
    public MyTrainingQuizSubmitResponse submitQuiz(
            Long organizationId,
            Long userId,
            Long employeeId,
            Long moduleItemId,
            SubmitMyTrainingQuizRequest request
    ) {
        TrainingDisplayModuleItem moduleItem = findModuleItem(organizationId, moduleItemId);

        if (moduleItem.getItemType() != TrainingDisplayItemType.QUIZ) {
            throw new RuntimeException("This module item is not a quiz");
        }

        TrainingDisplayModule module = moduleItem.getModule();

        TrainingQuiz quiz = quizRepo.findById(moduleItem.getItemRefId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int maxAttempts = quiz.getMaxAttempts() != null ? quiz.getMaxAttempts() : 1;

        long attemptsUsedLong =
                quizAttemptRepo.countByOrganizationIdAndUserIdAndModuleItemId(
                        organizationId,
                        userId,
                        moduleItemId
                );

        int attemptsUsed = Math.toIntExact(attemptsUsedLong);

        boolean unlimitedAttempts = maxAttempts == 0;

        if (!unlimitedAttempts && attemptsUsed >= maxAttempts) {
            throw new RuntimeException("No quiz attempts remaining");
        }

        int attemptNumber = attemptsUsed + 1;

        List<TrainingQuizQuestion> questions =
                questionRepo.findByOrganizationIdAndQuizIdAndActiveTrueOrderByDisplayOrderAsc(
                        organizationId,
                        quiz.getId()
                );

        Map<Long, List<Long>> selectedByQuestion = new HashMap<>();

        if (request.getAnswers() != null) {
            for (SubmitMyTrainingQuizRequest.Answer answer : request.getAnswers()) {
                selectedByQuestion.put(
                        answer.getQuestionId(),
                        answer.getSelectedOptionIds() != null
                                ? answer.getSelectedOptionIds()
                                : List.of()
                );
            }
        }

        int totalPoints = 0;
        int earnedPoints = 0;
        int correctAnswers = 0;

        for (TrainingQuizQuestion question : questions) {
            int points = question.getPoints() != null ? question.getPoints() : 1;
            totalPoints += points;

            List<TrainingQuizOption> options =
                    optionRepo.findByOrganizationIdAndQuestionIdAndActiveTrueOrderByDisplayOrderAsc(
                            organizationId,
                            question.getId()
                    );

            Set<Long> correctOptionIds = new HashSet<>();

            for (TrainingQuizOption option : options) {
                if (Boolean.TRUE.equals(option.getCorrect())) {
                    correctOptionIds.add(option.getId());
                }
            }

            Set<Long> selectedOptionIds = new HashSet<>(
                    selectedByQuestion.getOrDefault(question.getId(), List.of())
            );

            boolean correct = selectedOptionIds.equals(correctOptionIds);

            if (correct) {
                earnedPoints += points;
                correctAnswers++;
            }
        }

        int score = totalPoints == 0
                ? 0
                : Math.round((earnedPoints * 100f) / totalPoints);

        int passingScore = quiz.getPassingScore() != null ? quiz.getPassingScore() : 70;

        boolean passed = score >= passingScore;

        quizAttemptRepo.save(
                TrainingQuizAttempt.builder()
                        .organizationId(organizationId)
                        .userId(userId)
                        .employeeId(employeeId)
                        .trainingCatalogueId(module.getTrainingCatalogue().getId())
                        .moduleId(module.getId())
                        .moduleItemId(moduleItem.getId())
                        .quizId(quiz.getId())
                        .attemptNumber(attemptNumber)
                        .score(score)
                        .passed(passed)
                        .submittedAt(LocalDateTime.now())
                        .timeExpired(Boolean.TRUE.equals(request.getTimeExpired()))
                        .build()
        );

        int newAttemptsUsed = attemptNumber;

        int attemptsRemaining = unlimitedAttempts
                ? -1
                : Math.max(maxAttempts - newAttemptsUsed, 0);

        boolean canRetry = !passed && (unlimitedAttempts || attemptsRemaining > 0);

        TrainingProgressStatus finalStatus;

        if (passed) {
            finalStatus = TrainingProgressStatus.COMPLETED;
        } else if (canRetry) {
            finalStatus = TrainingProgressStatus.FAILED;
        } else {
            finalStatus = TrainingProgressStatus.FAILED;
        }

        MarkTrainingItemProgressRequest progressRequest = new MarkTrainingItemProgressRequest();
        progressRequest.setStatus(finalStatus);
        progressRequest.setScore(score);
        progressRequest.setPassed(passed);
        progressRequest.setProgressPercentage(passed ? 100 : 0);

        markProgress(
                organizationId,
                userId,
                employeeId,
                moduleItemId,
                progressRequest
        );

        String message;

        if (passed) {
            message = "Quiz passed successfully.";
        } else if (canRetry) {
            message = "Quiz failed. You still have attempts remaining.";
        } else {
            message = "Quiz failed. No attempts remaining. Please contact your trainer or LMS administrator.";
        }

        return MyTrainingQuizSubmitResponse.builder()
                .score(score)
                .passed(passed)
                .passingScore(passingScore)
                .correctAnswers(correctAnswers)
                .totalQuestions(questions.size())
                .attemptNumber(attemptNumber)
                .attemptsUsed(newAttemptsUsed)
                .attemptsRemaining(attemptsRemaining)
                .maxAttempts(maxAttempts)
                .canRetry(canRetry)
                .timeExpired(Boolean.TRUE.equals(request.getTimeExpired()))
                .status(finalStatus.name())
                .message(message)
                .build();
    }
}
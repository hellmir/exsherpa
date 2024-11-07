package bgaebalja.exsherpa.examination.controller;

import bgaebalja.exsherpa.exam.domain.GetExamResponse;
import bgaebalja.exsherpa.exam.domain.GetExamsResponse;
import bgaebalja.exsherpa.exam.service.ExamService;
import bgaebalja.exsherpa.examination.domain.ExamInformationResponse;
import bgaebalja.exsherpa.examination.domain.GetExaminationHistoriesResponse;
import bgaebalja.exsherpa.examination.domain.SubmitResultRequest;
import bgaebalja.exsherpa.examination.service.ExaminationService;
import bgaebalja.exsherpa.question.domain.GetQuestionResponse;
import bgaebalja.exsherpa.question.domain.Question;
import bgaebalja.exsherpa.question.service.QuestionService;
import bgaebalja.exsherpa.util.FormatConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static org.springframework.http.HttpStatus.CREATED;

@Controller
@RequestMapping("/user/exam")
@RequiredArgsConstructor
public class ExaminationController {
    private final ExaminationService examinationService;
    private final ExamService examService;
    private final QuestionService questionService;

    @GetMapping("/user-exam-cbt")
    public ModelAndView getPracticeInformationPage(@RequestParam("school_level") String schoolLevel, HttpSession httpSession) {
        return new ModelAndView("user/exam/user-exam-cbt", "schoolLevel", schoolLevel);
    }

    @GetMapping("/user/viewer1")
    public String getMiddleHighPracticePage() {
        return "exam/middle-high-practice-view";
    }

    @GetMapping("/user/viewer2")
    public String getElementaryPracticePage() {
        return "exam/elementary-practice-view";
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/user-exam-subject")
    public ModelAndView getExamSubjectPage(
            @RequestParam("school_level") String schoolLevel,
            @RequestParam("exam_round") String examRound,
            @RequestParam("year") String year,
            HttpSession session
    ) {
        ModelAndView modelAndView = new ModelAndView("user/exam/user-exam-subject");
        String email = session.getAttribute("email").toString();
        modelAndView.addObject(
                "examInformationResponse", ExamInformationResponse.of(schoolLevel, examRound, year)
        );

        if (examRound.equals("1")) {
            modelAndView.addObject("getExamsResponse", GetExamsResponse.from(examService.getPastExams(email)));
            return modelAndView;
        }

        modelAndView.addObject("getExamsResponse", GetExamsResponse.from(examService.getBsherpaExams(email)));
        return modelAndView;
    }

    @GetMapping("/user-exam-sound")
    public ModelAndView getExamSoundPage(
            @RequestParam("school_level") String schoolLevel,
            @RequestParam("exam_round") String examRound,
            @RequestParam("year") String year,
            @RequestParam(value = "exam_id", defaultValue = "1") String examId
    ) {
        ExamInformationResponse examInformationResponse
                = ExamInformationResponse.of(schoolLevel, examRound, year, examId);
        if (examRound.equals("1")) {
            return new ModelAndView(
                    "user/exam/user-exam-sound1", "examInformationResponse", examInformationResponse
            );
        }

        return new ModelAndView("user/exam/user-exam-sound2", "examInformationResponse", examInformationResponse);
    }

    @GetMapping("/user-exam-notice")
    public ModelAndView getExamNoticePage(
            @RequestParam("school_level") String schoolLevel,
            @RequestParam("exam_round") String examRound,
            @RequestParam("year") String year,
            @RequestParam(value = "exam_id", defaultValue = "1") String examId
    ) {
        ExamInformationResponse examInformationResponse
                = ExamInformationResponse.of(schoolLevel, examRound, year, examId);
        if (examRound.equals("1")) {
            return new ModelAndView(
                    "user/exam/user-exam-notice1", "examInformationResponse", examInformationResponse
            );
        }

        return new ModelAndView("user/exam/user-exam-notice2", "examInformationResponse", examInformationResponse);
    }

    @GetMapping("/report")
    public ModelAndView getReportPage(
            @RequestParam("school_level") String schoolLevel,
            @RequestParam(value = "exam_round", defaultValue = "1") String examRound,
            @RequestParam("year") String year,
            @RequestParam(value = "examination_sequence", defaultValue = "1") String examinationSequence,
            HttpSession session
    ) {
        ModelAndView modelAndView = new ModelAndView("user/exam/report");
        modelAndView.addObject(
                "examInformationResponse", ExamInformationResponse.of(schoolLevel, examRound, year)
        );
        String email = session.getAttribute("email").toString();
        modelAndView.addObject(
                "getExaminationHistoriesResponse",
                GetExaminationHistoriesResponse.from(examinationService.getSolvedExaminationHistories(email))
        );
        modelAndView.addObject("examination_sequence", FormatConverter.parseToInt(examinationSequence));

        return modelAndView;
    }

    @GetMapping("/exam-view")
    public ModelAndView getActualTestPage(@RequestParam(value = "exam_id") String examId) {
        GetExamResponse getExamResponse
                = GetExamResponse.from(examService.getExam(FormatConverter.parseToLong(examId)));

        return new ModelAndView("exam/exam-view", "getExamResponse", getExamResponse);
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submitResult(@RequestBody SubmitResultRequest submitResultRequest) {
        examinationService.registerResult(submitResultRequest);

        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/report-answer")
    public ModelAndView getReportAnswer(@RequestParam String questionId) {
        Question question = questionService.getQuestion(FormatConverter.parseToLong(questionId));

        return new ModelAndView("item/report-answer", "getQuestionResponse", GetQuestionResponse.from(question));
    }
}

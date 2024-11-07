package bgaebalja.exsherpa.examination.service;

import bgaebalja.exsherpa.examination.domain.ExaminationHistory;
import bgaebalja.exsherpa.examination.domain.SubmitResultRequest;

import java.util.List;

public interface ExaminationService {
    Long registerResult(SubmitResultRequest submitResultRequest);

    List<ExaminationHistory> getSolvedExaminationHistories(String email);
}

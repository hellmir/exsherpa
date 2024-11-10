package bgaebalja.exsherpa.exam.service;

import bgaebalja.exsherpa.exam.domain.Exam;
import bgaebalja.exsherpa.exam.exception.ExamNotFoundException;
import bgaebalja.exsherpa.exam.repository.ExamRepository;
import bgaebalja.exsherpa.user.domain.Users;
import bgaebalja.exsherpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static bgaebalja.exsherpa.exam.exception.ExceptionMessage.EXAM_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@RequiredArgsConstructor
@Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 20)
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Exam> getPastExams(Pageable pageable) {
        return examRepository.findByCustomYnFalseAndDeleteYnFalseAndOpenYnTrue(pageable);
    }

    @Override
    public Page<Exam> getBsherpaExams(Pageable pageable, String email, boolean isMine) {
        if (isMine) {
            Users user = userRepository.getUserWithRoles(email);

            return examRepository.findByUserIdAndCustomYnTrueAndDeleteYnFalseAndOpenYnTrue(user.getId(), pageable);
        }

        return examRepository.findByCustomYnTrueAndDeleteYnFalseAndOpenYnTrue(pageable);
    }

    @Override
    public Exam getExam(Long examId) {
        return examRepository.findByIdAndDeleteYnFalse(examId)
                .orElseThrow(() -> new ExamNotFoundException(String.format(EXAM_NOT_FOUND_EXCEPTION_MESSAGE, examId)));
    }
}

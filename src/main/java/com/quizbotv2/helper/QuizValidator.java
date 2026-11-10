package com.quizbotv2.helper;

import com.quizbotv2.dto.quizdtos.QuizQuestion;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.dto.quizdtos.RawQuestions;
import com.quizbotv2.exception.ErrorCode;
import com.quizbotv2.exception.QuizGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class QuizValidator {

    // extract the quiz data, quiz questions and returns them in a QuizResult Object
    public QuizResult parseQuizResponse(String json, String topic, ObjectMapper mapper) {
        try {
            JsonNode root = mapper.readTree(json);  //JsonNode can navigate

            JsonNode questionsNode = root.get("questions");

            if (questionsNode == null || !questionsNode.isArray()) {
                log.error("Model response does not contain a valid questions array");
                throw new QuizGenerationException(ErrorCode.QUIZ_MISSING_QUESTIONS);
            }

            // converting JSON into a list of objects
            List<RawQuestions> parsed = mapper.convertValue(
                    questionsNode,
                    new TypeReference<List<RawQuestions>>() {

                    }
            );

            Set<Integer> usedIds = new HashSet<>();

            List<QuizQuestion> questions = parsed.stream()
                    .map(q -> {
                        int id;
                        do {
                            id = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
                        } while (!usedIds.add(id));
                        return new QuizQuestion(id, q.question(), q.options(), q.correctAnswer());
                    }).toList();

            long generatedQuizId = java.util.concurrent.ThreadLocalRandom.current().nextLong(10000000L, 100000000L);
            return new QuizResult(generatedQuizId, topic, questions);

        } catch (QuizGenerationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Model response was not valid quiz JSON", e);
            throw new QuizGenerationException(ErrorCode.QUIZ_INVALID_JSON, e);
        }
    }

    public void validateQuiz(QuizResult result) {
        if (result.questions() == null || result.questions().isEmpty()) {
            log.error("Model returned zero questions");
            throw new QuizGenerationException(ErrorCode.QUIZ_ZERO_QUESTIONS);
        }

        for (QuizQuestion q : result.questions()) {
            if (q.options() == null || q.options().size() != 4) {
                log.error("Question missing 4 options: {}", q.question());
                throw new QuizGenerationException(
                        ErrorCode.QUIZ_INVALID_OPTIONS, "Question missing 4 options: " + q.question());
            }

            if (!q.options().contains(q.correctAnswer())) {
                log.error("correctAnswer not among options: {}", q.question());
                throw new QuizGenerationException(
                        ErrorCode.QUIZ_INVALID_ANSWER, "correctAnswer not among options: " + q.question());
            }
        }

    }
}

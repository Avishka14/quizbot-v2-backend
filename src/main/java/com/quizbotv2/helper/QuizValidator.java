package com.quizbotv2.helper;

import com.quizbotv2.dto.quizdtos.QuizQuestion;
import com.quizbotv2.dto.quizdtos.QuizResult;
import com.quizbotv2.exception.QuizGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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
                throw new QuizGenerationException(
                        "Model response does not contain a valid questions array."
                );
            }

            List<QuizQuestion> questions = mapper.convertValue(  //Converts JSON array into a List
                    root.get("questions"),
                    new TypeReference<List<QuizQuestion>>() {}
            );

            return new QuizResult(topic, questions);
        } catch (Exception e) {
            log.error("Model response was not valid quiz JSON ERROR", e);
            throw new QuizGenerationException("Model response was not valid quiz JSON", e);
        }
    }

    public void validateQuiz(QuizResult result) {
        if (result.questions() == null || result.questions().isEmpty()) {
            log.error("Model returned zero questions");
            throw new QuizGenerationException("Model returned zero questions.");
        }

        for (QuizQuestion q : result.questions()) {
            if (q.options() == null || q.options().size() != 4) {
                log.error("Question missing 4 options: {}", q.question());
                throw new QuizGenerationException("Question missing 4 options: " + q.question());
            }

            if (!q.options().contains(q.correctAnswer())) {
                log.error("correctAnswer not among options: {}", q.question());
                throw new QuizGenerationException("correctAnswer not among options: " + q.question());
            }
        }

    }
}

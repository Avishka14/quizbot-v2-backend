package com.quizbotv2.helper;

import com.quizbotv2.dto.quizdtos.QuizGenerationRequest;
import org.springframework.stereotype.Component;

@Component
public class PromptBuildHelper {

    public String buildSystemPrompt() {
        return """
        You are a quiz generator API. Respond ONLY with valid JSON.
        No markdown, no code fences, no commentary before or after.

        Required JSON shape:
        {
          "questions": [
            {
              "question": "string",
              "options": ["string", "string", "string", "string"],
              "correctAnswer": "string"
            }
          ]
        }

        Rules:
        - "options" must contain exactly 4 items.
        - "correctAnswer" must exactly match one of the strings in "options".
        - Output nothing except the JSON object.
        """;
    }

    public String buildUserPrompt(QuizGenerationRequest request) {
        return """
        Generate %d quiz questions about: "%s".
        Difficulty: %s.
        """.formatted(
                request.questionCount(),
                request.topic(),
                request.difficulty()
        );
    }


    // clean up a JSON by removing triple backticks if they exists
    public String sanitizeJsonResponse(String raw) {
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("^```(json)?", "")
                    .replaceAll("```$", "")
                    .trim();
        }
        return trimmed;
    }


}

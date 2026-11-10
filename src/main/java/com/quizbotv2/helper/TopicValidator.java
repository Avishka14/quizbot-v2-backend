package com.quizbotv2.helper;

import com.quizbotv2.exception.ErrorCode;
import com.quizbotv2.exception.InvalidTopicException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TopicValidator {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;
    private static final int MAX_WORDS  = 5;

    // Starts with letter/digit, then letters, digits, space . ' & -
    // Optional "++" or "#" only at the very end (C++, C#)
    private static final Pattern ALLOWED =
            Pattern.compile("^[\\p{L}\\p{N}][\\p{L}\\p{N} .'&-]*(\\+\\+|#)?$");

    // Second layer: known attack phrases
    private static final List<String> SUSPICIOUS_PHRASES = List.of(
            "ignore previous", "ignore all", "ignore above", "disregard",
            "system prompt", "you are now", "act as", "new instructions",
            "reveal your", "jailbreak", "drop table", "union select"
    );

    private TopicValidator() {}

    public static String validate(String rawTopic) {
        if (rawTopic == null || rawTopic.isBlank()) {
            throw new InvalidTopicException(ErrorCode.TOPIC_EMPTY);
        }

        // 1. Normalize: unicode tricks (fullwidth chars etc.), trim, collapse spaces
        String topic = Normalizer.normalize(rawTopic, Normalizer.Form.NFKC)
                .trim()
                .replaceAll("\\s+", " ");

        // 2. Length limits
        if (topic.length() < MIN_LENGTH || topic.length() > MAX_LENGTH) {
            throw new InvalidTopicException(ErrorCode.TOPIC_LENGTH,
                    "Topic must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters.");
        }
        if (topic.split(" ").length > MAX_WORDS) {
            throw new InvalidTopicException(ErrorCode.TOPIC_TOO_MANY_WORDS,
                    "Topic must be at most " + MAX_WORDS + " words.");
        }

        // 3. Allowlist check
        if (!ALLOWED.matcher(topic).matches()) {
            throw new InvalidTopicException(ErrorCode.TOPIC_INVALID_CHARS);
        }

        // 4. Suspicious phrase check
        String lower = topic.toLowerCase(Locale.ROOT);
        for (String phrase : SUSPICIOUS_PHRASES) {
            if (lower.contains(phrase)) {
                throw new InvalidTopicException(ErrorCode.TOPIC_NOT_ALLOWED);
            }
        }

        return topic; // always use this cleaned value, never the raw input
    }
}



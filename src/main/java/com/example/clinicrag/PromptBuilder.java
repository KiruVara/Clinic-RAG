package com.example.clinicrag;

import org.springframework.ai.document.Document;
import java.util.List;
import java.util.stream.Collectors;

public class PromptBuilder {
    //ensures no hallucinations, with short clinical ansewrs
    public static final String SYSTEM = """
            You are a helpful assistant for Maple Grove Community Clinic.
            Answer the question using ONLY the context provided.
            If the context does not contain the answer, reply exactly:
            "I don't have that information. Please contact the clinic at 123-456-7890."
            Keep answers short and clear.
            """;
    private PromptBuilder(){};

    public static String buildUserMessage(String question, List<Document> chunks) {
        String context = chunks.stream()
                .map(c -> "[" + c.getMetadata().get("source") + "]\n" + c.getText())
                .collect(Collectors.joining("\n\n---\n\n"));
        return "Context:\n" + context + "\n\nQuestion: " + question;
    }
}

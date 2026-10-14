package com.example.clinicrag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AskService {
    public record Answer(String answer, List<String> sources, long latencyMs){};
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public AskService(VectorStore vectorStore, ChatClient.Builder builder){
        this.vectorStore = vectorStore;
        this.chatClient = builder.build();
    }

    public Answer ask(String question){
        long start = System.currentTimeMillis();
        //embeds the question and retrieves the top 4 similar chunks
        List<Document> hits = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(4)
                .build());

        //sends the system prompt and the user message to the model
        String reply = chatClient.prompt()
                .system(PromptBuilder.SYSTEM)
                .user(PromptBuilder.buildUserMessage(question, hits))
                .call()
                .content();

        List<String> sources = hits.stream()
                .map(d -> String.valueOf(d.getMetadata().get("source")))
                .distinct()
                .toList();

        //returns the final answer with a list of the source documents and how long it took to do
        return new Answer(reply,sources,System.currentTimeMillis() - start);
    }
}

package com.example.clinicrag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

//The "ask" part of RAG:
//1. Search the vector store for chunks similar to the question (retrieval)
//2. Put those chunks into a prompt (augmentation)
//3. Ask the language model to answer from them (generation)

@Service
public class AskService {

    public record Answer(String answer, List<String> sources, long latencyMs){};
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private static final Logger log = LoggerFactory.getLogger(AskService.class);


    // Spring gives us a ChatClient.Builder that is already set up for Ollama
    public AskService(VectorStore vectorStore, ChatClient.Builder builder){
        this.vectorStore = vectorStore;
        this.chatClient = builder.build();
    }
    // @Cacheable: if the same question is asked again, return the saved answer
    // instead of calling the model again. Saves time and compute.
    @Cacheable(cacheNames = "answers", key = "#question.trim().toLowerCase()")
    public Answer ask(String question){
        long start = System.currentTimeMillis();
        //embeds the question and retrieves the top 4 similar chunks
        List<Document> hits = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(4)
                .build());

        if (hits.isEmpty()) {
            return new Answer(PromptBuilder.NO_ANSWER, List.of(),
                    System.currentTimeMillis() - start);
        }

        //sends the system prompt and the user message to the model
        String reply;

        try {
            reply = chatClient.prompt()
                    .system(PromptBuilder.SYSTEM)
                    .user(PromptBuilder.buildUserMessage(question, hits))
                    .call()
                    .content();
        }catch (RuntimeException e){
            log.error("Model call failed", e );
            throw new AnswerGenerationException("The AI model could not answer right now", e);
        }

        List<String> sources = hits.stream()
                .map(d -> String.valueOf(d.getMetadata().get("source")))
                .distinct()
                .toList();

        long latency = System.currentTimeMillis() - start;

        // Log numbers only, not the question text, because questions could contain private information.
        log.info("ask: questionLength={} chunksRetrieved={} latencyMs={}",
                question.length(), hits.size(), latency);
        //returns the final answer with a list of the source documents and how long it took to do
        return new Answer(reply,sources,latency);
    }
}

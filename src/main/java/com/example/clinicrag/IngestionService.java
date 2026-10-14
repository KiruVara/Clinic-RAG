package com.example.clinicrag;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class IngestionService {
    //fields
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbc;

    //constructor
    public IngestionService(VectorStore vectorStore, JdbcTemplate jdbc){
        this.vectorStore = vectorStore;
        this.jdbc = jdbc;
    }

    //methods
    //reads and packages the files
    public int ingestAll() throws IOException{
        //Start clean so re-ingesting doesn't create duplicates
        jdbc.execute("TRUNCATE TABLE vector_store");

        //get all the markdown files in docs
        Resource[] files = new PathMatchingResourcePatternResolver()
                .getResources("classpath:docs/*.md");

        //split the document into 400 token chunks
        //small enough to avoid mixing topics but still preserves meaning
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(400)
                .build();

        List<Document> allChunks = new ArrayList<>();
        for (Resource file : files) {
            //read file
            List<Document> docs = new TextReader(file).read();
            //add the metadata to each chunk
            docs.forEach(d -> d.getMetadata().put("source", file.getFilename()));
            allChunks.addAll(splitter.apply(docs));
        }

        //call the model, generate vectors and store them in pgvector
        vectorStore.add(allChunks);
        return allChunks.size();
    }
}

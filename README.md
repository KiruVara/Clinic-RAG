# Clinic RAG Assistant

A Spring Boot service that answers questions about a fictional clinic's policies (Maple Grove Community Clinic) using **RAG** (Retrieval-Augmented Generation). It runs **fully locally and free**: Postgres with pgvector in Docker, and Ollama for the AI models.

**All documents are fictional. No real patient data is used.**

Built by Kiru V as a learning project to practice building AI features in a Java backend. 

## Status
- [[x]] Project setup (Spring Boot, Spring AI, pgvector in Docker)
- [[x] ] Sample documents
- [[x]] Ingestion (chunk, embed, store)
- [[x]] `/api/ask` endpoint (retrieve, prompt, answer with sources)
- [[x]] Error handling, logging, caching
- [ ] Tests
- [ ] Evaluation with results

Current status: Testing

## How it works

```
Ingest:  Markdown docs -> chunks -> embeddings (Ollama) -> pgvector (Postgres)
Ask:     Question -> embedding -> top 4 similar chunks -> prompt -> local LLM -> answer + sources
```
1. **Ingest** reads each document, splits it into chunks of about 400 chunk size tokens, creates an embedding for each chunk, and stores it in Postgres.
2. **Ask** turns the question into an embedding, finds the closest chunks, puts them into a prompt that tells the model to answer **only** from that context (or say it doesn't know), and returns the answer with its source files.

## Tech stack

Java 25, Spring Boot 3.4, Spring AI, PostgreSQL + pgvector, Ollama (llama3 and nomic-embed-text), Docker Compose, JUnit 5

## Prerequisites

- JDK 25 and Maven
- Docker Desktop
- [Ollama](https://ollama.com), then run:
```
  ollama pull nomic-embed-text
  ollama pull llama3
```
## Run it

1. Start the database: `docker compose up -d db`
2. Make sure Ollama is running
3. Start the app: `mvn spring-boot:run`
4. Health check: http://localhost:8080/actuator/health
5. Load the documents (once) on Postman: `POST http://localhost:8080/api/ingest`
6. Check the count: `http://localhost:8080/api/ingest/status`
7. Ask a question:
```
   POST http://localhost:8080/api/ask 
   "Content-Type: application/json" \
   '{"question":"Can a visitor stay overnight?"}'
```
## Example

Request:
```json
{"question": "Address? and phone number? "}
```

Response:
```
**Address:** 1010 Maple Grove Road, Toronto, ON M5T 2Z5, Canada
**Phone:** 123-456-7890
```

A question the documents can't answer, and what it said:
```
I don't have that information. Please contact the clinic at 123-456-7890.
```

package com.example.clinicrag;

//Thrown when the language model can't produce an answer

public class AnswerGenerationException extends RuntimeException {

    public AnswerGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

}

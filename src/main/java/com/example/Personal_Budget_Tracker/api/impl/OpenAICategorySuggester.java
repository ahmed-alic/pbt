package com.example.Personal_Budget_Tracker.api.impl;

import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Component;

@Component
public class OpenAICategorySuggester implements CategorySuggester {
    private final OpenAiService openAiService;

    public OpenAICategorySuggester(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String suggestCategory(String description) {
        String prompt = "Suggest a category for the following task description: " + description;
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .model("gpt-3.5-turbo-instruct")
                .maxTokens(10)
                .build();
        return openAiService.createCompletion(completionRequest).getChoices().get(0).getText().trim();
    }
}


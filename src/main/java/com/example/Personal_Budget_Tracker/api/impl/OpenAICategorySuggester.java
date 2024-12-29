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
        System.out.println("Generating category suggestion for: " + description);
        
        String prompt = String.format(
            "Given this transaction description: '%s'\n" +
            "Suggest a single word category that best fits this transaction.\n" +
            "Choose ONLY from these available categories: mortgage, groceries, gas, salary, vacation, car\n" +
            "Reply with ONLY the category name in lowercase, nothing else.", 
            description
        );
        
        System.out.println("Sending prompt to OpenAI: " + prompt);
        
        try {
            CompletionRequest completionRequest = CompletionRequest.builder()
                    .prompt(prompt)
                    .model("gpt-3.5-turbo-instruct")
                    .maxTokens(10)
                    .temperature(0.1)
                    .build();
            
            String suggestion = openAiService.createCompletion(completionRequest)
                    .getChoices().get(0).getText().trim().toLowerCase();
            
            System.out.println("Received suggestion from OpenAI: " + suggestion);
            return suggestion;
        } catch (Exception e) {
            System.err.println("Error calling OpenAI: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

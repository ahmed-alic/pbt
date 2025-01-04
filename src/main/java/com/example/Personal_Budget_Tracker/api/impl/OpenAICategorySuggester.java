package com.example.Personal_Budget_Tracker.api.impl;

import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.example.Personal_Budget_Tracker.core.repository.CategoryRepository;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Component
public class OpenAICategorySuggester implements CategorySuggester {
    private final OpenAiService openAiService;
    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(OpenAICategorySuggester.class);

    public OpenAICategorySuggester(OpenAiService openAiService, CategoryRepository categoryRepository) {
        this.openAiService = openAiService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String suggestCategory(String description) {
        logger.info("Generating category suggestion for: {}", description);
        
        // Get all available categories
        String availableCategories = categoryRepository.findAll()
            .stream()
            .map(category -> category.getName().toLowerCase())
            .collect(Collectors.joining(", "));
        
        String prompt = String.format(
            "Given this transaction description: '%s'\n" +
            "Suggest a single word category that best fits this transaction.\n" +
            "Choose ONLY from these available categories: %s\n" +
            "Reply with ONLY the category name in lowercase, nothing else.", 
            description,
            availableCategories
        );
        
        logger.debug("Sending prompt to OpenAI: {}", prompt);
        
        try {
            CompletionRequest completionRequest = CompletionRequest.builder()
                    .prompt(prompt)
                    .model("gpt-3.5-turbo-instruct")
                    .maxTokens(10)
                    .temperature(0.1)
                    .build();
            
            String response = openAiService.createCompletion(completionRequest)
                    .getChoices().get(0).getText().trim().toLowerCase();
            
            logger.info("OpenAI suggested category: {}", response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error getting category suggestion from OpenAI: {}", e.getMessage(), e);
            return "other"; // fallback category
        }
    }
}

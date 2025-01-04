package com.example.Personal_Budget_Tracker.core.config;

import com.example.Personal_Budget_Tracker.api.impl.OpenAICategorySuggester;
import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.example.Personal_Budget_Tracker.core.repository.CategoryRepository;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class OpenAIConfig {
    
    @Value("${openai.secret}")
    private String openAiSecret;
    
    @Bean
    public OpenAiService openAiService() {
        if (openAiSecret == null || openAiSecret.trim().isEmpty()) {
            throw new IllegalStateException("OpenAI API key is not configured. Please check your application-local.yml file.");
        }
        return new OpenAiService(openAiSecret, Duration.ofSeconds(60));
    }

    @Bean
    public CategorySuggester categorySuggester(CategoryRepository categoryRepository) {
        return new OpenAICategorySuggester(openAiService(), categoryRepository);
    }
}

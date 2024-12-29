package com.example.Personal_Budget_Tracker.rest.configuration;
import com.example.Personal_Budget_Tracker.api.impl.OpenAICategorySuggester;
import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class OpenAIConfiguration {
    @Value("${openai.secret}")
    private String apiSecret;

    @Bean
    public CategorySuggester categorySuggester() {
        return new OpenAICategorySuggester(this.openAiService());
    }

    @Bean
    public OpenAiService openAiService() {
        if (apiSecret == null || apiSecret.trim().isEmpty()) {
            throw new IllegalStateException("OpenAI API key is not configured. Please check your .env file.");
        }
        return new OpenAiService(apiSecret, Duration.ofSeconds(30));
    }
}

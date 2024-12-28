package com.example.Personal_Budget_Tracker.rest.configuration;
import com.example.Personal_Budget_Tracker.api.impl.OpenAICategorySuggester;
import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
        return new OpenAiService(this.apiSecret);
    }
}

# Personal Budget Tracker

A full-stack application for tracking personal finances, built with Spring Boot and React.

**LINK for BE deployment: https://pbt-be-live.onrender.com**

## Setup Instructions

### Backend Setup

1. Copy `src/main/resources/application-local.yml.example` to `src/main/resources/application-local.yml`
2. Update the database configuration in `application-local.yml` with your MySQL credentials
3. Add your OpenAI API key to `application-local.yml`
4. Run the Spring Boot application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm start
   ```

## Features

- Transaction tracking
- Budget goal setting
- Category management with AI-powered suggestions
- Dashboard with financial insights
- Expense analysis

## Security Note

The application uses sensitive credentials (database, API keys) which are stored in `application-local.yml`. This file is not committed to version control for security reasons. Make sure to:

1. Never commit `application-local.yml`
2. Keep your API keys secure
3. Use environment variables in production

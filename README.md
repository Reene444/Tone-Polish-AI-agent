# Tone Polish

A simple internal tool that helps support agents refine their messages to be professional, empathetic, and concise using AI.

## Architecture

- **Backend**: Spring Boot (Java) REST API
- **Frontend**: React single-page application
- **AI Integration**: Groq API (recommended) or OpenAI API (with mock fallback)

## Setup Guide

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 16+ and npm

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

3. (Optional) To use AI API instead of mock:
 configure api key in the application.properties

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

   The frontend will start on `http://localhost:3000`

### Running Tests

**Backend Tests:**
```bash
cd backend
mvn test
```

## API Endpoint

### POST /api/refine

Refines a text message to be professional, empathetic, and concise.

**Request Body:**
```json
{
  "text": "your rough draft message here"
}
```

**Response:**
```json
{
  "polishedText": "refined professional message"
}
```

## AI Transcript

I used AI assistance (Claude) extensively to speed up development. Here's a detailed breakdown:

### 1. Project Structure & Configuration
**Prompt Used:**
> "Create a Spring Boot Maven project structure with pom.xml for a REST API that integrates with OpenAI API. Include Spring Web, WebFlux, Jackson, and logging dependencies."

**Result:** Generated complete `pom.xml` with all necessary dependencies and Spring Boot parent configuration.

### 2. REST Controller
**Prompt Used:**
> "Create a Spring Boot REST controller with POST /api/refine endpoint that accepts JSON request with 'text' field and returns JSON response with 'polishedText' field. Include error handling for empty input and CORS configuration for localhost:3000."

**Result:** Generated `RefineController` with proper error handling, validation, and CORS setup.

### 3. Service Layer & Client Architecture
**Prompt Used:**
> "Create a clean service layer architecture with separation of concerns. The service should use a client interface pattern. Create an AIClient interface, GroqAIClient implementation, and MockAIClient for fallback. Use DTOs for request/response instead of string manipulation."

**Result:** 
- Created `AIClient` interface
- Implemented `GroqAIClient` with DTO-based JSON serialization using ObjectMapper
- Created `ChatCompletionRequest` and `ChatCompletionResponse` DTOs
- Implemented `MockAIClient` for testing

**Actual Prompt Sent to Groq API:**
The system sends a two-message structure to AI API:
- **System Message**: "You are a professional communication assistant. Rewrite the following text to be professional, empathetic, and concise while preserving the original meaning and intent."
- **User Message**: The raw input text from the user
- the prompt could be configured with some flexibility 
This prompt instructs the AI to rewrite the text while maintaining professionalism, empathy, and conciseness.

### 4. Retry Mechanism (Enterprise Standard)
**Prompt Used:**
> "Implement enterprise-grade retry mechanism with exponential backoff. Requirements: max 3 retries, initial delay 1s, backoff multiplier 2.0, max delay 10s. Only retry on 5xx errors, 429 rate limits, and network timeouts. Don't retry on 4xx client errors. Include detailed logging."

**Result:**
- Created `RetryPolicy` class for retry configuration and error classification
- Created `RetryExecutor` class for retry execution with exponential backoff
- Integrated retry mechanism into `GroqAIClient`
- Added configurable retry parameters in `application.properties`

### 5. React Frontend
**Prompt Used:**
> "Generate a React component with useState hooks. Include a textarea for input, a 'Refine' button with loading state, and a read-only textarea for results. Handle API errors and empty input validation. Connect to http://localhost:8080/api/refine endpoint."

**Result:** Generated complete React component with state management, error handling, and API integration.

### 6. CSS Styling
**Prompt Used:**
> "Create a clean CSS layout with two-column grid for input and output sections. Make it responsive for mobile devices. Use modern styling with proper spacing and hover effects."

**Result:** Created responsive CSS Grid layout with professional styling.

## Time Log

**Total Time: ~45 minutes**

- **Setup & Project Structure**: 8 minutes
  - Created Maven project structure
  - Set up React app structure
  - Configured dependencies

- **Backend Development**: 15 minutes
  - Implemented REST controller
  - Created service layer with AI client integration
  - Added DTOs for request/response
  - Configured CORS
  - Separated Client layer from Service layer
  - Created DTO classes for API requests/responses
  - Replaced string manipulation with ObjectMapper
  - Added custom exception handling

- **Frontend Development**: 10 minutes
  - Built React component with state management
  - Integrated with backend API
  - Added loading and error states
  - Styled the UI

- **Retry Mechanism Implementation**: 8 minutes
  - Designed RetryPolicy with exponential backoff
  - Implemented RetryExecutor with error classification
  - Integrated retry logic into GroqAIClient
  - Added retry configuration to application.properties

- **Testing**: 3 minutes
  - Wrote unit tests for service layer
  - Tested error handling

- **Documentation**: 1 minute
  - Created README with setup instructions
  - Documented AI usage

## Features

### Core Features
- ✅ REST API endpoint (`POST /api/refine`)
- ✅ Groq API integration (recommended, free and fast) or OpenAI API (with mock fallback)
- ✅ React frontend with text area, refine button, and result display
- ✅ Loading states and error handling
- ✅ Input validation (empty text handling)
- ✅ Unit tests for service layer
- ✅ Clean separation of concerns (Controller/Service/Client/DTO)

### Other Features
- ✅ **Retry Mechanism with Exponential Backoff**
  - Configurable max retries (default: 3)
  - Exponential backoff (1s → 2s → 4s, max 10s)
  - Smart error classification (retry 5xx/429/timeouts, skip 4xx)
  - Detailed retry logging
- ✅ **Client Layer Architecture**
  - Interface-based design (`AIClient`)
  - DTO-based JSON serialization (no string manipulation)
  - Custom exception handling (`AIClientException`)
  - Configuration-driven bean management
- ✅ **Logging & Observability**
  - SLF4J logging throughout
  - Retry attempt logging
  - Error classification logging
- ✅ CORS configuration for frontend-backend communication

## Demo:

<img width="636" height="500" alt="image" src="https://github.com/user-attachments/assets/11b6c6df-7606-49cc-8f54-54d11193a722" />

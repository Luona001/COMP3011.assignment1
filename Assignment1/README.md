# COMP3011 Assignment 1 - Speech-to-Text Web Application

## Architecture
- **Backend:** Spring Boot 4.1.1 + Java 25
- **Frontend:** HTML + JavaScript (MediaRecorder API)
- **STT:** OpenAI gpt-4o-mini-transcribe
- **Concurrency:** Virtual threads (Java 21+)

## Build & Run
export OPENAI_API_KEY=your_key_here
mvn clean package -DskipTests
java -jar target/Assignment1-0.0.1-SNAPSHOT.jar

## API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/transcribe | Upload audio for transcription |
| GET | /api/v1/global/stats | Token usage statistics |
| GET | /api/v1/admin/uptime | Server uptime |
| POST | /api/v1/admin/shutdown | Graceful shutdown |

## Design Decisions
- **RestClient over OpenAI SDK:** More reliable, fewer dependency issues
- **Jackson for JSON parsing:** Eliminates fragile string parsing
- **Virtual threads:** Handles >200 concurrent requests efficiently
- **AtomicLong:** Prevents race conditions in token counting
- **Environment variable for API key:** Never hardcoded or logged
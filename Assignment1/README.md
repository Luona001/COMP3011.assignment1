# COMP3011 Assignment 1 - Speech-to-Text Web Application

## Architecture
- **Backend:** Spring Boot 4.1.1 + Java 25
- **Frontend:** HTML + CSS + JavaScript (MediaRecorder API)
- **STT Service:** OpenAI gpt-4o-mini-transcribe
- **Concurrency:** Virtual threads (Java 21+) with AtomicLong for thread-safe token counting
- **API Communication:** Spring RestClient (not OpenAI SDK) for simplicity and fewer dependencies

## Build & Run
```bash
export OPENAI_API_KEY=your_key_here
mvn clean package -DskipTests
java -jar target/Assignment1-0.0.1-SNAPSHOT.jar
API Endpoints
Method	Endpoint	Description
POST	/api/v1/transcribe	Upload audio for transcription
GET	/api/v1/global/stats	Token usage statistics
GET	/api/v1/admin/uptime	Server uptime
POST	/api/v1/admin/shutdown	Graceful shutdown
Testing Strategy
Regression Tests — REST API Controllers
Test	File	What It Tests
transcribe_returnsJsonText	TranscriptionControllerTest	POST /api/v1/transcribe with mock STT service
transcribe_serviceThrows_returns500	TranscriptionControllerTest	POST /api/v1/transcribe when STT service fails
uptime_returnsUptimeJson	AdminControllerTest	GET /api/v1/admin/uptime with mocked start time
shutdown_firstCall_returns202	AdminControllerTest	POST /api/v1/admin/shutdown (first call)
shutdown_secondCall_returns409	AdminControllerTest	POST /api/v1/admin/shutdown (second call)
stats_returnsTokenCounts	GlobalStatsControllerTest	GET /api/v1/global/stats
Regression Tests — Concurrency & Race Conditions
Test	What It Tests	Expected Result
concurrentUptimeRequests_noCrash	250 simultaneous HTTP GET /uptime	All 250 succeed, 0 failures
concurrentAddTokens_noRaceCondition	200 threads calling addTokens(100,100) concurrently	Exact total: 20000 input + 20000 output
Non-Functional Tests
Test	What It Tests	Expected Result
contextLoads	Spring Boot application context loads correctly	No exception
Design Decisions
- RestClient over OpenAI SDK: More reliable, fewer dependency issues, full control over HTTP requests
- Jackson for JSON parsing: Eliminates fragile string parsing for token extraction from OpenAI response
- Virtual threads: Handles >200 concurrent requests efficiently without thread pool configuration
- AtomicLong/AtomicBoolean: Prevents race conditions in token counting and shutdown state management
- Environment variable for API key: Never hardcoded, logged, or leaked to client side
- MediaRecorder with 16kbps bitrate: Minimizes network load while maintaining transcription quality
- Constructor injection: All controllers and services use constructor-based DI for testability and immutability
Security
- API key loaded from OPENAI_API_KEY environment variable at runtime
- API key never appears in logs, error messages, or client-side code
- API key passed only in Authorization header to OpenAI API
const startButton = document.getElementById("startButton");
const stopButton = document.getElementById("stopButton");
const statusElement = document.getElementById("status");

let mediaRecorder;
let audioChunks = [];
let mediaStream;

startButton.addEventListener("click", startRecording);
stopButton.addEventListener("click", stopRecording);

async function startRecording() {
    try {
        mediaStream = await navigator.mediaDevices.getUserMedia({
            audio: true
        });

        audioChunks = [];

        mediaRecorder = new MediaRecorder(mediaStream);

        mediaRecorder.addEventListener("dataavailable", event => {
            if (event.data.size > 0) {
                audioChunks.push(event.data);
            }
        });

        mediaRecorder.addEventListener("stop", handleRecordingStopped);

        mediaRecorder.start();

        startButton.disabled = true;
        stopButton.disabled = false;

        statusElement.textContent = "Status: Recording...";

    } catch (error) {
        console.error("Could not access microphone:", error);

        statusElement.textContent =
            "Status: Could not access microphone";
    }
}

function stopRecording() {
    if (!mediaRecorder) {
        return;
    }

    mediaRecorder.stop();

    if (mediaStream) {
        mediaStream.getTracks().forEach(track => track.stop());
    }

    startButton.disabled = false;
    stopButton.disabled = true;

    statusElement.textContent = "Status: Recording stopped";
}

async function handleRecordingStopped() {

    const audioBlob = new Blob(audioChunks, {
        type: mediaRecorder.mimeType
    });

    console.log("Audio Blob:", audioBlob);
    console.log("Audio size:", audioBlob.size);
    console.log("Audio type:", audioBlob.type);

    const formData = new FormData();

    formData.append(
        "audio",
        audioBlob,
        "recording.webm"
    );

    try {

        statusElement.textContent =
            "Status: Uploading audio...";

        console.log(
            "Uploading audio to backend..."
        );

        const response = await fetch(
            "/api/v1/transcribe",
            {
                method: "POST",
                body: formData
            }
        );

        console.log(
            "HTTP status:",
            response.status
        );

        if (!response.ok) {
            throw new Error(
                `HTTP error: ${response.status}`
            );
        }

        const result = await response.text();

        console.log(
            "Backend response:",
            result
        );

        statusElement.textContent =
            "Status: Audio uploaded successfully";

    } catch (error) {

        console.error(
            "Failed to upload audio:",
            error
        );

        statusElement.textContent =
            "Status: Upload failed";
    }

    if (mediaStream) {

        mediaStream.getTracks().forEach(
            track => track.stop()
        );

        mediaStream = null;
    }

    startButton.disabled = false;
    stopButton.disabled = true;
}


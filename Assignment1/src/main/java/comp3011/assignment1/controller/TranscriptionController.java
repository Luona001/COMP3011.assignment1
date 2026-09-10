package comp3011.assignment1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class TranscriptionController {

    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribe(
            @RequestParam("audio") MultipartFile audio) {

        System.out.println("Received audio request");

        System.out.println(
                "Original filename: "
                + audio.getOriginalFilename()
        );

        System.out.println(
                "Content type: "
                + audio.getContentType()
        );

        System.out.println(
                "Audio size: "
                + audio.getSize()
                + " bytes"
        );

        return ResponseEntity.ok(
                "Audio received successfully"
        );
    }
}

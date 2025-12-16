package com.tonepolish.controller;

import com.tonepolish.dto.RefineRequest;
import com.tonepolish.dto.RefineResponse;
import com.tonepolish.service.RefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class RefineController {

    private final RefineService refineService;

    @Autowired
    public RefineController(RefineService refineService) {
        this.refineService = refineService;
    }

    @PostMapping("/refine")
    public ResponseEntity<RefineResponse> refine(@RequestBody RefineRequest request) {
        try {
            if (request.getText() == null || request.getText().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new RefineResponse("Error: Input text cannot be empty"));
            }

            String polishedText = refineService.refineText(request.getText());
            return ResponseEntity.ok(new RefineResponse(polishedText));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RefineResponse("Error: " + e.getMessage()));
        }
    }
}


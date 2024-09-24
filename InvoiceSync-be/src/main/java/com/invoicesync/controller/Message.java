package com.invoicesync.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Message {

    @GetMapping("/messages")
    public ResponseEntity<List<String>> messeges() {
        return ResponseEntity.ok(List.of("firs", "second"));
    }
}

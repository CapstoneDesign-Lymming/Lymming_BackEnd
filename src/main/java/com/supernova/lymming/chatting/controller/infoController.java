package com.supernova.lymming.chatting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatting")
public class infoController {
    @GetMapping("/info")
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("WebSocket info endpoint");
    }
}

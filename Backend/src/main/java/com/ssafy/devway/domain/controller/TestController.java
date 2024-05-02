package com.ssafy.devway.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {
    @GetMapping("/cherry/api")
    public String home() {
        return "Hello, Leesters";
    }

    @GetMapping("/cherry/api/test")
    public String hi() {
        return "Hello, testers!!";
    }
}

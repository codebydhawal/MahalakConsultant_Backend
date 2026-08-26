package com.mahalak.media.controller;

import com.mahalak.media.dto.wrapper.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/test")
public class HelloController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {

        return ApiResponse.<String>builder().
                status(HttpStatus.OK.value()).message("Hello World").build();
    }
}

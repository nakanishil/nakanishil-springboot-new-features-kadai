package com.example.samuraitravel.controller;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @GetMapping("/error")
    public String handleError(WebRequest webRequest, Model model) {
        Map<String, Object> errors = errorAttributes.getErrorAttributes(
                webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)
        );
        model.addAttribute("errors", errors);
        return "error"; // error.htmlを表示
    }
}

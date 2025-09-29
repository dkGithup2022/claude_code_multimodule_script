package com.example.hello.api.example;

import com.example.hello.service.example.ExampleReader;
import com.example.hello.model.example.ExampleIdentity;
import com.example.hello.api.example.dto.ExampleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/examples")
public class ExampleApiController {

    private final ExampleReader exampleReader;

    public ExampleApiController(ExampleReader exampleReader) {
        this.exampleReader = exampleReader;
    }

    @GetMapping("/{exampleId}")
    public ExampleResponse getExample(@PathVariable Long exampleId) {
        var example = exampleReader.findByIdentity(new ExampleIdentity(exampleId));
        return ExampleResponse.from(example);
    }

    @GetMapping
    public List<ExampleResponse> getAllExamples() {
        return exampleReader.findAll()
                .stream()
                .map(ExampleResponse::from)
                .collect(Collectors.toList());
    }
}
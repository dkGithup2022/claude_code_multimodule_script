package io.example.hello.api.example;

import io.example.hello.service.example.ExampleReader;
import io.example.hello.model.example.ExampleIdentity;
import io.example.hello.api.example.dto.ExampleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class ExampleApiController {

    private final ExampleReader exampleReader;

    @GetMapping("/{exampleId}")
    public ExampleResponse getExample(@PathVariable Long exampleId) {
        var example = exampleReader.findById(new ExampleIdentity(exampleId));
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

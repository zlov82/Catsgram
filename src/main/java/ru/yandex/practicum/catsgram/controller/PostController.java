package ru.yandex.practicum.catsgram.controller;

import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.enums.SortOrder;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) throws ParameterNotValidException {

        SortOrder sortOrder = SortOrder.from(sort);
        if (sortOrder == null) {
            throw new ParameterNotValidException("sort", "Получено: " + sort + " должно быть: ask или desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }

        if (page < 0) {
            throw new ParameterNotValidException("page", "Начало выборки должно быть положительным числом");
        }


        return postService.findAll(page, size, sort);
    }

    @GetMapping("/{postId}")
    public Optional<Post> findPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
       return postService.update(newPost);
    }

}

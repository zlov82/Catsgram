package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.enums.SortOrder;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {

    private final Map<Long, Post> posts = new HashMap<>();

    @Autowired
    UserService userService;

    public Collection<Post> findAll(Integer from, Integer size, String sort) {
        Optional<SortOrder> sortOrder = Optional.ofNullable(SortOrder.from(sort));
        List<Post> sortedPosts = new ArrayList<>();

        if (sortOrder.isPresent() && sortOrder.get().equals(SortOrder.ASCENDING)) {
            sortedPosts = posts.values().stream()
                    .sorted(Comparator.comparing(Post::getPostDate))
                    .toList();
        } else {
            sortedPosts = posts.values().stream()
                    .sorted(Comparator.comparing(Post::getPostDate).reversed())
                    .toList();
        }

        //Определяем индексы для возвращаемого списка
        int fromIndex = from;
        int toIndex;
        if (sortedPosts.size() < from + size) {
            toIndex = sortedPosts.size();
        } else {
            toIndex = fromIndex + size;
        }
        return sortedPosts.subList(fromIndex,toIndex);
    }

    public Optional<Post> getPostById(Long postId) {
        return Optional.ofNullable(posts.get(postId));
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
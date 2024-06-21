package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String home() {
        return "<h1>Приветствуем в приложении Котограм</h1>";
    }

}

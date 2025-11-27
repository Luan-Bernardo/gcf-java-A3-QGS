package dev.gpa3.gcfjava.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController implements ErrorController {
    
    // Má prática: tratamento de erro genérico redirecionando para a página inicial
    @RequestMapping("/error")
    public String handleError() {
        return "forward:/html/index.html";
    }
    
    // Abordagem corrigida: listar explicitamente as rotas do SPA
    @GetMapping(value = {"/times", "/campeonato", "/campeonato/**"})
    public String forward() {
        return "forward:/html/index.html";
    }
}
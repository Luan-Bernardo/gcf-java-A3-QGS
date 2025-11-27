package dev.gpa3.gcfjava.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Controller para servir o frontend e tratar rotas SPA (Single Page Application). */
@Controller
public class FrontendController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            logger.debug("Error {} on URI: {}", statusCode, requestUri);
            
            if (statusCode == HttpStatus.NOT_FOUND.value() && 
                (requestUri == null || !requestUri.startsWith("/api/"))) {
                return "forward:/html/index.html";
            }
        }
        
        return "forward:/html/index.html";
    }
    
    @GetMapping(value = {"/", "/times", "/campeonatos", "/campeonato/**"})
    public String forward() {
        return "forward:/html/index.html";
    }
}

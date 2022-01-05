package com.example.boot.controller;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class SecureAppController {

    @GetMapping("/open")
    public String open() {
        return "Open endpoint - anyone can access.";
    }

    @GetMapping("/loggedin")
    public String loggedIn(Principal principal) {
        return "Hello " + principal.getName() + "! Looks like you are logged in!";
    }

    @GetMapping("/needsRole")
    public String authRoleGet(Principal principal) {
        return "Hello " + principal.getName() + "! Looks like you have the MGR role!";
    }

    @PostMapping("/needsRole")
    public String authRolePost(Principal principal) {
        return "Hello " + principal.getName() + "! Looks like you have the ADMIN role!";
    }

    @GetMapping("/allDone")
    public String allDone() {
        return "That's All Folks!";
    }

}

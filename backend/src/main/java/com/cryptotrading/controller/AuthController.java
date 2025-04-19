package com.cryptotrading.controller;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.service.IApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IApplicationUserService applicationUserService;

    @Autowired
    public AuthController(IApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PostMapping("/register")
    public ApplicationUserDTO register(@RequestBody ApplicationUserDTO userDTO) {
        return applicationUserService.register(userDTO);
    }

    @PostMapping("/login")
    public ApplicationUserDTO login(@RequestBody ApplicationUserDTO userDTO) {
        return applicationUserService.logIn(userDTO);
    }

    @PostMapping("/restart")
    public ApplicationUserDTO restart(@RequestBody ApplicationUserDTO userDTO) {
        return applicationUserService.restart(userDTO);
    }
}

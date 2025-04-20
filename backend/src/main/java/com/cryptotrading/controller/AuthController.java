package com.cryptotrading.controller;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.service.IApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> register(@RequestBody ApplicationUserDTO userDTO) {
        try{
            ApplicationUserDTO toReturn = applicationUserService.register(userDTO);
            return ResponseEntity.ok(toReturn);
        } catch(IllegalArgumentException | NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ApplicationUserDTO userDTO) {
        try {
            ApplicationUserDTO toReturn = applicationUserService.logIn(userDTO);
            return ResponseEntity.ok(toReturn);
        } catch(IllegalArgumentException | NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters!");
        }
    }

    @PostMapping("/restart")
    public ResponseEntity<?> restart(@RequestBody ApplicationUserDTO userDTO) {
        try{
            ApplicationUserDTO toReturn = applicationUserService.restart(userDTO);
            return ResponseEntity.ok(toReturn);
        } catch(IllegalArgumentException | NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters!");
        }
    }
}

package com.user.checker.UsernameChecker.controller;

import com.user.checker.UsernameChecker.dto.ApiResponse;
import com.user.checker.UsernameChecker.dto.RequestDTO;
import com.user.checker.UsernameChecker.dto.ResponseDTO;
import com.user.checker.UsernameChecker.service.UserNameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/username")
@CrossOrigin(origins = "*")
public class UserNameController {

    private final UserNameService userNameService;

    public UserNameController(UserNameService userNameService) {
        this.userNameService = userNameService;
    }

    @PostMapping("/checkandregisteruser")
    public ResponseEntity<ApiResponse<ResponseDTO>> checkAndRegisterUser(@RequestBody RequestDTO request)
    {
        ResponseDTO result = userNameService.checkAndRegisterUser(request.getUsername(), request.getEmail());

        if (result.isUserExists()) {
            // User already exists, return 409 Conflict with suggested usernames
            ApiResponse<ResponseDTO> response = new ApiResponse<>(
                    false, 
                    "Username already exists. Please choose from the suggested usernames.", 
                    result
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            // User created successfully, return 201 Created
            ApiResponse<ResponseDTO> response = new ApiResponse<>(
                    true, 
                    "User created successfully", 
                    result
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }
}

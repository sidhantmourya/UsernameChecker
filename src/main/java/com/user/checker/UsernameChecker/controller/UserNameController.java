package com.user.checker.UsernameChecker.controller;

import com.user.checker.UsernameChecker.dto.ApiResponse;
import com.user.checker.UsernameChecker.dto.RequestDTO;
import com.user.checker.UsernameChecker.service.UserNameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/username")
public class UserNameController {

    private final UserNameService userNameService;

    public UserNameController(UserNameService userNameService) {
        this.userNameService = userNameService;
    }

    @PostMapping("/checkandregisteruser")
    public ResponseEntity<ApiResponse<Void>> checkAndRegisterUser(@RequestBody RequestDTO request)
    {
        String msg = userNameService.checkAndRegisterUser(request.getUsername(), request.getEmail());

        ApiResponse<Void> response = new ApiResponse<>(true, "User created: "+ msg, null);
        return ResponseEntity.ok(response);
    }
}

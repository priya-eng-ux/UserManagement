package com.example.usermanagement.controller;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto request){
        return ResponseEntity.ok(usersManagementService.createUser(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto request){
        return ResponseEntity.ok(usersManagementService.login(request));
    }

    @GetMapping("/admin/getAllUsers")
    public ResponseEntity<UserDto> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());

    }

    @GetMapping("/admin/getUsers/{userId}")
    public ResponseEntity<UserDto> getUserByID(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer userId, @RequestBody User request){
        return ResponseEntity.ok(usersManagementService.updateUser(userId, request));
    }

    @GetMapping("/adminuser/getMyProfile")
    public ResponseEntity<UserDto> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDto userDto = usersManagementService.getMyProfile(email);
        return  ResponseEntity.status(userDto.getStatusCode()).body(userDto);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }
}

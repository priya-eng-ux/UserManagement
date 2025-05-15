package com.example.usermanagement.service;

import com.example.usermanagement.Repository.UserRepository;
import com.example.usermanagement.config.JWTUtils;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserDto createUser(UserDto userRegistration) {
        UserDto userDto = new UserDto();

        try {
            User user = new User();
            user.setEmail(userRegistration.getEmail());
            user.setAddress(userRegistration.getAddress());
            user.setRole(userRegistration.getRole());
            user.setName(userRegistration.getName());
            user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
            User userResult = userRepository.save(user);
            if (userResult.getId()>0) {
                userDto.setUser(userResult);
                userDto.setMessage("User Saved Successfully");
                userDto.setStatusCode(200);
            }

        }catch (Exception e){
            userDto.setStatusCode(500);
            userDto.setError(e.getMessage());
        }
        return userDto;
    }


    public UserDto login(UserDto loginRequest){
        UserDto userDto = new UserDto();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            userDto.setStatusCode(200);
            userDto.setToken(jwt);
            userDto.setRole(user.getRole());
            userDto.setExpirationTime("24Hrs");
            userDto.setMessage("Successfully Logged In");

        }catch (Exception e){
            userDto.setStatusCode(500);
            userDto.setMessage(e.getMessage());
        }
        return userDto;
    }

    public UserDto getAllUsers() {
        UserDto userDto = new UserDto();

        try {
            List<User> result = userRepository.findAll();
            if (!result.isEmpty()) {
                userDto.setUserList(result);
                userDto.setStatusCode(200);
                userDto.setMessage("Successful");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("No users found");
            }
            return userDto;
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred: " + e.getMessage());
            return userDto;
        }
    }


    public UserDto getUsersById(Integer id) {
        UserDto userDto = new UserDto();
        try {
            User userById = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            userDto.setUser(userById);
            userDto.setStatusCode(200);
            userDto.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred: " + e.getMessage());
        }
        return userDto;
    }


    public UserDto deleteUser(Integer userId) {
        UserDto userDto = new UserDto();
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                userRepository.deleteById(userId);
                userDto.setStatusCode(200);
                userDto.setMessage("User deleted successfully");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return userDto;
    }

    public UserDto updateUser(Integer userId, User updatedUser) {
        UserDto userDto = new UserDto();
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                User existingUser = user.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setAddress(updatedUser.getAddress());
                existingUser.setRole(updatedUser.getRole());
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
                userDto.setUser(userRepository.save(existingUser));
                userDto.setStatusCode(200);
                userDto.setMessage("User updated successfully");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("User not found for update");
            }
        } catch (Exception e) {
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return userDto;
    }


    public UserDto getMyProfile(String email){
        UserDto userDto = new UserDto();
        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                userDto.setUser(user.get());
                userDto.setStatusCode(200);
                userDto.setMessage("successful");
            } else {
                userDto.setStatusCode(404);
                userDto.setMessage("User not found for update");
            }

        }catch (Exception e){
            userDto.setStatusCode(500);
            userDto.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return userDto;

    }
}

package com.example.blogs.Contollers;

import com.example.blogs.payloads.ApiResponse;
import com.example.blogs.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.blogs.payloads.UserDto;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto createUserDto = this.userService.createUser(userDto);
            logger.info("User created successfully: " + createUserDto.getEmail());
            return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating user: " + userDto.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
        try {
            UserDto updatedUser = this.userService.updateUser(userDto, uid);
            logger.info("User updated successfully: " + updatedUser.getEmail());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user with ID: " + uid, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
        try {
            this.userService.deleteUser(uid);
            logger.info("User deleted successfully with ID: " + uid);
            return new ResponseEntity<>(new ApiResponse("User Deleted Successfully", true), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting user with ID: " + uid, e);
            return new ResponseEntity<>(new ApiResponse("User Deletion Failed", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getSingleUser(@PathVariable Integer userId) {
        try {
            UserDto user = this.userService.getUserById(userId);
            logger.info("User retrieved successfully with ID: " + userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error retrieving user with ID: " + userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = this.userService.getAllUsers();
            logger.info("All users retrieved successfully");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error retrieving all users", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

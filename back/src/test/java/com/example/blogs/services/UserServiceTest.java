package com.example.blogs.services;

import com.example.blogs.Repositories.UserRepository;
import com.example.blogs.entities.Role;
import com.example.blogs.entities.User;
import com.example.blogs.exceptions.ResourceNotFoundException;
import com.example.blogs.payloads.UserDto;
import com.example.blogs.services.implementations.UserImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserImplementation userService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password123");
        user.setAbout("Test user");

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("John Doe");
        userDto.setEmail("johndoe@example.com");
        userDto.setAbout("Test user");
    }

    // --- CREATE FUNCTIONALITY TESTS ---
    @Test
    void testCreateUser() {
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        UserDto createdUser = userService.createUser(userDto);

        assertNotNull(createdUser);
        assertEquals(userDto.getEmail(), createdUser.getEmail());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserServerError() {
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Server error"));

        assertThrows(RuntimeException.class, () -> userService.createUser(userDto));
        verify(userRepo, times(1)).save(any(User.class));
    }

    // --- UPDATE FUNCTIONALITY TESTS ---
    @Test
    void testUpdateUser() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        UserDto updatedUser = userService.updateUser(userDto, 1);

        assertNotNull(updatedUser);
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userDto, 1));
    }

    @Test
    void testUpdateUserServerError() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenThrow(new RuntimeException("Server error"));

        assertThrows(RuntimeException.class, () -> userService.updateUser(userDto, 1));
        verify(userRepo, times(1)).save(any(User.class));
    }

    // --- DELETE FUNCTIONALITY TESTS ---
    @Test
    void testDeleteUser() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));
        doNothing().when(userRepo).delete(any(User.class));

        userService.deleteUser(1);

        verify(userRepo, times(1)).delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1));
    }

    @Test
    void testDeleteUserServerError() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Server error")).when(userRepo).delete(any(User.class));

        assertThrows(RuntimeException.class, () -> userService.deleteUser(1));
        verify(userRepo, times(1)).delete(any(User.class));
    }

    // --- GET FUNCTIONALITY TESTS ---
    @Test
    void testGetUserById() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        UserDto foundUser = userService.getUserById(1);

        assertNotNull(foundUser);
        assertEquals(userDto.getEmail(), foundUser.getEmail());
        verify(userRepo, times(1)).findById(1);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void testGetUserByIdServerError() {
        when(userRepo.findById(anyInt())).thenThrow(new RuntimeException("Server error"));

        assertThrows(RuntimeException.class, () -> userService.getUserById(1));
        verify(userRepo, times(1)).findById(1);
    }

    // --- GET ALL FUNCTIONALITY TESTS ---
    @Test
    void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepo.findAll()).thenReturn(users);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        List<UserDto> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(1, allUsers.size());
        verify(userRepo, times(1)).findAll();
    }

    @Test
    void testGetAllUsersServerError() {
        when(userRepo.findAll()).thenThrow(new RuntimeException("Server error"));

        assertThrows(RuntimeException.class, () -> userService.getAllUsers());
        verify(userRepo, times(1)).findAll();
    }

    // --- ADDITIONAL FUNCTIONALITY TESTS ---
    @Test
    void testRegisterNewUser() {
        // Arrange
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenAnswer(invocation -> {
            UserDto input = invocation.getArgument(0);
            User mappedUser = new User();
            mappedUser.setName(input.getName());
            mappedUser.setEmail(input.getEmail());
            mappedUser.setPassword(input.getPassword());
            return mappedUser;
        });
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1); // Simulate saved ID
            return savedUser;
        });
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenAnswer(invocation -> {
            User input = invocation.getArgument(0);
            UserDto output = new UserDto();
            output.setId(input.getId());
            output.setName(input.getName());
            output.setEmail(input.getEmail());
            return output;
        });
        // Act
        UserDto inputDto = new UserDto();
        inputDto.setName("John Doe");
        inputDto.setEmail("johndoe@example.com");
        inputDto.setPassword("password123");

        UserDto registeredUser = userService.registerNewUser(inputDto);
        // Assert
        assertNotNull(registeredUser);
        assertEquals("John Doe", registeredUser.getName());
        assertEquals("johndoe@example.com", registeredUser.getEmail());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepo, times(1)).save(any(User.class));
        verify(modelMapper, times(2)).map(any(), any());
    }

    @Test
    void testFindByUsername() {
        String username = "johndoe@example.com";
        when(userRepo.findByEmail(username)).thenReturn(user);

        User foundUser = userService.findByUsername(username);

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
        verify(userRepo, times(1)).findByEmail(username);
    }

    @Test
    void testFindByUsernameNotFound() {
        String username = "nonexistent@example.com";
        when(userRepo.findByEmail(username)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUsername(username));
        verify(userRepo, times(1)).findByEmail(username);
    }


    @Test
    void testAddUser() {
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.addUser(user);

        assertNotNull(user);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testGetAdminList() {
        List<User> admins = List.of(user);
        when(userRepo.getAdmins(Role.ADMIN)).thenReturn(admins);

        List<User> result = userService.getAdminList();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepo, times(1)).getAdmins(Role.ADMIN);
    }
}

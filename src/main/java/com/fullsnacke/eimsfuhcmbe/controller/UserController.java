package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.UserRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.repository.user.UserNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "API for User Controller")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public UserController(UserServiceImpl userServiceImpl, ModelMapper modelMapper,
                          UserRepository userRepository) {
        this.userServiceImpl = userServiceImpl;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> userList = userServiceImpl.getAllUsers();
        if(userList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(userList);
        }
    }

    @PostMapping
    @Operation(summary = "Add a user", description = "Add a new user")
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody @Valid UserRequestDTO userRequestDTO){
        User user = modelMapper.map(userRequestDTO, User.class);
        User addedUser = userServiceImpl.add(user);
        URI uri = URI.create("/users" + user.getFuId());
        UserResponseDTO userResponseDTO = modelMapper.map(addedUser, UserResponseDTO.class);
        return ResponseEntity.created(uri).body(userResponseDTO);
    }

    @PutMapping("/{fuId}")
    @Operation(summary = "Update a user", description = "Update an existing user")
    public ResponseEntity<UserResponseDTO> updateUserByFuId(@RequestBody @Valid UserRequestDTO userRequestDTO){
        User user = modelMapper.map(userRequestDTO, User.class);
        try{
            User updatedUser = userServiceImpl.updateUser(user);
            UserResponseDTO userResponseDTO = modelMapper.map(updatedUser, UserResponseDTO.class);
            return ResponseEntity.ok(userResponseDTO);
        }catch (UserNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{fuId}")
    @Operation(summary = "Get a user by fuId", description = "Retrieve a user by fuId")
    public ResponseEntity<UserResponseDTO> getUserByFuId(@PathVariable("fuId") String fuId){
        User user = userServiceImpl.getUserByFuId(fuId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }else{
            UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
            return ResponseEntity.ok(userResponseDTO);
        }
    }

    @DeleteMapping("/{fuId}")
    @Operation(summary = "Delete a user by fuId", description = "Delete a user by fuId")
    public ResponseEntity<?> deleteUserByFuId(@PathVariable("fuId") String fuId){
        try{
            userServiceImpl.deleteUser(fuId);
            return ResponseEntity.noContent().build();
        }catch (UserNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }


}

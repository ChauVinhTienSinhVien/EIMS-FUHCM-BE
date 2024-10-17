package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.mapper.UserMapper;
import com.fullsnacke.eimsfuhcmbe.dto.request.UserRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.repository.user.UserNotFoundException;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "User Controller", description = "API for User Controller")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<User> userList = userServiceImpl.getAllUsers();

        for (User user : userList) {
            System.out.println(user.getCreatedAt());
        }

        if(userList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            List<UserResponseDTO> userResponseDTOList;
            userResponseDTOList = userList.stream().map(user -> userMapper.toDto(user)).toList();
            for (UserResponseDTO userResponseDTO : userResponseDTOList) {
                System.out.println(userResponseDTO.getCreatedAt());
            }
            return ResponseEntity.ok(userResponseDTOList);
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    @Operation(summary = "Add a user", description = "Add a new user")
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody @Valid UserRequestDTO userRequestDTO){
        User user = userMapper.toEntity(userRequestDTO);
        user.setIsDeleted(false);
        User addedUser = userServiceImpl.add(user);
        URI uri = URI.create("/users" + user.getFuId());
        UserResponseDTO userResponseDTO = userMapper.toDto(addedUser);
        return ResponseEntity.created(uri).body(userResponseDTO);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('user:create')")
    @Operation(summary = "Add multiple users", description = "Add multiple users")
    public ResponseEntity<List<UserResponseDTO>> addUsers(@RequestBody List<UserRequestDTO> userRequestDTOList){
        List<User> userList = userRequestDTOList.stream()
                .map(userRequestDTO -> userMapper.toEntity(userRequestDTO))
                .toList();
        for(User user: userList){
            user.setIsDeleted(false);
        }
        List<User> addedUsers = userServiceImpl.saveAll(userList);
        List<UserResponseDTO> userResponseDTOList = addedUsers.stream()
                .map(user -> userMapper.toDto(user))
                .toList();
        return ResponseEntity.ok(userResponseDTOList);
    }

    @PutMapping("/{fuId}")
    @PreAuthorize("hasAuthority('user:write')")
    @Operation(summary = "Update a user", description = "Update an existing user")
    public ResponseEntity<UserResponseDTO> updateUserByFuId(@RequestBody @Valid UserRequestDTO userRequestDTO){
        User user = userMapper.toEntity(userRequestDTO);
        try{
            User updatedUser = userServiceImpl.updateUser(user);
            UserResponseDTO userResponseDTO = userMapper.toDto(updatedUser);
            return ResponseEntity.ok(userResponseDTO);
        }catch (UserNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{fuId}")
    @PreAuthorize("hasAuthority('user:read')")
    @Operation(summary = "Get a user by fuId", description = "Retrieve a user by fuId")
    public ResponseEntity<UserResponseDTO> getUserByFuId(@PathVariable("fuId") String fuId){
        User user = userServiceImpl.getUserByFuId(fuId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }else{
            UserResponseDTO userResponseDTO = userMapper.toDto(user);
            return ResponseEntity.ok(userResponseDTO);
        }
    }

    @DeleteMapping("/{fuId}")
    @PreAuthorize("hasAuthority('user:delete')")
    @Operation(summary = "Delete a user by fuId", description = "Delete a user by fuId")
    public ResponseEntity<?> deleteUserByFuId(@PathVariable("fuId") String fuId){
        try{
            userServiceImpl.deleteUser(fuId);
            return ResponseEntity.noContent().build();
        }catch (UserNotFoundException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userInfo")
    public ResponseEntity<UserResponseDTO> getUserInfo(@AuthenticationPrincipal
    OAuth2User oAuth2User){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userServiceImpl.getMyInfo(oAuth2User));
    }
}

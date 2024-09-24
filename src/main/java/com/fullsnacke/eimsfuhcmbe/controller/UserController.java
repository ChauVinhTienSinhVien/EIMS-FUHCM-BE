package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.UserRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.repository.user.UserNotFoundException;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserServiceImpl userServiceImpl;
    private ModelMapper modelMapper;

    public UserController(UserServiceImpl userServiceImpl, ModelMapper modelMapper) {
        this.userServiceImpl = userServiceImpl;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> userList = userServiceImpl.getAllUsers();
        if(userList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(userList);
        }
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody @Valid UserRequestDTO userRequestDTO){
        User user = modelMapper.map(userRequestDTO, User.class);
        User addedUser = userServiceImpl.add(user);
        URI uri = URI.create("/users" + user.getFuId());
        UserResponseDTO userResponseDTO = modelMapper.map(addedUser, UserResponseDTO.class);
        return ResponseEntity.created(uri).body(userResponseDTO);
    }

    @PutMapping("/{fuId}")
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
    public ResponseEntity<UserResponseDTO> getUserByFuId(@PathVariable("fuId") String fuId){
        User user = userServiceImpl.getUserByFuId(fuId);
        if(user == null){
            return ResponseEntity.notFound().build();
        }else{
            UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
            return ResponseEntity.ok(userResponseDTO);
        }
    }

}

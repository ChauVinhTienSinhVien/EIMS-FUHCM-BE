package com.fullsnacke.eimsfuhcmbe.controller;

import com.fullsnacke.eimsfuhcmbe.dto.request.UserRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.ApiResponse;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.user.UserNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
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

    @DeleteMapping("/{fuId}")
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

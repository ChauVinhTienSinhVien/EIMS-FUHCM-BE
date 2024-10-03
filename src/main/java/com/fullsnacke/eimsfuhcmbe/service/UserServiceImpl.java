package com.fullsnacke.eimsfuhcmbe.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;

import com.fullsnacke.eimsfuhcmbe.entity.Role;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.user.UserNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.RoleRepository;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public User add(User user) {
        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(() -> new UserNotFoundException("Role not found"));
        user.setRole(role);
        System.out.println(user);
        return userRepository.save(user);
    }

    public User getUserByFuId(String fuId) {
        return userRepository.findByFuId(fuId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationProcessException(ErrorCode.USER_NOT_FOUND));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User userInRequest) {
        String fuId = userInRequest.getFuId();
        User userInDb = userRepository.findByFuId(fuId);

        if (userInDb == null) {
            throw new UserNotFoundException("No User found with given fuId:" + fuId);
        }

        userInDb.setFirstName(userInRequest.getFirstName());
        userInDb.setLastName(userInRequest.getLastName());
        userInDb.setGender(userInRequest.getGender());
        userInDb.setDepartment(userInRequest.getDepartment());

        return userRepository.save(userInDb);
    }


    public void deleteUser(String fuId) {
        User userInDb = userRepository.findByFuId(fuId);
        if(userInDb == null){
            throw new UserNotFoundException("No User found with given fuId:" + fuId);
        }
        userRepository.delete(userInDb);
    }

    public UserResponseDTO getMyInfo(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        System.out.println(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationProcessException(ErrorCode.USER_NOT_FOUND));

        return new UserResponseDTO().builder()
                .fuId(user.getFuId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .department(user.getDepartment())
                .gender(user.getGender())
                .role(user.getRole().getId())
                .build();
    }
}

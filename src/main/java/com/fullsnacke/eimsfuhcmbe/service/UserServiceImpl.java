package com.fullsnacke.eimsfuhcmbe.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;

import com.fullsnacke.eimsfuhcmbe.entity.Role;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.EntityNotFoundException;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User add(User user) {
        User userInDb = userRepository.findByEmailAndIsDeleted(user.getEmail(),false);
        if (userInDb != null) {
            throw new AuthenticationProcessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(() -> new UserNotFoundException("Role not found"));
        user.setRole(role);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public List<User> saveAll(List<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    public User getUserByFuId(String fuId) {
        User user = userRepository.findByFuId(fuId);
        if(user == null){
            throw new EntityNotFoundException(User.class, "fuId", fuId);
        }
        return userRepository.findByFuId(fuId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationProcessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByIsDeleted(false);
    }

    @Override
    public User updateUser(User userInRequest) {
        String fuId = userInRequest.getFuId();
        User userInDb = userRepository.findByFuIdAndIsDeleted(fuId, false);

        if (userInDb == null) {
            throw new EntityNotFoundException(User.class, "fuId", fuId);
        }

        userInDb.setFirstName(userInRequest.getFirstName());
        userInDb.setLastName(userInRequest.getLastName());
        userInDb.setGender(userInRequest.getGender());
        userInDb.setDepartment(userInRequest.getDepartment());
        userInDb.setPhoneNumber(userInRequest.getPhoneNumber());
        userInDb.setRole(roleRepository.findById(userInRequest.getRole().getId()).orElseThrow(() -> new EntityNotFoundException(Role.class, "id", userInRequest.getRole().getId().toString())));

        return userRepository.save(userInDb);
    }

    @Override
    public void deleteUser(String fuId) {
        User userInDb = userRepository.findByFuIdAndIsDeleted(fuId, false);
        if(userInDb == null){
            throw new EntityNotFoundException(User.class, "fuId", fuId);
        }
        userInDb.setIsDeleted(true);
        userRepository.save(userInDb);
    }

    @Override
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

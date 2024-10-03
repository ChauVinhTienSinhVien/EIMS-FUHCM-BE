package com.fullsnacke.eimsfuhcmbe.service;


import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;

import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.repository.user.UserNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public User getUserByFuId(String fuId) {
        return userRepository.findByFuId(fuId);
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


}

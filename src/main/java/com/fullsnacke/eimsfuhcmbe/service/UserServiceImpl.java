package com.fullsnacke.eimsfuhcmbe.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(User user) {
        return userRepository.save(user);
    }

    public User getUserByFuId(String fuId){
        return userRepository.findByFuId(fuId);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}

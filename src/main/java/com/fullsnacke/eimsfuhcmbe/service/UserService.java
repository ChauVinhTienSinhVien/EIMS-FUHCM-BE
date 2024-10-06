package com.fullsnacke.eimsfuhcmbe.service;

import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Role;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.repository.user.UserNotFoundException;
import com.fullsnacke.eimsfuhcmbe.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public interface UserService {

    public User add(User user);

    public User getUserByFuId(String fuId);

    public User getUserByEmail(String email);

    public List<User> getAllUsers();

    public User updateUser(User userInRequest);


    public void deleteUser(String fuId);

    public UserResponseDTO getMyInfo(OAuth2User oAuth2User);
}

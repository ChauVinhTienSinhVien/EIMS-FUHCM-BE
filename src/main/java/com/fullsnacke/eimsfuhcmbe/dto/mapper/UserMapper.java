package com.fullsnacke.eimsfuhcmbe.dto.mapper;

import com.fullsnacke.eimsfuhcmbe.dto.request.UserRequestDTO;
import com.fullsnacke.eimsfuhcmbe.dto.response.UserResponseDTO;
import com.fullsnacke.eimsfuhcmbe.entity.Role;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role", qualifiedByName = "intToRole")
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "role", source = "role", qualifiedByName = "roleToInt")
    @Mapping(target = "createdAt", source = "createdAt")
    UserResponseDTO toDto(User entity);

    @Named("intToRole")
    default Role intToRole(int roleId) {
        Role role = new Role();
        role.setId(roleId);
        return role;
    }

    @Named("roleToInt")
    default int roleToInt(Role role) {
        return role.getId();
    }
}

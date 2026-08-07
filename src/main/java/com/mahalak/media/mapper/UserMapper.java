package com.mahalak.media.mapper;

import com.mahalak.media.dto.request.RegisterUserRequest;
import com.mahalak.media.dto.request.UpdateUserRequest;
import com.mahalak.media.dto.response.UserResponse;
import com.mahalak.media.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(RegisterUserRequest request);

    @Mapping(target = "addresses", ignore = true)
    @Mapping(
            target = "fullName",
            expression = "java(user.getFirstName() + \" \" + user.getLastName())"
    )
    @Mapping(
            target = "role",
            expression = "java(user.getRole() != null ? user.getRole().getRole() : null)"
    )
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    User updateUser(UpdateUserRequest request,
                    @MappingTarget User user);

}
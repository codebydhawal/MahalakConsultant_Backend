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

    /**
     * Register Request -> User Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(RegisterUserRequest request);


    /**
     * User Entity -> User Response
     */
    @Mapping(
            target = "fullName",
            expression = "java(user.getFirstName() + \" \" + user.getLastName())"
    )
    @Mapping(
            target = "role",
            expression = "java(user.getRole() != null ? user.getRole().getRole() : null)"
    )
    UserResponse toResponse(User user);


    /**
     * List<User> -> List<UserResponse>
     */
    List<UserResponse> toResponseList(List<User> users);


    /**
     * Update Existing User
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    void updateUser(UpdateUserRequest request,
                    @MappingTarget User user);

}
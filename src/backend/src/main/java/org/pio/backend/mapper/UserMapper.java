package org.pio.backend.mapper;

import org.mapstruct.*;
import org.pio.backend.dto.request.UserAddRequest;
import org.pio.backend.dto.request.UserUpdateRequest;
import org.pio.backend.dto.response.UserResponse;
import org.pio.backend.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserAddRequest request);

    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", ignore = true )
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}

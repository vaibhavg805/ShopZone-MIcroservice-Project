package com.vaibhav.user_service.mapper;

import com.vaibhav.user_service.dto.UserRequestDto;
import com.vaibhav.user_service.dto.UserResponseDto;
import com.vaibhav.user_service.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper=modelMapper;
    }

    public User convertRequestDtoToEntity(UserRequestDto userRequestDto) {
        return  modelMapper.map(userRequestDto, User.class);
    }

    public UserResponseDto convertEntityToResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

}

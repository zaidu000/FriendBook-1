package com.friendbook.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.friendbook.dto.UserDTO;
import com.friendbook.model.User;

@Configuration
public class AppConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		// Custom mapping: UserDTO.fullName -> User.name
		modelMapper.typeMap(UserDTO.class, User.class).addMappings(mapper -> {
			mapper.map(UserDTO::getFullName, User::setFullName);
		});

		return modelMapper;
	}
}

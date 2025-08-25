package com.friendbook.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.friendbook.dto.UserDTO;
import com.friendbook.model.User;

@Configuration
public class AppConfig {

	//Converting DTO To Entity
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		modelMapper.typeMap(UserDTO.class, User.class).addMappings(mapper -> {
			mapper.map(UserDTO::getFullName, User::setFullName);
		});

		return modelMapper;
	}
}

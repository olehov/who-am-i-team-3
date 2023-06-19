package com.eleks.academy.whoami.service;

import com.eleks.academy.whoami.model.request.AuthorisationRequest;
import com.eleks.academy.whoami.model.request.UserRequestDto;
import com.eleks.academy.whoami.model.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    @Transactional
    UserResponseDto create(UserRequestDto userRequestDto);

    UserResponseDto get(Long id);

    Page<UserResponseDto> getAll(Pageable pageable);

    @Transactional
    UserResponseDto update(Long id, UserRequestDto userRequestDto);

    void deleteById(Long userId);

    @Transactional
    UserResponseDto authorisationUser(AuthorisationRequest authorisation);

}

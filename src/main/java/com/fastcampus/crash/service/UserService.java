package com.fastcampus.crash.service;

import com.fastcampus.crash.exception.user.UserAlreadyExistsException;
import com.fastcampus.crash.exception.user.UserNotFoundException;
import com.fastcampus.crash.model.entity.UserEntity;
import com.fastcampus.crash.model.user.User;
import com.fastcampus.crash.model.user.UserAuthenticationResponse;
import com.fastcampus.crash.model.user.UserLoginRequestBody;
import com.fastcampus.crash.model.user.UserSignUpRequestBody;
import com.fastcampus.crash.repository.UserEntityRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  @Autowired private UserEntityRepository userEntityRepository;
  @Autowired private BCryptPasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return getUserEntityByUsername(username);
  }

  public User signUp(UserSignUpRequestBody userSignUpRequestBody) {

    // 가입된 정보가 있는지
    userEntityRepository
        .findByUsername(userSignUpRequestBody.username())
        .ifPresent(
            user -> {
              throw new UserAlreadyExistsException();
            });

    var userEntity =
        userEntityRepository.save(
            UserEntity.of(
                userSignUpRequestBody.username(),
                passwordEncoder.encode(userSignUpRequestBody.password()),
                userSignUpRequestBody.name(),
                userSignUpRequestBody.email()));

    return User.from(userEntity);
  }

  public UserAuthenticationResponse authenticate(UserLoginRequestBody userLoginRequestBody) {
    var userEntity = getUserEntityByUsername(userLoginRequestBody.username());

    if (passwordEncoder.matches(userLoginRequestBody.password(), userEntity.getPassword())) {
      var accessToken = jwtService.generateAccessToken(userEntity);
      return new UserAuthenticationResponse(accessToken);
    } else {
      throw new UserNotFoundException();
    }
  }

  // username을 받아 userEntity 를 찾고 없을 시 에러를 내려 보내는 - 많이 쓸거라 따로 빼기
  private UserEntity getUserEntityByUsername(String username) {
    return userEntityRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
  }
}

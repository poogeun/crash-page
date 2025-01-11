package com.fastcampus.crash.model.entity;

import com.fastcampus.crash.model.user.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
    name = "\"user\"",
    // 인덱스 설정으로 중복된 username 막음
    indexes = {@Index(name = "user_username_idx", columnList = "username", unique = true)})
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column
  @Enumerated(value = EnumType.STRING)
  private Role role;

  @Column private ZonedDateTime createdDateTime;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public ZonedDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(ZonedDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserEntity that = (UserEntity) o;
    return Objects.equals(getUserId(), that.getUserId())
        && Objects.equals(getUsername(), that.getUsername())
        && Objects.equals(getPassword(), that.getPassword())
        && Objects.equals(getEmail(), that.getEmail())
        && Objects.equals(getName(), that.getName())
        && getRole() == that.getRole()
        && Objects.equals(getCreatedDateTime(), that.getCreatedDateTime());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getUserId(),
        getUsername(),
        getPassword(),
        getEmail(),
        getName(),
        getRole(),
        getCreatedDateTime());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (this.role.equals(Role.ADMIN)) {
      return List.of(
          new SimpleGrantedAuthority("ROLE_" + Role.ADMIN.name()), // hasRole()
          new SimpleGrantedAuthority(Role.ADMIN.name()), // hasAuthority()
          new SimpleGrantedAuthority("ROLE_" + Role.USER.name()),
          new SimpleGrantedAuthority(Role.USER.name()));
    } else {
      return List.of(
          new SimpleGrantedAuthority("ROLE_" + Role.USER.name()),
          new SimpleGrantedAuthority(Role.USER.name()));
    }
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  // 밑의 메서드들은 유저가 정상적인 유저인지 알려주는 기능 -> 사용하지 않을거라 true 리턴
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public static UserEntity of(String username, String password, String email, String name) {
    var userEntity = new UserEntity();
    userEntity.setUsername(username);
    userEntity.setPassword(password);
    userEntity.setEmail(email);
    userEntity.setName(name);
    userEntity.setRole(Role.USER);
    return userEntity;
  }

  // 매번 직접 저장하는게 아니라 jpa 에 의해 데이터가 저장되기 전에 실행해줌
  @PrePersist
  private void prePersist() {
    this.createdDateTime = ZonedDateTime.now();
  }
}

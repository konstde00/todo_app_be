package com.konstde00.todo_app.service.mapper;

import com.konstde00.todo_app.domain.Authority;
import com.konstde00.todo_app.domain.User;
import com.konstde00.todo_app.domain.enums.FeatureFlag;
import com.konstde00.todo_app.service.FileService;
import com.konstde00.todo_app.service.dto.UserDTO;
import com.konstde00.todo_app.service.dto.UserProfileDto;
import java.util.*;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring")
public abstract class UserMapper {

  @Autowired protected FileService fileService;

  public List<UserDTO> usersToUserDTOs(List<User> users) {
    return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).toList();
  }

  public UserDTO userToUserDTO(User user) {
    return new UserDTO(user);
  }

  public User userDTOToUser(UserProfileDto userDTO) {
    if (userDTO == null) {
      return null;
    } else {
      User user = new User();
      user.setId(userDTO.getId());
      user.setLogin(userDTO.getLogin());
      user.setFirstName(userDTO.getFirstName());
      user.setLastName(userDTO.getLastName());
      user.setEmail(userDTO.getEmail());
      user.setActivated(userDTO.isActivated());
      user.setLangKey(userDTO.getLangKey());
      Set<Authority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
      user.setAuthorities(authorities);
      return user;
    }
  }

  private Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
    Set<Authority> authorities = new HashSet<>();

    if (authoritiesAsString != null) {
      authorities =
          authoritiesAsString.stream()
              .map(
                  string -> {
                    Authority auth = new Authority();
                    auth.setName(string);
                    return auth;
                  })
              .collect(Collectors.toSet());
    }

    return authorities;
  }

  public User userFromId(String id) {
    if (id == null) {
      return null;
    }
    User user = new User();
    user.setId(id);
    return user;
  }

  @Named("id")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  public UserDTO toDtoId(User user) {
    if (user == null) {
      return null;
    }
    UserDTO userDto = new UserDTO();
    userDto.setId(user.getId());
    return userDto;
  }

  @Named("idSet")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  public Set<UserDTO> toDtoIdSet(Set<User> users) {
    if (users == null) {
      return Collections.emptySet();
    }

    Set<UserDTO> userSet = new HashSet<>();
    for (User userEntity : users) {
      userSet.add(this.toDtoId(userEntity));
    }

    return userSet;
  }

  @Named("login")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "login", source = "login")
  public UserDTO toDtoLogin(User user) {
    if (user == null) {
      return null;
    }
    UserDTO userDto = new UserDTO();
    userDto.setId(user.getId());
    userDto.setLogin(user.getLogin());
    return userDto;
  }

  @Mapping(target = "authorities", qualifiedByName = "mapAuthorities")
  @Mapping(target = "featureFlags", qualifiedByName = "mapFeatureFlags")
  @Mapping(target = "imageUrl", expression = "java(fileService.getUrl(user.getImage()))")
  public abstract UserProfileDto toUserProfileDto(User user);

  @Named("mapAuthorities")
  public static Set<String> mapAuthorities(Set<Authority> authorities) {

    return authorities.stream().map(Authority::getName).collect(Collectors.toSet());
  }

  @Named("mapFeatureFlags")
  public static Set<Integer> mapFeatureFlags(Set<FeatureFlag> authorities) {

    return authorities.stream().map(FeatureFlag::getId).collect(Collectors.toSet());
  }

  @Named("loginSet")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "id")
  @Mapping(target = "login", source = "login")
  public Set<UserDTO> toDtoLoginSet(Set<User> users) {
    if (users == null) {
      return Collections.emptySet();
    }

    Set<UserDTO> userSet = new HashSet<>();
    for (User userEntity : users) {
      userSet.add(this.toDtoLogin(userEntity));
    }

    return userSet;
  }
}

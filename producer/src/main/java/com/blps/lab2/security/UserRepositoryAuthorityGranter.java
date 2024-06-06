package com.blps.lab2.security;

import java.security.Principal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.jaas.AuthorityGranter;

import com.blps.lab2.model.repository.post.UserRepository;

@RequiredArgsConstructor
public class UserRepositoryAuthorityGranter implements AuthorityGranter {

  private final UserRepository userRepository;

  @Override
  public Set<String> grant(Principal principal) {
    final var user = userRepository.findByPhoneNumber(principal.getName());
    return user.getRoles();
  }
}
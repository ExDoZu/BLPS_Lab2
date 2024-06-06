package com.blps.lab2.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;

import com.blps.lab2.model.repository.post.UserRepository;
import com.blps.lab2.security.UserRepositoryAuthorityGranter;

@Configuration
public class JaasAuthentificationProviderConfig {
    @Bean
    public AbstractJaasAuthenticationProvider jaasAuthenticationProvider(
            final javax.security.auth.login.Configuration configuration,
            final UserRepository userRepository) {
        final var defaultJaasAuthenticationProvider = new DefaultJaasAuthenticationProvider();
        defaultJaasAuthenticationProvider.setConfiguration(configuration);
        defaultJaasAuthenticationProvider.setAuthorityGranters(
                new AuthorityGranter[] { new UserRepositoryAuthorityGranter(userRepository) });
        return defaultJaasAuthenticationProvider;
    }
}

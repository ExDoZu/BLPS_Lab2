package com.blps.lab2.config.security;

import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;

import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blps.lab2.security.UserDetailsLoginModule;

@Configuration
public class JaasConfig {

    @Bean
    public javax.security.auth.login.Configuration configuration(
            final UserDetailsService userDetailsService, final PasswordEncoder passwordEncoder) {
        final var configurationEntries = new AppConfigurationEntry[] {
                new AppConfigurationEntry(
                        UserDetailsLoginModule.class.getCanonicalName(),
                        REQUIRED,
                        Map.of("userDetailsService", userDetailsService, "passwordEncoder", passwordEncoder))
        };
        return new InMemoryConfiguration(Map.of("SPRINGSECURITY", configurationEntries));
    }

   
}
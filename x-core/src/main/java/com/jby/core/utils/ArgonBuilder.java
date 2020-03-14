package com.jby.core.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Argon2 为密码加密工具
 */
@Component
public class ArgonBuilder {

    @Bean
    public Argon2 argon2() {
        return Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }
}

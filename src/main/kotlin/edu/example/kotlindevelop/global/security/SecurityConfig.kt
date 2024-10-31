package edu.example.kotlindevelop.global.security

import edu.example.kotlindevelop.domain.member.service.MemberService
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val customOAuth2UserService: MemberService.CustomOAuth2UserService = MemberService.CustomOAuth2UserService()
) {


    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        AntPathRequestMatcher("/resources/**"),
                        AntPathRequestMatcher("/h2-console/**")
                    ).permitAll()
//                    .requestMatchers("/adm/**").hasRole("ADMIN")
                    .requestMatchers("/").permitAll()
                    .anyRequest().permitAll()
            }
            .headers { headers ->
                headers
                    .addHeaderWriter(
                        XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)
                    )
            }
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/h2-console/**")
            }
            .formLogin{it.disable()}
        http
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfoEndpointConfig ->
                        userInfoEndpointConfig
                            .userService(customOAuth2UserService)
                    }
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}

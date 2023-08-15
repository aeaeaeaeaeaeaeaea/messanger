package massenger.proj.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import massenger.proj.services.PersonDetailsServices;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	 private final PersonDetailsServices personDetailsService;

	    @Autowired
	    public SecurityConfig(PersonDetailsServices personDetailsService) {
	        this.personDetailsService = personDetailsService;
	    }

 
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService);
    }


    protected void configure(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity
        .authorizeRequests()
            .antMatchers("/auth/login", "/auth/reg", "/error").permitAll()
            .antMatchers("/auth/register").permitAll() // Добавляем разрешение на регистрацию
            .antMatchers("/message").authenticated() // Предположим, что для /message нужна роль ROLE_USER
            .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
        .and()
            .formLogin()
            .loginPage("/auth/login")
            .loginProcessingUrl("/process_login")
            .defaultSuccessUrl("/message", true)
            .failureUrl("/auth/login?error")
        .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/auth/login")
        .and()
            .headers().frameOptions().sameOrigin()
        .and()
            .csrf().disable(); // Временно отключаем CSRF для упрощения
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

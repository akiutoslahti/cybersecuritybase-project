package sec.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // This facilitates using H2-console in dev mode
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll().and()
                .headers().frameOptions().sameOrigin().and();

        // Allow registration and login without authentication
        // but require it everywhere else
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().permitAll();

        /*
        Vulnerability: A2 - Broken Authentication
        
        Although cookies are deleted during logout, HttpSession is not
        invalidated. Also sessions have no expiry date.
        
        FIX: Invalidate HttpSession during logout and set session expiry time.
        */
        http.logout()
                .invalidateHttpSession(false)
                .deleteCookies("JSESSIONID").logoutSuccessUrl("/login");

        /*
        Vulnerability: Cross-Site Request Forgery
        
        As many frameworks include CSRF defenses, it is no longer on OWASP
        Top 10 list (2017). In any case CSRF protection mechanisms have been
        disabled here so CSRF is possible.
        
        FIX: Remove line below to enable csrf protection.
        */
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /*
    Vulnerabilities: A2 - Broken Authentication and A3 -Sensitive Data Exposure
    
    Passwords are handled and stored in plain text.
    
    FIX: replace PlainTextPasswordEncoder with BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder();
    }
}

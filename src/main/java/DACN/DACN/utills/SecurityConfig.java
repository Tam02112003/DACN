package DACN.DACN.utills;


import DACN.DACN.services.OauthService;
import DACN.DACN.services.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
@Configuration // Đánh dấu lớp này là một lớp cấu hình cho Spring Context.
@EnableWebSecurity // Kích hoạt tính năng bảo mật web của Spring Security.
@RequiredArgsConstructor // Lombok tự động tạo constructor có tham số cho tất cả các trường final.
public class SecurityConfig {
    @Autowired
    private UserService userService; // Tiêm UserService vào lớp cấu hình này.
    @Autowired
    private OauthService oauthService;
    @Bean // Đánh dấu phương thức trả về một bean được quản lý bởi Spring Context.
    public UserDetailsService userDetailsService() {
        return new UserService(); // Cung cấp dịch vụ xử lý chi tiết người dùng.
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Bean mã hóa mật khẩu sử dụng BCrypt.
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider(); // Tạo nhà cung cấp xác thực.
        auth.setUserDetailsService(userDetailsService()); // Thiết lập dịch vụ chi tiết người dùng.
        auth.setPasswordEncoder(passwordEncoder()); // Thiết lập cơ chế mã hóa mật khẩu.
        return auth; // Trả về nhà cung cấp xác thực.
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/fonts/**", "/css/**", "/", "/img/**", "/video/**", "/uploads/profile-pictures/**",
                                "/Karma Shop-doc/**", "/scss/**", "/js/**", "/assets/**", "/docs/**",
                                "/.github/**", "/register", "/home", "/shop", "/blog", "/blog/detail/**",
                                "/cart", "/cart/**", "/detail/**", "/search", "/search-results", "/suggestions","/api/ai/chat")
                        .permitAll()
                        .requestMatchers("/admin", "/products", "/products/detail/**", "/categories/list",
                                "/categories/create", "/categories/edit", "/categories/delete",
                                "/products/edit/**", "/products/create", "/products/delete",
                                "/order/list", "/orders/details/**", "/sizes", "/sizes/create",
                                "/sizes/edit/**", "/sizes/delete")
                        .hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .logout(logout -> configureLogout(logout))
                .formLogin(formLogin -> configureFormLogin(formLogin))
                .oauth2Login(oauth2Login -> configureOauth2Login(oauth2Login))
                .rememberMe(rememberMe -> configureRememberMe(rememberMe))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403"))
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)
                        .expiredUrl("/login"))
                .httpBasic(httpBasic -> httpBasic
                        .realmName("hutech"));

        return http.build();
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll();
    }

    private void configureFormLogin(FormLoginConfigurer<HttpSecurity> formLogin) {
        formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")
               // .defaultSuccessUrl("/home", true)//\\
                .successHandler(new CustomAuthenticationSuccessHandler()) // Sử dụng handler tùy chỉnh.
                .failureUrl("/login?error")
                .permitAll();
    }

    private void configureOauth2Login(OAuth2LoginConfigurer<HttpSecurity> oauth2Login) {
        oauth2Login
                .loginPage("/login")
                .failureUrl("/login?error")
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oauthService))
                .successHandler((request, response, authentication) -> {
                    var oidcUser = (DefaultOidcUser) authentication.getPrincipal();
                    userService.saveOauthUser(oidcUser.getEmail(), oidcUser.getName());
                    response.sendRedirect("/home");
                })
                .permitAll();
    }

    private void configureRememberMe(RememberMeConfigurer<HttpSecurity> rememberMe) {
        rememberMe
                .key("hutech")
                .rememberMeCookieName("hutech")
                .tokenValiditySeconds(24 * 60 * 60)
                .userDetailsService(userDetailsService());
    }

}


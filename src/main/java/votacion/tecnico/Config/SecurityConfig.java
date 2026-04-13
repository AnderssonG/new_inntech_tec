package votacion.tecnico.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Configura la seguridad basica de la API usando autenticacion Basic Auth en todos los endpoints.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .httpBasic(httpBasic -> {
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
        // Fin de función securityFilterChain
    }

    // Registra en memoria el usuario de acceso a la API usando credenciales cargadas desde variables de entorno.
    @Bean
    public UserDetailsService userDetailsService(
            @Value("${APP_SECURITY_USERNAME}") String username,
            @Value("${APP_SECURITY_PASSWORD}") String password,
            PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("API_USER")
                .build();

        return new InMemoryUserDetailsManager(user);
        // Fin de función userDetailsService
    }

    // Crea el codificador de contraseñas para no almacenar la clave de acceso en texto plano dentro del contexto de seguridad.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // Fin de función passwordEncoder
    }
}

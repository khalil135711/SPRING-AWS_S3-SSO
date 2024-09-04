package largescaleit_demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ServiceConfiguration {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		  .csrf(c -> c.disable())
		  .oauth2Login(Customizer.withDefaults())
		  .authorizeHttpRequests( a -> a
				  .requestMatchers("/", "/login/**").authenticated()
				  .anyRequest().permitAll()
				  )

		  ;
		
		 return http.build();
	}
	
}
 
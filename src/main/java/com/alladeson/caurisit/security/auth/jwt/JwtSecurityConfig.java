package com.alladeson.caurisit.security.auth.jwt;

import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.security.core.AccountDetailsService;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//@Profile("jwt-auth")

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountDetailsService accountDetailsService;

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	AppConfig appConfig;

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(accountDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		List<String> origins = Arrays.stream(this.appConfig.getSecurityCorsOrigins().split(";")).distinct()
				.collect(Collectors.toList());
		for (String o : origins)
			if (StringUtils.hasText(o))
				config.addAllowedOrigin(o);
		List<String> methods = Arrays.stream(this.appConfig.getSecurityCorsMethods().split(";")).distinct()
				.collect(Collectors.toList());
		for (String m : methods)
			if (StringUtils.hasText(m))
				config.addAllowedMethod(m);
//        config.addAllowedOrigin(this.appConfig.getFrontEndBaseUrl());
//        config.addAllowedOrigin("*");
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("DELETE");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}

	@Bean
	public StrictHttpFirewall httpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowSemicolon(true);
		return firewall;
	}

	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().mvcMatchers(HttpMethod.OPTIONS, "/**");
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/**", "/swagger-resources/**", "/swagger-ui.html",
				"/webjars/**", "/api-docs/**");
		web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().ignoringAntMatchers("/chat/**").disable().exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html",
						"/**/*.css", "/**/*.js", "/error")
				.permitAll().antMatchers("/public/**")
				/*
				 * "/api/auth/signup", "/api/auth/changeddefaultpassword",
				 * "/api/auth/first-password", "/api/auth/checkedtoken",
				 * "/api/auth/newpassword", "/api/auth/passwordforgot",
				 * "/api/auth/phone-number/refresh-token", "/api/auth/phone-number/register",
				 * "/api/auth/phone-number/verification"
				 */
				.permitAll().anyRequest().authenticated();

		// Add our custom JWT com.techdigita.eshopping.security filter
//        http.addFilterBefore(new BasicAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

	}

//    @Bean
//    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        firewall.setAllowUrlEncodedSlash(true);
//        return firewall;
//    }
}

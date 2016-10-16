package seo.dale.practice.spring.security.prepost;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import seo.dale.practice.spring.security.prepost.service.HelloMessageService;
import seo.dale.practice.spring.security.prepost.service.MessageService;
import seo.dale.practice.spring.security.prepost.user.CustomUserDetails;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/test-method.html#test-method-withuserdetails
 * https://github.com/DaleSeo/spring-security/blob/master/test/src/test/java/org/springframework/security/test/context/showcase/WithUserDetailsTests.java
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WithUserDetailsTest.Config.class)
public class WithUserDetailsTest {

	@Autowired
	private MessageService messageService;

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void getMessageUnauthenticated() {
		messageService.getMessage();
	}

	@Test
	@WithUserDetails
	public void getMessageWithUserDetails() {
		String message = messageService.getMessage();
		assertThat(message).contains("user");
		assertThat(getPrincipal()).isInstanceOf(CustomUserDetails.class);
	}


	@Test
	@WithUserDetails("customUsername")
	public void getMessageWithUserDetailsCustomUsername() {
		String message = messageService.getMessage();
		assertThat(message).contains("customUsername");
		assertThat(getPrincipal()).isInstanceOf(CustomUserDetails.class);
	}

	@Test
	@WithUserDetails(value="customUsername", userDetailsServiceBeanName="myUserDetailsService")
	public void getMessageWithUserDetailsServiceBeanName() {
		String message = messageService.getMessage();
		assertThat(message).contains("customUsername");
		assertThat(getPrincipal()).isInstanceOf(CustomUserDetails.class);
	}

	private Object getPrincipal() {
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@ComponentScan(basePackageClasses = HelloMessageService.class)
	static class Config {
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.userDetailsService(myUserDetailsService());
		}

		@Bean
		public UserDetailsService myUserDetailsService() {
			return new CustomUserDetailsService();
		}
	}

	static class CustomUserDetailsService implements UserDetailsService {

		public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
			return new CustomUserDetails(username);
		}

	}

}
package com.lxg.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * 认证服务器配置
 * @author lxg
 *
 * 2017年2月17日上午10:50:04
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients.inMemory()
	        .withClient("my-trusted-client")//客户端ID
            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit", "client_credentials")
            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
            .scopes("read", "write", "trust")//授权用户的操作权限
            .secret("secret")//密码
            .accessTokenValiditySeconds(120)//token有效期为120秒
            .refreshTokenValiditySeconds(600)
			.redirectUris("http://example.com");//刷新token有效期为600秒
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore)
				.userApprovalHandler(userApprovalHandler)
				.authenticationManager(authenticationManager)
				//接收GET和POST
				.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer
				// 开启/oauth/token_key验证端口无权限访问
				.tokenKeyAccess("permitAll()")
				// 开启/oauth/check_token验证端口认证权限访问
				.checkTokenAccess("isAuthenticated()")
				.allowFormAuthenticationForClients();
		oauthServer.addTokenEndpointAuthenticationFilter(new CorsFilter(corsConfigurationSource()));
	}

	/**
	 *  处理跨域问题
	 * @return
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "HEAD", "DELETE", "OPTION"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.addExposedHeader("Authorization");
		configuration.addExposedHeader("Content-disposition");//文件下载消息头
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
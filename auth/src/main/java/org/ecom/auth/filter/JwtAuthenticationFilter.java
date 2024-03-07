package org.ecom.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecom.auth.model.ConnValidationResponse;
import org.ecom.auth.model.JwtAuthenticationModel;
import org.ecom.auth.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtHelper jwtHelper;

    private final AntPathRequestMatcher DEFAULT = new AntPathRequestMatcher("/api/auth/login", "POST");

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            JwtAuthenticationModel authModel = mapper.readValue(request.getInputStream(), JwtAuthenticationModel.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authModel.getUsername(), authModel.getPassword());
            return authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        List<? extends GrantedAuthority> authorities = authResult.getAuthorities().stream().toList();
        String token =  jwtHelper.generateToken(authResult.getName(), Map.of("authorities", authorities));

        response.addHeader("Authorization", String.format("Bearer %s", token));
        response.addHeader("Expiration", String.valueOf(30*60));

        ConnValidationResponse respModel = ConnValidationResponse.builder().status(HttpStatus.OK).username(authResult.getName()).token(token).authorities(authorities).methodType(HttpMethod.POST.name()).isAuthenticated(true).build();
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(mapper.writeValueAsBytes(respModel));
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return DEFAULT.matcher(request).isMatch();
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    private String getHexString() {
        return "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl("/api/auth/login");
    }
}

package com.example.demo.authorization;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import java.util.Collection;

@Component
public class TodoItemAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {
  @Override
  public boolean supports(ConfigAttribute attribute) {
    return true;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return true;
  }

  @Override
  public int vote(
      Authentication authentication,
      FilterInvocation filterInvocation,
      Collection<ConfigAttribute> attributes) {
    boolean isAdmin = hasAdminRole(authentication);
    String requestMethod = filterInvocation.getRequest().getMethod();

    if ("DELETE".equals(requestMethod) && !isAdmin) {
      return ACCESS_DENIED;
    }
    return ACCESS_GRANTED;
  }

  private boolean hasAdminRole(Authentication authentication) {
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    return authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
  }
}

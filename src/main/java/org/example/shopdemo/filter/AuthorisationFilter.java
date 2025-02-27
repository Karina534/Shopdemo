package org.example.shopdemo.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static org.example.shopdemo.utils.UrlPath.*;

@Slf4j
@WebFilter("/*")
public class AuthorisationFilter implements Filter {

    private boolean isStaticResource(String uri) {
        return uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif") || uri.endsWith(".svg") || uri.endsWith(".ico");
    }
    private final static Set<String> PUBLIC_PATH = Set.of(LOGIN, REGISTRATION, BOOKS, BOOK_PAGE, PICTURES);
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var uri = ((HttpServletRequest) servletRequest).getRequestURI();

        if (isStaticResource(uri)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        
        log.info("Start filtering is uri: {} for not authorization users allowed.", uri);
        if (isPublicPath(uri) || isUserLoggedIn(servletRequest) || isInCookie(servletRequest)){
            log.info("Uri: {} is allowed for users.", uri);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            log.info("Uri: {} isn't allowed for not authorized users.", uri);
            ((HttpServletResponse) servletResponse).sendRedirect(LOGIN);
        }
    }

    private boolean isUserLoggedIn(ServletRequest servletRequest){
        var consumer = ((HttpServletRequest) servletRequest).getSession().getAttribute("consumer");
        var admin = ((HttpServletRequest) servletRequest).getSession().getAttribute("admin");
        return consumer != null || admin != null;
    }

    private boolean isInCookie (ServletRequest servletRequest){
        var consumer = Arrays.stream(((HttpServletRequest) servletRequest).getCookies())
                .anyMatch(cookie -> cookie.getName().equals("consumer"));
        var admin = Arrays.stream(((HttpServletRequest) servletRequest).getCookies())
                .anyMatch(cookie -> cookie.getName().equals("admin"));
        return consumer || admin;
    }

    private boolean isPublicPath(String uri){
        return PUBLIC_PATH.stream().anyMatch(e -> uri.startsWith(e));
    }
}

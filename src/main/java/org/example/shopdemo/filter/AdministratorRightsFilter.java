package org.example.shopdemo.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;

import static org.example.shopdemo.utils.UrlPath.LOGIN;

@Slf4j
@WebFilter(urlPatterns = {UrlPath.MAKE_BOOK, UrlPath.BOOK_REDUCTION, UrlPath.DELETE_BOOK})
public class AdministratorRightsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var admin = ((HttpServletRequest) servletRequest).getSession().getAttribute("admin");
        log.info("Start checking if user is admin to allow him to go to makeBook page. Is admin: {}", admin);
        if (admin != null){
            log.info("User was allowed to go to makeBook page. Is admin: {}", admin);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            log.info("User wasn't allowed to go to makeBook page and redirected to login page.");
            ((HttpServletResponse) servletResponse).sendRedirect(LOGIN);
        }
    }
}

package org.example.shopdemo.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.service.AdminsService;
import org.example.shopdemo.service.ConsumersService;

import java.io.IOException;

@Slf4j
@WebFilter("/*")
public class SessionRestoreByCookieFilter implements Filter {
    private static final String CONSUMER = "consumerId";
    private static final String ADMIN = "adminId";
    private final ConsumersService consumersService = ConsumersService.getInstance();
    private final AdminsService adminsService = AdminsService.getInstance();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();

        if (session.getAttribute(CONSUMER) == null && session.getAttribute(ADMIN) == null){
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (ADMIN.equals(cookie.getName())) {
                        Long adminId = Long.parseLong(cookie.getValue());
                        if (adminsService.getAdminById(adminId).isPresent()) { // Проверь, существует ли админ
                            session.setAttribute("admin", adminId);
                        }
                        log.info("Set cookie adminId to session.");
                    } else if (CONSUMER.equals(cookie.getName())) {
                        Long consumerId = Long.parseLong(cookie.getValue());
                        if (consumersService.getConsumerById(consumerId).isPresent()) { // Проверь, существует ли пользователь
                            session.setAttribute("consumer", consumerId);
                        }
                        log.info("Set cookie consumerId to session.");
                    }
                }
            }
        }

        filterChain.doFilter(req, resp);
    }
}

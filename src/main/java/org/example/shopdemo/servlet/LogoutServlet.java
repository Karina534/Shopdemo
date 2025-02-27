package org.example.shopdemo.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@WebServlet(UrlPath.LOGOUT)
public class LogoutServlet extends HttpServlet {
    private static final String CONSUMER = "consumerId";
    private static final String ADMIN = "adminId";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("User started logout");
        req.getSession().invalidate();
        log.info("User was redirected to login after logout");

        if (Arrays.stream(req.getCookies()).anyMatch(cookie -> cookie.getName().equals(CONSUMER))){
            var id = Arrays.stream(req.getCookies()).anyMatch(cookie -> cookie.getName().equals("consumerId"));
            Cookie cookie = new Cookie(CONSUMER, "");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
            log.info("Cookie for consumer id: {} was deleted.", id);
        } else if (Arrays.stream(req.getCookies()).anyMatch(cookie -> cookie.getName().equals(ADMIN))) {
            var id = Arrays.stream(req.getCookies()).anyMatch(cookie -> cookie.getName().equals("adminId"));
            Cookie cookie = new Cookie(ADMIN, "");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
            log.info("Cookie for admin id: {} was deleted.", id);
        } else {
            log.error("Cookie wasn't deleted, as there wasn't name consumerId or adminId");
        }

        log.info("User was redirected to login after logout");
        resp.sendRedirect(UrlPath.LOGIN);
    }
}

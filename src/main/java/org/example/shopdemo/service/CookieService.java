package org.example.shopdemo.service;

import jakarta.servlet.http.Cookie;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.exception.CookieCreationException;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieService {
    private final static CookieService INSTANCE = new CookieService();
    private static final String CONSUMER = "consumerId";
    private static final String ADMIN = "adminId";

    public Cookie saveConsumer(Cookie[] cookies, ConsumerDto consumerDto) throws CookieCreationException {
        LogService.logInfo("Starting create cookie", "for consumer id: %s", consumerDto.getConsumerId());

        if (cookies == null || Arrays.stream(cookies).noneMatch(cook -> CONSUMER.equals(cook.getName()))){
            Cookie cookie = new Cookie(CONSUMER,String.valueOf(consumerDto.getConsumerId()));
            cookie.setMaxAge(60 * 6); // два часа
            cookie.setPath("/");
            LogService.logInfo("Cookie have been made successfully.", "For consumer id: %s",
                    consumerDto.getConsumerId());
            return cookie;
        }else {
            LogService.logError("Cookie already exists", "for consumer id: %s", consumerDto.getConsumerId());
            throw new CookieCreationException("Cookie already exists for consumer.");
        }
    }

    public Cookie saveAdmin(Cookie[] cookies, AdminsDto adminsDto) throws CookieCreationException {
        LogService.logInfo("Starting create cookie", "for admin id: %s", adminsDto.getAdminId());
        if (cookies == null || Arrays.stream(cookies).noneMatch(cook -> cook.getName().equals(ADMIN))) {
            Cookie cookie = new Cookie(ADMIN, String.valueOf(adminsDto.getAdminId()));
            cookie.setMaxAge(60 * 6);
            cookie.setPath("/");
            LogService.logInfo("Cookie have been made successfully.", "For admin id: %s",
                    adminsDto.getAdminId());
            return cookie;
        } else {
            LogService.logError("Cookie already exists", "for admin id: %s", adminsDto.getAdminId());
            throw new CookieCreationException("Cookie already exists for admin.");
        }
    }

    public Long getConsumerIdFromCookie(Cookie[] cookies){
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("consumerId"))
                .map(Cookie::getValue)
                .map(Long::valueOf)
                .findFirst()
                .orElse(null);
    }

    public static CookieService getInstance(){
        return INSTANCE;
    }
}

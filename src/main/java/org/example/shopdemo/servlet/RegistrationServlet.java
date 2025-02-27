package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Admins;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.entity.Role;
import org.example.shopdemo.exception.CookieCreationException;
import org.example.shopdemo.exception.ValidationException;
import org.example.shopdemo.service.*;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(UrlPath.REGISTRATION)
public class RegistrationServlet extends HttpServlet {
    private final ConsumersService consumersService = ConsumersService.getInstance();
    private final AdminsService adminsService = AdminsService.getInstance();
    private final CookieService cookieService = CookieService.getInstance();
    private final BasketService basketService = BasketService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("roles", Role.values());
        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.REGISTRATION)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            LogService.logInfo("Start login request.", "for User email: %s", req.getParameter("email"));

            if (req.getParameter("individualNum").isBlank()){
                ConsumerDto consumerDto = ConsumerDto
                        .builder()
                        .consumerName(req.getParameter("consumerName"))
                        .surname(req.getParameter("surname"))
                        .email(req.getParameter("email"))
                        .password(req.getParameter("password"))
                        .telephone(req.getParameter("telephone"))
                        .role(req.getParameter("role"))
                        .build();

                try {
                    // Делаем валидацию формы и сохраняем в базе пользователя
                    LogService.logInfo("Consumer  sent to validation.", "Consumer email: %s",
                            consumerDto.getEmail());
                    Consumers consumers = consumersService.saveRegistrationConsumer(consumerDto);

                    // Устанавливаем id для пользователя
                    consumerDto.setConsumerId(consumers.getConsumerId());
                    LogService.logInfo("Set id to consumer", "with email %s, id = %s",
                            consumerDto.getEmail(), consumerDto.getConsumerId());

                    // Создаем куки с id пользователя
                    LogService.logInfo("Consumer sent to cookieService.", "Consumer id: %s",
                            consumerDto.getEmail());
                    try {
                        var cookie = cookieService.saveConsumer(req.getCookies(), consumerDto);
                        resp.addCookie(cookie);
                        LogService.logInfo("Cookie successfully added to response.", "Consumer id: %s",
                                consumerDto.getConsumerId());

                    } catch (CookieCreationException e){
                        LogService.logWarn("Cookie creation failed, it was already existed.", "Consumer id: %s",
                                consumerDto.getConsumerId(), e);
                    }

                    req.getSession().setAttribute("consumer", consumerDto);

                     // Создаем корзину для покупателя
                     boolean isCreatedBasket = basketService.createBasket(consumerDto);
                     if (!isCreatedBasket){
                         LogService.logError("Creation basket for Consumer during registration failed.",
                                 "Consumer surname: %s", consumerDto.getSurname());

                         // Удаляем покупателя
                         consumersService.deleteConsumer(consumerDto.getConsumerId());
                         resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sorry, try again later.");
                     }

                    LogService.logInfo("Redirecting to personal account after successful login.",
                            "Consumer id: %s", consumerDto.getConsumerId());
                    resp.sendRedirect(UrlPath.PERSONAL_ACCOUNT);

                } catch (ValidationException e){
                    req.setAttribute("errors", e.getErrors());
                    LogService.logDebug("Consumer failed login validation.", "Consumer id: %s, consumer parameters: %s",
                            consumerDto.getConsumerId(), sanitizedParams(req.getParameterMap()));
                    doGet(req, resp);
                }
            } else {
                AdminsDto adminsDto = AdminsDto
                        .builder()
                        .name(req.getParameter("admin_name"))
                        .surname(req.getParameter("surname"))
                        .email(req.getParameter("email"))
                        .password(req.getParameter("password"))
                        .telephone(req.getParameter("telephone"))
                        .role(req.getParameter("role"))
                        .individualNum(Long.valueOf(req.getParameter("individualNum")))
                        .build();

                try {
                    // Делаем валидацию формы и сохраняем в базе пользователя
                    LogService.logInfo("Admin sent to validation.", "Admin email: %s",
                            adminsDto.getEmail());
                    Admins admins = adminsService.saveRegistrationAdmin(adminsDto);

                    // Устанавливаем id для админа
                    adminsDto.setAdminId(admins.getAdminId());
                    LogService.logInfo("Set id to admin", "with email %s, id = %s",
                            adminsDto.getEmail(), adminsDto.getAdminId());

                    // Создаем куки с id админа
                    LogService.logInfo("Admin sent to cookieService.", "Admin id: %s",
                            adminsDto.getEmail());
                    try {
                        var cookie = cookieService.saveAdmin(req.getCookies(), adminsDto);
                        resp.addCookie(cookie);
                        LogService.logInfo("Cookie successfully added to response.", "Admin id: %s",
                                adminsDto.getAdminId());
                    } catch (CookieCreationException e){
                        LogService.logWarn("Cookie creation failed, it was already existed.", "Admin id: %s",
                                adminsDto.getAdminId(), e);
                    }

                    req.getSession().setAttribute("admin", adminsDto);

                    LogService.logInfo("Redirecting to personal account after successful login.",
                            "Admin id: %s", adminsDto.getAdminId());
                    resp.sendRedirect(UrlPath.PERSONAL_ACCOUNT);

                } catch (ValidationException e){
                    req.setAttribute("errors", e.getErrors());
                    LogService.logDebug("Admin failed login validation.", "Admin id: %s, admin parameters: %s",
                            adminsDto.getAdminId(), sanitizedParams(req.getParameterMap()));
                    doGet(req, resp);
                }
            }

        } catch (Exception e){
            LogService.logDebug("Unexpected error during users login.", "errors: %s", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            MDC.remove("requestId");
        }
    }

    // Повторяющийся код, такой же есть в логин
    private Map<String, String[]> sanitizedParams(Map<String, String[]> parameterMap) {
        Map<String, String[]> sanitizedParams = new HashMap<>(parameterMap);
        sanitizedParams.remove("password");
        return sanitizedParams;
    }
}

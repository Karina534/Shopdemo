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
import org.example.shopdemo.exception.EntityNotFoundException;
import org.example.shopdemo.exception.ValidationException;
import org.example.shopdemo.service.AdminsService;
import org.example.shopdemo.service.ConsumersService;
import org.example.shopdemo.service.CookieService;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(UrlPath.LOGIN)
public class LoginServlet extends HttpServlet {
    private final ConsumersService consumersService = ConsumersService.getInstance();
    private final AdminsService adminsService = AdminsService.getInstance();
    private final CookieService cookieService = CookieService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("roles", Role.values());
        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.LOGIN)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            LogService.logInfo("Start login request.", "for User email: %s", req.getParameter("email"));

            if (req.getParameter("role").equals("U")){
                ConsumerDto consumerDto = ConsumerDto
                        .builder()
                        .email(req.getParameter("email"))
                        .password(req.getParameter("password"))
                        .role(req.getParameter("role"))
                        .build();

                try {
                    // проверка формы на валидацию и нахождение в бд такого пользователя
                    LogService.logInfo("Sent to validation and finding in bd.", "Consumer email: %s",
                            consumerDto.getEmail());
                    boolean checking = consumersService.checkLogin(consumerDto);

                    if (checking) {
                        // Устанавливаем id для Dto
                        Consumers consumers = consumersService.getConsumerFromDto(consumerDto);
                        consumerDto.setConsumerId(consumers.getConsumerId());
                        LogService.logInfo("Set id to consumer", "with email %s, id = %s",
                                consumerDto.getEmail(), consumerDto.getConsumerId());

                        req.getSession().setAttribute("consumer", consumerDto.getConsumerId());

                        // Создаем куки с id пользователя
                        LogService.logInfo("Sent to cookieService.", "Consumer email: %s",
                                consumerDto.getEmail());
                        try{
                            var cookie = cookieService.saveConsumer(req.getCookies(), consumerDto);
                            resp.addCookie(cookie);
                            LogService.logInfo("Cookie successfully added to response.", "Consumer id: %s",
                                    consumerDto.getConsumerId());

                        } catch (CookieCreationException e){
                            LogService.logWarn("Cookie creation failed, it was already existed.", "Consumer id: %s",
                                    consumerDto.getConsumerId(), e);
//                            throw new RuntimeException("Cookie creation failed.", e);
                        }

                        LogService.logInfo("Redirecting to personal account after successful login.",
                                "Consumer id: %s", consumerDto.getConsumerId());
                        resp.sendRedirect(UrlPath.PERSONAL_ACCOUNT);
                    }

                }catch (ValidationException e){
                    req.setAttribute("errors", e.getErrors());
                    LogService.logDebug("Consumer failed login validation.", "Consumer id: %s, consumer parameters: %s",
                            consumerDto.getConsumerId(), sanitizedParams(req.getParameterMap()));
                    doGet(req, resp);
                }

            } else if (req.getParameter("role").equals("A")){
                AdminsDto adminsDto = AdminsDto
                        .builder()
                        .email(req.getParameter("email"))
                        .password(req.getParameter("password"))
                        .role(req.getParameter("role"))
                        .individualNum(Long.valueOf(req.getParameter("individualNum")))
                        .build();

                try {
                    // проверка формы на валидацию и нахождение в бд такого админа
                    LogService.logInfo("Sent to validation and finding in bd.", "Admin email: %s",
                            adminsDto.getEmail());
                    boolean checking = adminsService.checkLogin(adminsDto);

                    if (checking) {
                        // Устанавливаем id для Dto
                        Admins admin = adminsService.getAdminFromDto(adminsDto);
                        adminsDto.setAdminId(admin.getAdminId());
                        LogService.logInfo("Set id to admin", "with email %s, id = %s",
                                adminsDto.getEmail(), adminsDto.getAdminId());

                        req.getSession().setAttribute("admin", adminsDto.getAdminId());

                        // Создаем куки с id админа
                        LogService.logInfo("Sent to cookieService.", "Admin email: %s",
                                adminsDto.getEmail());
                        try {
                            var cookie = cookieService.saveAdmin(req.getCookies(), adminsDto);
                            resp.addCookie(cookie);
                            LogService.logInfo("Cookie successfully added to response.", "Admin id: %s",
                                    adminsDto.getAdminId());
                        } catch (CookieCreationException e){
                            LogService.logWarn("Cookie creation failed, it was already existed.", "Admin id: %s",
                                    adminsDto.getAdminId(), e);
//                            throw new RuntimeException("Failed to create cookie.", e);
                        }

                        LogService.logInfo("Redirecting to personal account after successful login.",
                                "Admin id: %s", adminsDto.getAdminId());
                        resp.sendRedirect(UrlPath.PERSONAL_ACCOUNT);
                    }

                }catch (ValidationException e){
                    req.setAttribute("errors", e.getErrors());
                    LogService.logDebug("Admin failed login validation.", "Admin id: %s, admin parameters: %s",
                            adminsDto.getAdminId(), sanitizedParams(req.getParameterMap()));
                    doGet(req, resp);
                }

            } else {
                req.setAttribute("errors", Collections.singletonList(new RuntimeException("Unknown role")));
                LogService.logWarn("User sent login form with unknown role.", "role = %s", req.getParameter("role"), new Throwable("Unknown role"));
                doGet(req, resp);
            }

        } catch (Exception e){
            LogService.logDebug("Unexpected error during users login.", "errors: %s", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            MDC.remove("requestId");
        }
    }

    private Map<String, String[]> sanitizedParams(Map<String, String[]> parameterMap) {
        Map<String, String[]> sanitizedParams = new HashMap<>(parameterMap);
        sanitizedParams.remove("password");
        return sanitizedParams;
    }
}

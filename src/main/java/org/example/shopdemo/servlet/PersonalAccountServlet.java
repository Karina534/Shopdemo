package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.shopdemo.dao.ConsumersDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.service.AdminsService;
import org.example.shopdemo.service.ConsumersService;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@WebServlet(UrlPath.PERSONAL_ACCOUNT)
public class PersonalAccountServlet extends HttpServlet {
    private final ConsumersService consumersService = ConsumersService.getInstance();
    private final AdminsService adminsService = AdminsService.getInstance();
    private static final String CONSUMER = "consumerId";
    private static final String ADMIN = "adminId";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            LogService.logInfo("Start open personal account.", "");

            // Определяем consumer или admin и получаем id
            var session = req.getSession();
            Cookie[] cookies = req.getCookies();
            Long consumerId = null;
            Long adminId = null;
            if (session.getAttribute("consumer") != null) {
                consumerId = (Long) session.getAttribute("consumer");
                LogService.logInfo("ConsumerId was found in session for opening personal account page.", "ConsumerId: ", consumerId);
            } else if (session.getAttribute("admin") != null) {
                adminId = (Long) session.getAttribute("admin");
                LogService.logInfo("AdminId was found in session for opening personal account page.", "AdminId: ", adminId);
            } else if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(CONSUMER)) {
                        consumerId = Long.valueOf(cookie.getValue());
                        LogService.logInfo("ConsumerId was found in cookie for opening personal account page.", "ConsumerId: ", consumerId);
                    } else if (cookie.getName().equals(ADMIN)) {
                        adminId = Long.valueOf(cookie.getValue());
                        LogService.logInfo("AdminId was found in cookie for opening personal account page.", "AdminId: ", adminId);
                    }
                }
            }

            // Получаем объект по id, передаем пользователя в JSP
            if (consumerId != null) {
                Optional<ConsumerDto> consumerDto = consumersService.getConsumerById(consumerId);
                if (consumerDto.isEmpty()) {
                    LogService.logDebug("Consumer wasn't found in consumersService by Id for opening personal" +
                            " account page account!", "ConsumerId: ", consumerId);
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not found consumer by id.");
                } else {
                    LogService.logInfo("Consumer was successfully found for opening personal account page.",
                            "ConsumerId: ", consumerId);
                    req.setAttribute("consumer", consumerDto.get());
                }
            } else if (adminId != null) {
                Optional<AdminsDto> adminsDto = adminsService.getAdminById(adminId);
                if (adminsDto.isEmpty()) {
                    LogService.logDebug("Admin wasn't found in adminsService by Id for opening personal" +
                            " account!", "AdminId: ", adminId);
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not found admin by id.");
                } else {
                    LogService.logInfo("Admin was successfully found for opening personal account page.",
                            "AdminId: ", adminId);
                    req.setAttribute("admin", adminsDto.get());
                }
            } else {
                LogService.logInfo("ConsumerId and AdminId wasn't found for opening personal account page.", "");
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not found user.");
            }

            req.getRequestDispatcher(JSPHelper.getPath(UrlPath.PERSONAL_ACCOUNT)).forward(req, resp);
        }catch (Exception e){
            LogService.logDebug("Unexpected error during users login.", "errors: %s", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            MDC.remove("requestId");
        }
    }
}

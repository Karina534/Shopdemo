package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.Orders;
import org.example.shopdemo.service.CookieService;
import org.example.shopdemo.service.OrderService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(UrlPath.ACTIVE_ORDERS)
public class ActiveOrdersServlet extends HttpServlet {
    private final CookieService cookieService = CookieService.getInstance();
    private final OrderService orderService = OrderService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            log.info("Start getting consumer id from cookie");
            var consumerId = cookieService.getConsumerIdFromCookie(req.getCookies());
            log.info("Got consumer id fro, cookie: {}", consumerId);
            log.info("Start finding all orders for consumer id: {}", consumerId);
            List<Orders> orders = orderService.findAllByConsumerId(consumerId);
            log.info("Finding all orders for consumer id: {} finished. List size: {}", consumerId, orders.size());

            req.setAttribute("orders", orders);
            log.info("All ordersBooks for consumer id: {} successfully found for active orders. Opening page.",  consumerId);
            req.getRequestDispatcher(JSPHelper.getPath(UrlPath.ACTIVE_ORDERS)).forward(req, resp);

        } catch (Exception e){
            log.error("Unknown error during opening active orders servlet get response: {}.", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}

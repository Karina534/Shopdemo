package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.OrdersDto;
import org.example.shopdemo.entity.Statuses;
import org.example.shopdemo.service.OrderService;
import org.example.shopdemo.service.StatusesService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;
import org.example.shopdemo.validator.Error;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(UrlPath.ORDER_DETAILS)
public class OrderDetailsServlet extends HttpServlet {
    private final OrderService orderService = OrderService.getInstance();
    private final StatusesService statusesService = StatusesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Start opening order details page");
        long orderId = Long.parseLong(req.getParameter("orderId"));
        log.info("Got orderId = {} for order details page.", orderId);

        log.info("Start finding orderDto by id = {}, using orderService.", orderId);
        OrdersDto orderDto = orderService.getByOrderId(orderId);
        log.info("OrderDto successfully found by id = {} for order details page.", orderId);

        log.info("Finding statuses for orders");
        List<Statuses> statuses = statusesService.findAll();
        log.info("Finding statuses for order successfully found. List size: {}", statuses.size());

        log.info("Setting orderDto to request and sending admin to order details page with order id: {}.", orderId);
        req.setAttribute("orderDto", orderDto);
        req.setAttribute("statuses", statuses);
        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.ORDER_DETAILS) + "?orderId=" + orderId).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Start updating order details post page");
        long orderId = Long.parseLong(req.getParameter("orderId"));
        Integer statusId = Integer.parseInt(req.getParameter("statusId"));
        log.info("Got orderId = {} and status id = {} for order details post page.", orderId, statusId);

        log.info("Start updating order with id = {} using orderService", orderId);
        boolean isUpdated = orderService.updateOrderById(orderId, statusId);
        if (isUpdated) {
            log.info("Order with id = {} successfully updating with status id {}", orderId, statusId);

        } else {
            log.debug("Order with id = {} wasn't updated with status id {}", orderId, statusId);
            req.setAttribute("errors", Error.of("order.notUpdated","Sorry, status for this order wasn't updated. Try later."));
        }

        log.info("Reloading orderDto from database after update...");
        OrdersDto updatedOrderDto = orderService.getByOrderId(orderId);

        req.setAttribute("orderDto", updatedOrderDto);
        req.setAttribute("statuses", statusesService.findAll());

        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.ORDER_DETAILS) + "?orderId=" + orderId).forward(req, resp);
    }
}

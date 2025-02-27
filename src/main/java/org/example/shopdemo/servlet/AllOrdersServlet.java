package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.OrdersDto;
import org.example.shopdemo.service.OrderService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(UrlPath.ALL_ORDERS)
public class AllOrdersServlet extends HttpServlet {
    private final OrderService orderService = OrderService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Start open all orders page. Finding all orders.");
        List<OrdersDto> ordersDtos = orderService.findAllOrders();
        log.info("All orders successfully found for all orders page.");

        // Добавить валюту

        req.setAttribute("orders", ordersDtos);
        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.ALL_ORDERS)).forward(req, resp);
    }
}

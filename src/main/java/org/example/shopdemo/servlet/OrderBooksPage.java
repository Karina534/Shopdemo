package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.service.OrdersBooksService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;
import org.example.shopdemo.entity.OrdersBooks;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet(UrlPath.ORDER_BOOKS_PAGE)
public class OrderBooksPage extends HttpServlet {
    private final OrdersBooksService ordersBooksService = OrdersBooksService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long orderId = Long.valueOf(req.getParameter("orderId"));
        log.info("Got order Id: {} from request for opening order books page", orderId);

        List<OrdersBooks> orderBooks = ordersBooksService.findAllByOrderId(orderId);

        req.setAttribute("orderBooks", orderBooks);
        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.ORDER_BOOKS_PAGE) + "?orderId=" + orderId).forward(req, resp);
    }
}

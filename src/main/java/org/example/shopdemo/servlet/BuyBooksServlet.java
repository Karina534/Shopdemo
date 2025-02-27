package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.OrdersDto;
import org.example.shopdemo.entity.BasketBooks;
import org.example.shopdemo.entity.Orders;
import org.example.shopdemo.entity.OrdersBooks;
import org.example.shopdemo.service.BasketBooksService;
import org.example.shopdemo.service.BooksService;
import org.example.shopdemo.service.OrderService;
import org.example.shopdemo.service.OrdersBooksService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@WebServlet(UrlPath.BUY_BOOKS)
public class BuyBooksServlet extends HttpServlet {
    private final BasketBooksService basketBooksService = BasketBooksService.getInstance();
    private final OrderService orderService = OrderService.getInstance();
    private final OrdersBooksService ordersBooksService = OrdersBooksService.getInstance();
    private final BooksService booksService = BooksService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long basketId = Long.valueOf(req.getParameter("basketId"));

            log.info("Try to find list of basketBooks for buying page. Basket id: {}", basketId);
            List<BasketBooks> basketBooks = basketBooksService.findByBasketId(basketId);

            log.info("Count full cost of books in basket. Basket id: {}", basketId);
            BigDecimal fullCost = BigDecimal.valueOf(0L);
            for (BasketBooks books: basketBooks){
                fullCost = fullCost.add(books.getBook().getPrice().multiply(BigDecimal.valueOf(books.getQuantity())));
            }

            req.setAttribute("basketBooks", basketBooks);
            req.setAttribute("fullCost", fullCost);

            req.getRequestDispatcher(JSPHelper.getPath(UrlPath.BUY_BOOKS)).forward(req, resp);
        } catch (Exception e){
            log.error("Unknown error during buy books servlet get response: {}.", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            String fullCostParam = req.getParameter("fullCost");
            fullCostParam = fullCostParam.trim().replace(",", ".");
            BigDecimal fullCost = new BigDecimal(fullCostParam);

            Long consumerId = Long.valueOf(req.getParameter("consumerId").trim());
            Long basketId = Long.valueOf(req.getParameter("basketId").trim());
            String address = req.getParameter("address");
            log.info("Took fullCost = {}, consumerId = {}, basketId = {}, address = {} from buy books respons.",
                    fullCost, consumerId, basketId, address);

            OrdersDto ordersDto = OrdersDto.builder()
                    .totalPrice(fullCost)
                    .consumerId(consumerId)
                    .address(address).build();
            log.info("Sent orderDto to orderService for saving.");
            Orders savedOrder = orderService.saveOrder(ordersDto);

            // Найти лист basketBooks по basketid, для каждой книги сохранить в ordersbooks
            log.info("Start finding and saving list of BasketBooks by basket id: {}", basketId);
            List<BasketBooks> basketBooks = basketBooksService.findByBasketId(basketId);
            for (BasketBooks basketBook: basketBooks){
                OrdersBooks ordersBook = new OrdersBooks(null, savedOrder, basketBook.getBook(), basketBook.getQuantity());
                ordersBooksService.saveOrderBook(ordersBook);
                log.info("OrderBook with order id {} and book id {} saved successfully.", ordersBook.getOrderBookId(), basketBook.getBook().getBookId());

                booksService.decreaseRemains(basketBook.getBook());
                log.info("Book remains was decreased for book id: {}", basketBook.getBook().getBookId());
            }

            // Очистить корзину
            log.info("Start deleting all books, that was in basket. Basket id: {}", basketId);
            boolean isDeleted = basketBooksService.deleteAllBooksByBasketId(basketId);
            log.info("Deleting all books from basket with id {} successfully finished.", basketId);

            log.info("Buying books and deleting basket successfully finished. Consumer redirected to basket page.");
            resp.sendRedirect(UrlPath.ACTIVE_ORDERS);
        } catch (Exception e){
            log.error("Unknown error during buy books servlet post response: {}.", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}

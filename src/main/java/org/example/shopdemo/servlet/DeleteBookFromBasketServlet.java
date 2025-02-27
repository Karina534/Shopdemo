package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.service.BasketBooksService;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;

@Slf4j
@WebServlet(UrlPath.DELETE_BOOK_FROM_BASKET)
public class DeleteBookFromBasketServlet extends HttpServlet {
    private final static BasketBooksService basketBookService = BasketBooksService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long bookId = Long.valueOf(req.getParameter("bookId"));
            Long basketId = Long.valueOf(req.getParameter("basketId"));
            Integer quantity = Integer.parseInt(req.getParameter("quantity"));

            boolean isSuccess;
            if (quantity < 2){
                isSuccess = basketBookService.deleteBook(basketId, bookId);
            } else {
                isSuccess = basketBookService.decreaseBookQuantity(basketId, bookId, quantity);
            }

            if (!isSuccess){
                log.error("Book wasn't deleted for basketBook with such basketID: {} and BookID: {} and quantity: {}"
                        , basketId, bookId, quantity);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sorry, we can't delete book, try later");
                return;
            }

            log.info("Book for basketId: {}, bookId: {} and quantity: {} was successfully deleted." +
                     " User was redirected to Basket page.", basketId, bookId, quantity);
            resp.sendRedirect(UrlPath.BASKET);

        } catch (Exception e){
            log.error("Unknown error during delete book from basket servlet get response: {}.", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}

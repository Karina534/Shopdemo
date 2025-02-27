package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.exception.DaoException;
import org.example.shopdemo.exception.EntityNotFoundException;
import org.example.shopdemo.service.BasketBooksService;
import org.example.shopdemo.service.BooksService;
import org.example.shopdemo.service.CookieService;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.UrlPath;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@WebServlet(UrlPath.ADD_BOOK_IN_BASKET)
public class AddBookInBasketServlet extends HttpServlet {
    private final BasketBooksService basketBooksService = BasketBooksService.getInstance();
    private final CookieService cookieService = CookieService.getInstance();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);

            Long bookId = Long.valueOf(req.getParameter("bookId"));
            LogService.logInfo("Start add book to basket.", "Book id: %s", bookId);

            var consumerId = cookieService.getConsumerIdFromCookie(req.getCookies());

            if (consumerId == null){
                LogService.logInfo("Not authorized used tried to add book in basket and was redirected" +
                                   " to login page", "Consumer id: %s", null);
                resp.sendRedirect(UrlPath.LOGIN);
                return;
            }

            LogService.logInfo("Consumer is authorized, trying to add book to his basket.",
                    "Consumer id: %s", consumerId);
            try {
                basketBooksService.saveBook(consumerId, bookId);
                LogService.logInfo("Book was successfully added to basket books. Consumer was redirected to books page",
                            "Book id: %s, Consumer id: %s", bookId, consumerId);
                resp.sendRedirect(UrlPath.BOOKS);

            } catch (EntityNotFoundException| DaoException e){
                LogService.logError("Consumer can't add book to basketBooks.",
                        "BookID: %s", "ConsumerID: %s", "Errors: %s", bookId, consumerId, e.getMessage());
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
            }

        } catch (Exception e){
            LogService.logDebug("Unexpected error during adding book to basket.", "errors: %s", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            MDC.remove("requestId");
        }
    }
}

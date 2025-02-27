package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.dto.BooksWithImgDto;
import org.example.shopdemo.entity.BasketBooks;
import org.example.shopdemo.service.BasketBooksService;
import org.example.shopdemo.service.BasketService;
import org.example.shopdemo.service.BooksImagesService;
import org.example.shopdemo.service.CookieService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@WebServlet(UrlPath.BASKET)
public class BasketServlet extends HttpServlet {
    private final BasketService basketService = BasketService.getInstance();
    private final BasketBooksService basketBooksService = BasketBooksService.getInstance();
    private final BooksImagesService booksImagesService = BooksImagesService.getInstance();
    private final CookieService cookieService = CookieService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var consumerId = cookieService.getConsumerIdFromCookie(req.getCookies());
            log.info("Start trying to open basket page for consumer id: {}", consumerId);

            if (consumerId == null){
                log.info("Can't open basket page for consumer as his id is null. User was redirected to login page.");
                resp.sendRedirect(UrlPath.LOGIN);
                return;
            }

            log.info("Try find basketId for consumer id: {}", consumerId);
            Long basketId = basketService.findByConsumerId(consumerId);
            log.info("Try find list basketBooks for basket id: {}", basketId);
            List<BasketBooks> basketBooks = basketBooksService.findByBasketId(basketId);
            log.info("Try find books list in list basketBooks size: {}", basketBooks.size());
            List<BooksDto> books = basketBooksService.getBooksFromBasketBooks(basketBooks);

            log.info("Try find books with img list for list books size: {}", books.size());
            List<BooksWithImgDto> booksWithImg = new ArrayList<>(books.size());
            for (BooksDto book : books) {
                var img = booksImagesService.getFirstImageForBook(book.getBooksId());
                if (img == null) {
                    img = req.getContextPath() + "/uploads/catDefault.jpg";
                }
                booksWithImg.add(BooksWithImgDto
                        .builder()
                        .booksId(book.getBooksId())
                        .author(book.getAuthor())
                        .title(book.getTitle())
                        .description(book.getDescription())
                        .price(book.getPrice())
                        .currencyId(book.getCurrencyId())
                        .remains(book.getRemains())
                        .publisherId(book.getPublisherId())
                        .imgUrl(img)
                        .quantity(book.getQuantity())
                        .build());
            }

            log.info("Find books with img list for list books size: {} successfully finished." +
                    " Consumer sent to basket page.", booksWithImg.size());
            req.setAttribute("basketId", basketId);
            req.setAttribute("books", booksWithImg);
            req.getRequestDispatcher(JSPHelper.getPath(UrlPath.BASKET)).forward(req, resp);
        } catch (Exception e){
            log.error("Unknown error during basket servlet get response: {}.", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}

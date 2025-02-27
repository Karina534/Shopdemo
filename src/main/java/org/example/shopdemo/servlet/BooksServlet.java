package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.dto.BooksWithImgDto;
import org.example.shopdemo.service.BooksImagesService;
import org.example.shopdemo.service.BooksService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;
import java.util.*;

@Slf4j
@WebServlet(UrlPath.BOOKS)
public class BooksServlet extends HttpServlet {
    private final BooksService booksService = BooksService.getInstance();
    private final BooksImagesService booksImagesService = BooksImagesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        try {
            log.info("Starting find all books from bd using booksService.");
            var preBooks = booksService.findAll();
            var books = booksService.filterByRemains(preBooks);

            log.info("Starting find one img for each book using booksImagesService.");
            List<BooksWithImgDto> booksWithImg = new ArrayList<>();
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
                        .build());
            }

            req.setAttribute("books", booksWithImg);
            log.info("Finding books and img finished successfully.");
            req.getRequestDispatcher(JSPHelper.getPath(UrlPath.BOOKS)).forward(req, resp);
//        } catch (Exception e){
//            log.error("Unknown error during book servlet get response: {}.", e.getMessage());
//            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
//        }
    }
}

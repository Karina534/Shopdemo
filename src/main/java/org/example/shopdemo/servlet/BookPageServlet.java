package org.example.shopdemo.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.exception.EntityNotFoundException;
import org.example.shopdemo.service.BookPageService;
import org.example.shopdemo.service.BooksImagesService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebServlet(UrlPath.BOOK_PAGE)
public class BookPageServlet extends HttpServlet {
    private final BookPageService bookPageService = BookPageService.getInstance();
    private final BooksImagesService booksImagesService = BooksImagesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Long bookId = Long.valueOf(req.getParameter("bookId"));
        log.info("Finding img for book id {} using booksImagesService.", bookId);
        try {
            List<String> booksImgUrls = booksImagesService.getImagesForBook(bookId);
            if (booksImgUrls.isEmpty()){
                booksImgUrls = Collections.singletonList(req.getContextPath() + "/uploads/catDefault.jpg");
            }

            log.info("Finding bookDto using bookPageService for id: {}", bookId);
            BooksDto booksDto = bookPageService.openBookPage(bookId);

            req.setAttribute("bookPage", booksDto);
            req.setAttribute("bookImagesUrls", booksImgUrls);

            log.info("Redirecting to bookPage with bookId: {}.", bookId);
            req.getRequestDispatcher(JSPHelper.getPath(UrlPath.BOOK_PAGE)).forward(req, resp);
        } catch (EntityNotFoundException e) {
            log.warn("Finding img for book id {} using booksImagesService failed.", bookId);
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Finding book in bd by id failed");
        } catch (Exception e){
            log.error("Unknown server error during bookPage opening response: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }
}

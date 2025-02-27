package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dao.BooksDao;
import org.example.shopdemo.utils.UrlPath;

import java.io.IOException;

@Slf4j
@WebServlet(UrlPath.DELETE_BOOK)
public class DeleteBookServlet extends HttpServlet {
    private final BooksDao booksDao = BooksDao.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var session = req.getSession();
        Long bookId = Long.valueOf(req.getParameter("bookId"));
        log.info("Admin with id {} try to delete book with id {}", session.getAttribute("admin"), bookId);

        boolean isDeleted = booksDao.delete(bookId);
        if (!isDeleted){
            log.error("Book wasn't deleted, as bookId wasn't found. BookId was: {}", bookId);
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Book wasn't deleted, as bookId wasn't found");
        }
        log.info("Book with id {} aws successfully deleted.", bookId);
        resp.sendRedirect(UrlPath.BOOKS);
    }
}

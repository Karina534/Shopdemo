package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dao.BooksDao;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.dto.PublishersDto;
import org.example.shopdemo.entity.Books;
import org.example.shopdemo.entity.Currencies;
import org.example.shopdemo.service.BooksImagesService;
import org.example.shopdemo.service.CurrenciesService;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.service.PublishersService;
import org.example.shopdemo.utils.JSPHelper;
import org.example.shopdemo.utils.UrlPath;
import org.slf4j.MDC;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@WebServlet(UrlPath.BOOK_REDUCTION)
public class BookReductionServlet extends HttpServlet {
    private final BooksDao booksDao = BooksDao.getInstance();
    private final CurrenciesService currenciesService = CurrenciesService.getInstance();
    private final PublishersService publishersService = PublishersService.getInstance();
    private final BooksImagesService booksImagesService = BooksImagesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var session = req.getSession();
        Long bookId = Long.valueOf(req.getParameter("bookId"));
        log.info("Admin with id {} try to change book with id {}", session.getAttribute("admin"), bookId);

        Optional<Books> books = booksDao.findById(bookId);
        if (books.isEmpty()){
            log.debug("Can't found book by this id {}.", bookId);
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Can't found book by this id.");
            return;
        }

        log.info("Book by this id {} was found successfully.", bookId);
        req.setAttribute("books", books.get());
        List<Currencies> currenciesList = currenciesService.findAll();
        List<PublishersDto> publishersDtoList = publishersService.findAll();
        req.setAttribute("currencies", currenciesList);
        req.setAttribute("publishers", publishersDtoList);
        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.BOOK_REDUCTION)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            LogService.logInfo("Starting change book", "");

            log.info("price = {}", req.getParameter("price"));

            BooksDto booksDto = buildBooksDto(req);

            // Сохраняем файлы и создаем DTO
//            LogService.logInfo("Making DTO images list and saving them", "");
//            List<BooksImagesDto> booksImagesDtoList = new ArrayList<>();
//            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
//            File uploadDir = new File(uploadPath);

//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//                LogService.logInfo("Dir for saving img created.", "");
//            }

//            for (Part part : req.getParts()){
//                if (part.getName().equals("image") && part.getSubmittedFileName() != null){
//                    String fileName = part.getSubmittedFileName();
//
//                    // Сохраняем файл
//                    String absoluteFilePath = uploadPath + File.separator + fileName;
//                    String relativeFilePath = UPLOAD_DIR + "\\" + fileName;
//                    part.write(absoluteFilePath);
//                    LogService.logDebug("File was saved!", "File path: %s", absoluteFilePath);
//
//                    // Добавляем DTO в список
//                    booksImagesDtoList.add(
//                            BooksImagesDto.builder().imageUrl(relativeFilePath).build()
//                    );
//                }
//            }

            // Передаем изображения в сервис для сохранения в бд
//            LogService.logInfo("Sending booksImagesDtoList to booksImagesService for saving.",
//                    "Length: %s", booksImagesDtoList.size());
//            Books book = booksImagesService.saveImageUrl(booksDto, booksImagesDtoList);

//            Optional<BooksDto> booksDto2 = booksImagesService.getBooksDtoByBookId(book.getBookId());
//            if (booksDto2.isEmpty()){
//                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not found book by id");
//            }

            boolean isChanged = booksImagesService.updateBook(booksDto);
            if (!isChanged){
                LogService.logDebug("Attempt to change book failed. Book title: {}", booksDto.getTitle());
                resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Attempt to change book failed.");
            }

            LogService.logInfo("Redirecting to book page after successful changing book.", "BookId: ", booksDto.getBooksId());
            req.setAttribute("bookId", booksDto.getBooksId());
            resp.sendRedirect(UrlPath.BOOK_PAGE + "?bookId=" + booksDto.getBooksId());

        } catch (Exception e){
            LogService.logError("Unknown mistake in MakeBookServlet", "Exception: %s", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            MDC.remove("requestId");
        }
    }

    private static BooksDto buildBooksDto(HttpServletRequest req){
        return BooksDto.builder()
                .booksId(Long.valueOf(req.getParameter("bookId")))
                .author(req.getParameter("author"))
                .title(req.getParameter("title"))
                .description(req.getParameter("description"))
                .price(new BigDecimal(req.getParameter("price")))
                .currencyId(Integer.parseInt(req.getParameter("currencyId")))
                .remains(Integer.parseInt(req.getParameter("remains")))
                .publisherId(Long.parseLong(req.getParameter("publisherId")))
                .build();
    }
}

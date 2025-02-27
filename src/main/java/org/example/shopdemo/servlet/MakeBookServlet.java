package org.example.shopdemo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.dto.BooksImagesDto;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@MultipartConfig
@WebServlet(UrlPath.MAKE_BOOK)
public class MakeBookServlet extends HttpServlet {
    private final CurrenciesService currenciesService = CurrenciesService.getInstance();
    private final PublishersService publishersService = PublishersService.getInstance();
    private static final String UPLOAD_DIR = "uploads";
    private final static BooksImagesService booksImagesService = BooksImagesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Currencies> currenciesList = currenciesService.findAll();
        List<PublishersDto> publishersDtoList = publishersService.findAll();
        req.setAttribute("currencies", currenciesList);
        req.setAttribute("publishers", publishersDtoList);

        req.getRequestDispatcher(JSPHelper.getPath(UrlPath.MAKE_BOOK)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);
            LogService.logInfo("Starting make book", "");

            BooksDto booksDto = BooksDto.builder()
                    .author(req.getParameter("author").trim())
                    .title(req.getParameter("title").trim())
                    .description(req.getParameter("description").trim())
                    .price(new BigDecimal(req.getParameter("price").trim()))
                    .currencyId(Integer.parseInt(req.getParameter("currencyId").trim()))
                    .remains(Integer.parseInt(req.getParameter("remains").trim()))
                    .publisherId(Long.parseLong(req.getParameter("publisherId").trim()))
                    .build();

            // Сохраняем файлы и создаем DTO
            LogService.logInfo("Making DTO images list and saving them", "");
            List<BooksImagesDto> booksImagesDtoList = new ArrayList<>();
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
                LogService.logInfo("Dir for saving img created.", "");
            }

            for (Part part : req.getParts()){
                if (part.getName().equals("image") && !Objects.equals(part.getSubmittedFileName(), "")){
                    String fileName = part.getSubmittedFileName();

                    // Сохраняем файл
                    String absoluteFilePath = uploadPath + File.separator + fileName;
                    String relativeFilePath = UPLOAD_DIR + "/" + fileName;
                    part.write(absoluteFilePath);
                    LogService.logDebug("File was saved!", "File path: %s", absoluteFilePath);

                    // Добавляем DTO в список
                    booksImagesDtoList.add(
                            BooksImagesDto.builder().imageUrl(relativeFilePath).build()
                    );
                }
            }

            // Передаем изображения в сервис для сохранения в бд
            LogService.logInfo("Sending booksImagesDtoList to booksImagesService for saving.",
                    "Length: %s", booksImagesDtoList.size());
            Books book = booksImagesService.saveImageUrl(booksDto, booksImagesDtoList);

            LogService.logInfo("Redirecting to book page after successful saving.", "BookId: ", book.getBookId());
            req.setAttribute("bookId", book.getBookId());
            resp.sendRedirect(UrlPath.BOOK_PAGE + "?bookId=" + book.getBookId());

        } catch (Exception e){
            LogService.logError("Unknown mistake in MakeBookServlet", "Exception: %s", e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        } finally {
            MDC.remove("requestId");
        }
    }
}

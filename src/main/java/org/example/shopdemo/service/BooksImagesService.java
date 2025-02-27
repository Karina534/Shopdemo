package org.example.shopdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.dao.BooksDao;
import org.example.shopdemo.dao.BooksImagesDao;
import org.example.shopdemo.dao.PublishersDao;
import org.example.shopdemo.dto.BooksDto;
import org.example.shopdemo.dto.BooksImagesDto;
import org.example.shopdemo.entity.Books;
import org.example.shopdemo.entity.BooksImages;
import org.example.shopdemo.entity.Currencies;
import org.example.shopdemo.exception.DaoException;

import java.util.List;
import java.util.Optional;

@Slf4j
public class BooksImagesService {
    private final static BooksImagesService INSTANCE = new BooksImagesService();
    private final BooksImagesDao booksImagesDao = BooksImagesDao.getInstance();
    private final BooksDao booksDao = BooksDao.getInstance();
    private final PublishersDao publishersDao = PublishersDao.getInstance();

    public Books saveImageUrl(BooksDto booksDto, List<BooksImagesDto> booksImagesDtoList){
        // Сохранение книги
        Books savedBook = saveBook(booksDto);

        // Сохранение изображений
        LogService.logInfo("Starting saving images for book.", "Book title: %s", savedBook.getTitle());
        for (BooksImagesDto booksImagesDto: booksImagesDtoList){
            BooksImages booksImages = new BooksImages(
                    null,
                    savedBook.getBookId(),
                    booksImagesDto.getImageUrl()
            );

            booksImagesDao.save(booksImages);
        }
        LogService.logInfo("Saving images for book successfully finished.", "Book title: %s", savedBook.getTitle());
        return savedBook;
    }

    public List<String> getImagesForBook(Long booksId){
        return booksImagesDao.findByBookId(booksId);
    }

    private Books saveBook(BooksDto booksDto){
        LogService.logInfo("Starting save book in bd.", "Book title: %s", booksDto.getTitle());
        Books book = new Books(
                null,
                booksDto.getAuthor(),
                booksDto.getTitle(),
                booksDto.getDescription(),
                booksDto.getPrice(),
                Currencies.fromId(booksDto.getCurrencyId()),
                booksDto.getRemains(),
                publishersDao.findById(booksDto.getPublisherId()).get()
        );

        var Book = booksDao.save(book);
        LogService.logInfo("Book saved in bd.", "Book title: %s, BookId: %s",
                booksDto.getTitle(), booksDto.getBooksId());
        return Book;
    }

    public boolean updateBook(BooksDto booksDto){
        LogService.logInfo("Starting update book in bd.", "Book title: %s", booksDto.getTitle());
        Books book = new Books(
                booksDto.getBooksId(),
                booksDto.getAuthor(),
                booksDto.getTitle(),
                booksDto.getDescription(),
                booksDto.getPrice(),
                Currencies.fromId(booksDto.getCurrencyId()),
                booksDto.getRemains(),
                publishersDao.findById(booksDto.getPublisherId()).get()
        );

        var Book = booksDao.update(book);
        LogService.logInfo("Book changed {} in bd.", "Book title: %s, BookId: %s", Book,
                booksDto.getTitle(), booksDto.getBooksId());
        return Book;
    }

    public String getFirstImageForBook(Long id){
        log.info("Trying to find first img for book with id: {}", id);
        var img = booksImagesDao.findByBookId(id);
        if (!img.isEmpty()){
            log.info("Have found first img for book with id: {} - path: {}", id, img.get(0));
            return img.get(0);
        }
        log.debug("No one img was found for book id: {}", id);
        return null;
    }

    public Optional<BooksDto> getBooksDtoByBookId(Long id){
        LogService.logInfo("Try find book in bd by ", "BookId: %s", id);
        Optional<Books> book = booksDao.findById(id);
        return book.flatMap(books -> Optional.ofNullable(BooksDto
                .builder()
                .booksId(books.getBookId())
                .author(books.getAuthor())
                .title(books.getTitle())
                .description(books.getDescription())
                .price(books.getPrice())
                .currencyId(books.getCurrency().getId())
                .remains(books.getRemains())
                .publisherId(books.getPublisher().getPublisherId())
                .build()));
    }

    public static BooksImagesService getInstance(){
        return INSTANCE;
    }
    private BooksImagesService() {
    }
}

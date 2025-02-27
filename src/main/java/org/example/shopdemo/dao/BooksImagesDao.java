package org.example.shopdemo.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.shopdemo.entity.BooksImages;
import org.example.shopdemo.exception.DaoException;
import org.example.shopdemo.utils.BDConnectionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BooksImagesDao implements Dao<Long, BooksImages> {
    private final static BooksImagesDao INSTANCE = new BooksImagesDao();

    private static final String SAVE_SQL = """
            INSERT INTO books_images (book_id, image_url)
            values (?, ?)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM books_images
            WHERE books_images_id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT bi.books_images_id, bi.book_id, bi.image_url, b.author, b.title, b.description, b.price,
            b.currency_id, b.remains, b.publisher_id
            FROM books_images bi
            join public.books b on b.book_id = bi.book_id
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT bi.books_images_id, bi.book_id, bi.image_url, b.author, b.title, b.description, b.price,
            b.currency_id, b.remains, b.publisher_id
            FROM books_images bi
            join public.books b on b.book_id = bi.book_id
            where bi.books_images_id = ?
            """;

    private static final String FIND_BY_BOOK_ID_SQL = """
            SELECT bi.books_images_id, bi.book_id, bi.image_url, b.author, b.title, b.description, b.price,
            b.currency_id, b.remains, b.publisher_id
            FROM books_images bi
            join public.books b on b.book_id = bi.book_id
            where b.book_id = ?
            """;

    private static final String UPDATE_SQL = """
        UPDATE books_images
        SET book_id = ?,
            image_url = ?
        where books_images_id = ?
        """;

    @Override
    public BooksImages save(BooksImages booksImages) {
        log.info("Saving booksImages: {}", booksImages);
        try(var connection = BDConnectionService.connection();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, booksImages.getBookId());
            statement.setString(2, booksImages.getImageUrl());

            statement.executeUpdate();

            var key = statement.getGeneratedKeys();
            if (key.next()){
                booksImages.setBooksImagesId(key.getLong("books_images_id"));
            }

            log.info("BooksImages saved with ID: {}", booksImages.getBooksImagesId());
            return booksImages;
        } catch (SQLException e) {
            log.error("Failed to save booksImages: {}", booksImages, e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        log.info("Deleting BooksImages with ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(1, id);
            boolean result = statement.executeUpdate() > 0;
            log.info("BooksImages deletion result for ID {}: {}", id, result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to delete BooksImages with ID: {}", id, e);
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(BooksImages booksImages) {
        log.info("Updating booksImages: {}", booksImages);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setLong(1, booksImages.getBookId());
            statement.setString(2, booksImages.getImageUrl());

            boolean result = statement.executeUpdate() > 0;
            log.info("BooksImages update result for ID {}: {}", booksImages.getBooksImagesId(), result);
            return result;
        } catch (SQLException e) {
            log.error("Failed to update booksImages: {}", booksImages, e);
            throw new DaoException(e);
        }
    }

    @Override
    public List<BooksImages> findAll() {
        log.info("Find all BooksImages");
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {

            List<BooksImages> allBooksImages = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allBooksImages.add(
                        makeBookImage(res)
                );
            }

            log.info("Found {} BooksImages", allBooksImages.size());
            return allBooksImages;
        } catch (SQLException e) {
            log.error("Failed to found all BooksImages", e);
            throw new DaoException(e);
        }
    }

    private BooksImages makeBookImage(ResultSet res) throws SQLException {
        return new BooksImages(res.getLong("books_images_id"),
                res.getLong("book_id"),
                res.getString("image_url"));
    }

    @Override
    public Optional<BooksImages> findById(Long id) {
        log.info("Finding BooksImages by ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, id);
            BooksImages booksImages = null;
            var res = statement.executeQuery();
            if (res.next()) booksImages = makeBookImage(res);

            log.info("BooksImages found by ID {}: {}", id, booksImages != null);
            return Optional.ofNullable(booksImages);
        } catch (SQLException e) {
            log.error("Failed to find BooksImages by ID: {}", id, e);
            throw new DaoException(e);
        }
    }

    public List<String> findByBookId(Long id) {
        log.info("Finding allBooksImagesUrl by ID: {}", id);
        try (var connection = BDConnectionService.connection();
             var statement = connection.prepareStatement(FIND_BY_BOOK_ID_SQL)) {

            statement.setLong(1, id);
            List<String> allBooksImagesUrl = new ArrayList<>();
            var res = statement.executeQuery();
            while (res.next()){
                allBooksImagesUrl.add(res.getString("image_url"));
            }

            log.info("AllBooksImagesUrl found by ID {}: {}", id, !allBooksImagesUrl.isEmpty());
            return allBooksImagesUrl;
        } catch (SQLException e) {
            log.error("Failed to find allBooksImagesUrl by ID: {}", id, e);
            throw new DaoException(e);
        }
    }

    public static BooksImagesDao getInstance(){
        return INSTANCE;
    }
    private BooksImagesDao() {
    }
}

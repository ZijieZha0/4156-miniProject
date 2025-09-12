package dev.coms4156.project.individualproject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.coms4156.project.individualproject.model.Book;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mock API service that mimics a backing catalogue (e.g., CLIO).
 *
 * <p>Provides read/update operations on an in-memory list of {@link Book}s that is
 * loaded from {@code src/main/resources/mockdata/books.json} at construction time.
 */
@Service
public class MockApiService {

  /** Logger instance for this service. */
  private static final Logger LOG = LoggerFactory.getLogger(MockApiService.class);

  /** In-memory catalogue of books, loaded from the JSON resource. */
  private List<Book> books;

  /** Example auxiliary list; kept for parity with baseline code. */
  private List<String> bags;

  /**
   * Constructs a new service and eagerly loads book data from
   * {@code resources/mockdata/books.json}. If the resource is missing or
   * unreadable, the service falls back to an empty catalogue and logs the error.
   */
  public MockApiService() {
    try (InputStream inputStream = Thread.currentThread()
        .getContextClassLoader()
        .getResourceAsStream("mockdata/books.json")) {

      if (inputStream == null) {
        LOG.error("Failed to find mockdata/books.json in resources.");
        this.books = new ArrayList<>(0);
      } else {
        final ObjectMapper mapper = new ObjectMapper();
        // PMD: UseDiamondOperator — use <>
        this.books = mapper.readValue(inputStream, new TypeReference<>() {});
        LOG.info("Successfully loaded books from mockdata/books.json.");
      }
    } catch (final Exception e) {
      LOG.error("Failed to load books", e);
      this.books = new ArrayList<>(0);
    }

    // Keep parity with baseline; not used functionally.
    this.bags = new ArrayList<>();
  }

  /**
   * Returns the current catalogue.
   *
   * <p>Note: Returns an unmodifiable view to prevent accidental external mutation.
   *
   * @return an unmodifiable list of books
   */
  public List<Book> getBooks() {
    return Collections.unmodifiableList(books);
  }

  /**
   * Replaces the existing book (matched via {@link Book#equals(Object)}) with
   * {@code newBook}. If no match is found, the catalogue remains unchanged.
   *
   * @param newBook updated book instance (non-null)
   */
  public void updateBook(final Book newBook) {
    final List<Book> tmpBooks = new ArrayList<>();
    for (final Book book : books) {
      if (book.equals(newBook)) {
        tmpBooks.add(newBook);
      } else {
        tmpBooks.add(book);
      }
    }
    this.books = tmpBooks;
  }

  /**
   * Logs the current catalogue to the application logger.
   */
  public void printBooks() {
    for (final Book b : books) {
      LOG.info("Book: {}", b);
    }
  }
}
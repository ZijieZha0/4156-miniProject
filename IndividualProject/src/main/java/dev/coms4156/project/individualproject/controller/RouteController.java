package dev.coms4156.project.individualproject.controller;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;      
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for book-related endpoints:
 * "/", "/book/{id}", "/books/available", "/book/{bookId}/add".
 */
@RestController
public class RouteController {

  /** Logger for this controller. */
  private static final Logger LOG = LoggerFactory.getLogger(RouteController.class); 

  /** Service layer facade for book operations. */
  private final MockApiService mockApiService;

  /** Constructor that injects the service dependency. */
  public RouteController(final MockApiService mockApiService) {
    this.mockApiService = mockApiService;
  }

  /** Welcome endpoint. */
  @GetMapping({"/", "/index"})
  public String index() {
    return "Welcome to the home page! In order to make an API call direct your browser"
        + "or Postman to an endpoint.";
  }

  /**
   * Returns the details of the specified book.
   *
   * @param id the unique identifier of the book to retrieve
   * @return 200 with the book if found; otherwise 404
   */
  @GetMapping({"/book/{id}"})
  @SuppressWarnings("PMD.ShortVariable") // keep path variable name as 'id'
  public ResponseEntity<?> getBook(@PathVariable final int id) {
    Book found = null;
    for (final Book book : mockApiService.getBooks()) {
      if (book.getId() == id) {
        found = book;
        break;
      }
    }
    return (found != null)
        ? new ResponseEntity<>(found, HttpStatus.OK)
        : new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Get and return a list of all the books with available copies.
   *
   * @return 200 with available books; otherwise 500 if error
   */
  @GetMapping({"/books/available"})
  public ResponseEntity<?> getAvailableBooks() {
    ResponseEntity<?> result;
    try {
      final List<Book> availableBooks = new ArrayList<>();
      for (final Book book : mockApiService.getBooks()) {
        if (book.hasCopies()) {
          availableBooks.add(book);
        }
      }
      result = new ResponseEntity<>(availableBooks, HttpStatus.OK);
    } catch (final Exception e) {
      LOG.error("Error occurred when getting all available books", e); 
      result = new ResponseEntity<>(
          "Error occurred when getting all available books",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  /**
   * Adds a copy to the Book if it exists.
   *
   * @param bookId the unique id of the book
   * @return 200 with updated book; 404 if not found; 500 if error
   */
  @PatchMapping({"/book/{bookId}/add"})
  public ResponseEntity<?> addCopy(@PathVariable final Integer bookId) {
    ResponseEntity<?> result;
    try {
      Book matched = null;
      for (final Book book : mockApiService.getBooks()) {
        if (bookId.equals(book.getId())) {
          matched = book;
          break;
        }
      }
      if (matched != null) {
        matched.addCopy();
        result = new ResponseEntity<>(matched, HttpStatus.OK);
      } else {
        result = new ResponseEntity<>("Book not found.", HttpStatus.NOT_FOUND);
      }
    } catch (final Exception e) {
      LOG.error("Error occurred when adding a copy", e); 
      result = new ResponseEntity<>(
          "Error occurred when adding a copy",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return result;
  }
}
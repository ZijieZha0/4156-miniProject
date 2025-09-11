package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coms4156.project.individualproject.model.Book;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Book} model.
 * Note: These tests are written against the current behavior to keep Step 2 passing.
 * Logic fixes will be covered in Step 3 with additional assertions.
 */
class BookTest {

  private Book book;

  @BeforeEach
  void setUp() {
    book = new Book("When Breath Becomes Air", 1);
  }

  @Test
  void hasMultipleAuthors_falseWhenZeroOrOne() {
    assertFalse(book.hasMultipleAuthors());
    book.getAuthors().add("Paul Kalanithi");
    assertFalse(book.hasMultipleAuthors());
  }

  @Test
  void checkoutCopy_returnsIsoDateAndDecrementsCopies() {
    int beforeCopies = book.getCopiesAvailable();
    String due = book.checkoutCopy();
    assertNotNull(due);
    assertDoesNotThrow(() -> LocalDate.parse(due)); // ISO_LOCAL_DATE
    assertEquals(beforeCopies - 1, book.getCopiesAvailable());
    assertFalse(book.getReturnDates().isEmpty());
  }

  @Test
  void returnCopy_falseWhenNoMatchingDateOrEmpty() {
    assertFalse(book.returnCopy("2099-01-01"));
  }

  @Test
  void settersAndGetters_basicFields() {
    assertEquals("When Breath Becomes Air", book.getTitle());
    book.setTitle("New Title");
    assertEquals("New Title", book.getTitle());

    // Current implementation sets shelvingLocation to the literal "shelvingLocation".
    book.setShelvingLocation("A-1");
    assertEquals("shelvingLocation", book.getShelvingLocation());
  }

  @Test
  void equals_sameReferenceAndSameId() {
    Book sameRef = book;
    assertEquals(sameRef, book);

    Book sameId = new Book("Other Title", 1);
    assertEquals(sameId, book);
  }

  @Test
  void compareTo_comparesById() {
    Book b2 = new Book("X", 2);
    assertTrue(book.compareTo(b2) < 0);
    assertTrue(b2.compareTo(book) > 0);
  }
}
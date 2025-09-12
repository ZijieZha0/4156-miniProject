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
 * Unit tests for the {@link Book} model against the FIXED logic in Step 3.
 * These tests assert correct behavior for copy counts, checkout/return flow,
 * setters/getters, equality and comparison.
 */
class BookTest {

  private Book book;

  @BeforeEach
  void setUp() {
    // Default constructor sets copiesAvailable = 1, totalCopies = 1, etc.
    book = new Book("When Breath Becomes Air", 1);
  }

  @Test
  void hasCopies_trueOnlyWhenCopiesAvailableIsPositive() {
    // With default 1 copy, should be true
    assertTrue(book.hasCopies(), "Expected hasCopies() to be true when copiesAvailable > 0");

    // Bring copiesAvailable down to 0 by checking out once
    String due = book.checkoutCopy();
    assertNotNull(due, "Checkout should succeed when a copy is available");
    assertFalse(book.hasCopies(), "Expected hasCopies() to be false when copiesAvailable == 0");
  }

  @Test
  void addCopy_incrementsTotalAndAvailable() {
    int beforeTotal = book.getTotalCopies();
    int beforeAvail = book.getCopiesAvailable();

    book.addCopy();

    assertEquals(beforeTotal + 1, book.getTotalCopies(), "totalCopies should increment by 1");
    assertEquals(beforeAvail + 1, book.getCopiesAvailable(),    
            "copiesAvailable should increment by 1");
  }

  @Test
  void deleteCopy_returnsTrueOnSuccessAndFalseWhenNoCopies() {
    // Start with 1 copy -> delete should succeed and decrement both totals
    int beforeTotal = book.getTotalCopies();
    int beforeAvail = book.getCopiesAvailable();

    assertTrue(book.deleteCopy(), "deleteCopy() should return true when a copy is available");
    assertEquals(beforeTotal - 1, book.getTotalCopies(), 
            "totalCopies should decrement by 1");
    assertEquals(beforeAvail - 1, book.getCopiesAvailable(), 
            "copiesAvailable should decrement by 1");

    // Now there are no copies left -> delete should fail
    assertFalse(book.deleteCopy(), "deleteCopy() should return false when no copies are available");
  }

  @Test
  void checkoutCopy_emitsIsoDueDate_decrementsAvailable_incrementsCheckoutCount() {
    int beforeAvail = book.getCopiesAvailable();
    final int beforeTimes = book.getAmountOfTimesCheckedOut();

    String due = book.checkoutCopy();
    assertNotNull(due, "Checkout should return a due date string");
    assertDoesNotThrow(() -> LocalDate.parse(due), "Due date should be ISO_LOCAL_DATE parseable");

    assertEquals(beforeAvail - 1, book.getCopiesAvailable(), 
            "copiesAvailable should decrement by 1");

    assertEquals(beforeTimes + 1, book.getAmountOfTimesCheckedOut(),
        "amountOfTimesCheckedOut should increment by 1");
    assertFalse(book.getReturnDates().isEmpty(), "returnDates should contain the due date");
  }

  @Test
  void returnCopy_trueWhenMatchingDateExists_andRestoresAvailability() {
    // First checkout to create a due date
    String due = book.checkoutCopy();
    assertNotNull(due);

    int beforeAvail = book.getCopiesAvailable();

    // Return the exact date -> should succeed and increase availability by 1
    assertTrue(book.returnCopy(due), "returnCopy() should succeed for a stored due date");
    assertEquals(beforeAvail + 1, book.getCopiesAvailable(),
        "copiesAvailable should increment by 1 after a successful return");

    // Returning a non-existent date should fail
    assertFalse(book.returnCopy("2099-01-01"), "returnCopy() should fail for a non-existent date");
  }

  @Test
  void setters_setShelvingLocationUsesProvidedValue_notLiteral() {
    book.setShelvingLocation("A-1");
    assertEquals("A-1", book.getShelvingLocation(),
        "setShelvingLocation should set the provided value (not a literal string)");
  }

  @Test
  void hasMultipleAuthors_falseForZeroOrOneAuthor() {
    assertFalse(book.hasMultipleAuthors(), "Zero authors -> false");
    book.getAuthors().add("Paul Kalanithi");
    assertFalse(book.hasMultipleAuthors(), "Exactly one author -> false");
  }

}
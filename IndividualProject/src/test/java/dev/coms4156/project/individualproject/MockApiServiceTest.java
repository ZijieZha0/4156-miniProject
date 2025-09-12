package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MockApiService}.
 * These tests assert stability based on current behavior; functional fixes will be added in Step 3.
 */
class MockApiServiceTest {

  @Test
  void constructor_initializesBooksList() {
    MockApiService svc = new MockApiService();
    assertNotNull(svc.getBooks());
  }

  @Test
  void updateBook_doesNotThrow_andSizeUnchangedPerCurrentBehavior() {
    MockApiService svc = new MockApiService();
    ArrayList<Book> before = new ArrayList<>(svc.getBooks());

    assertDoesNotThrow(() -> {
      if (!before.isEmpty()) {
        Book orig = before.get(0);
        Book updated = new Book(orig.getTitle() + " (Updated)", orig.getId());
        svc.updateBook(updated);
      } else {
        svc.updateBook(new Book("X", 1));
      }
    });

    // Current implementation keeps the same list reference and size.
    assertEquals(before.size(), svc.getBooks().size());
  }
}
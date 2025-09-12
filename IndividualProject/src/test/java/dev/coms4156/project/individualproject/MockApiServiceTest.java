package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MockApiService}.
 * Now asserts the functional fix of updateBook (B-005).
 */
class MockApiServiceTest {

  @Test
  void constructor_initializesBooksList() {
    MockApiService svc = new MockApiService();
    assertNotNull(svc.getBooks());
  }

  @Test
  void updateBook_replacesMatchingBook_afterFix() {
    MockApiService svc = new MockApiService();
    ArrayList<Book> before = new ArrayList<>(svc.getBooks());
    assertNotNull(before);

    if (before.isEmpty()) {
      // If no data (unlikely with provided mock json), create a book and verify size behavior.
      Book seed = new Book("Seed", 1);
      assertDoesNotThrow(() -> svc.updateBook(seed));
      assertEquals(0, svc.getBooks().size(), "Without a matching id, size should remain unchanged");
      return;
    }

    // Pick the first book and "update" it with same id but modified title.
    Book orig = before.get(0);
    String updatedTitle = orig.getTitle() + " (Updated)";
    Book updated = new Book(updatedTitle, orig.getId());

    assertDoesNotThrow(() -> svc.updateBook(updated));

    Optional<Book> found =
        svc.getBooks().stream().filter(b -> b.getId() == orig.getId()).findFirst();
    assertNotNull(found.orElse(null), "Updated book with same id should exist");
    assertEquals(updatedTitle, found.get().getTitle(), "Title should be replaced after update");
 
    assertEquals(before.size(), svc.getBooks().size(), "Size should not change on in-place update");
  }
}
package dev.coms4156.project.individualproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.individualproject.model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the {@link Book} model.
 */
@SpringBootTest
public class BookUnitTests {

  public static Book book;

  @BeforeAll
  public static void setUpBookForTesting() {
    book = new Book("When Breath Becomes Air", 0);
  }

  @Test
  public void equalsBothAreTheSame() {
    Book cmpBook = book;
    assertEquals(cmpBook, book);
  }
  
}
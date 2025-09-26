package dev.coms4156.project.individualproject;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.coms4156.project.individualproject.controller.RouteController;
import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List; 
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; 
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType; 
import org.springframework.test.web.servlet.MockMvc;

/**
 * MVC tests for {@link RouteController}.
 *
 * <p>These tests focus on controller behavior using {@code @WebMvcTest}, while the service layer
 * is mocked via {@code @MockBean}.
 */
@WebMvcTest(RouteController.class)
class RouteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MockApiService mockApiService;

  private List<Book> books;

  @BeforeEach
  void setUp() {
    // Default stub: small catalogue for basic endpoints.
    books = new ArrayList<>();
    final Book b1 = new Book("A", 1);
    final Book b2 = new Book("B", 2);
    books.add(b1);
    books.add(b2);

    Mockito.when(mockApiService.getBooks()).thenReturn(new ArrayList<>(books));
  }

  @Test
  void index_returnsWelcomeMessage() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Welcome")));
  }

  @Test
  void getBook_found_returns200AndBody() throws Exception {
    mockMvc.perform(get("/book/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("A"));
  }

  @Test
  void getAvailableBooks_returnsAvailableBooksList() throws Exception {
    mockMvc.perform(get("/books/available"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void addCopy_found_returns200() throws Exception {
    mockMvc.perform(patch("/book/1/add"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  /**
   * Happy path for /books/recommendation.
   * - Returns exactly 10 unique books
   * - Must include the top-5 most popular (by amountOfTimesCheckedOut)
   */
  @Test
  void getRecommendations_returns10_unique_andContainsTop5() throws Exception {
    // Build a catalogue of 12 books with popularity increasing by id.
    final List<Book> catalogue = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      final Book b = new Book("B" + i, i);
      // Increase checkout count i times; return immediately to restore availability.
      for (int k = 0; k < i; k++) {
        final String due = b.checkoutCopy();
        if (due != null) {
          b.returnCopy(due);
        }
      }
      catalogue.add(b);
    }
    Mockito.when(mockApiService.getBooks()).thenReturn(catalogue);

    final String body = mockMvc.perform(get("/books/recommendation"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();

    final ObjectMapper om = new ObjectMapper();
    final List<Map<String, Object>> arr =
        om.readValue(body, new TypeReference<List<Map<String, Object>>>() {});

    // 1) Exactly 10
    assertEquals(10, arr.size(), "should return exactly 10 books");

    // 2) All unique (by id)
    final Set<Integer> ids = arr.stream()
        .map(m -> (Integer) m.get("id"))
        .collect(Collectors.toSet());
    assertEquals(10, ids.size(), "all recommended books must be unique");

    // 3) Top-5 popular are ids {12,11,10,9,8}
    final Set<Integer> expectedTop5 = new HashSet<>(Arrays.asList(12, 11, 10, 9, 8));
    assertTrue(ids.containsAll(expectedTop5),
        "recommendations must include top-5 popular books");
  }

  /**
   * Error path for /books/recommendation:
   * - When there are fewer than 10 books, respond with 400 Bad Request.
   */
  @Test
  void getRecommendations_returns400_whenLessThan10Books() throws Exception {
    final List<Book> few = Arrays.asList(
        new Book("A", 1),
        new Book("B", 2),
        new Book("C", 3)
    );
    Mockito.when(mockApiService.getBooks()).thenReturn(few);

    mockMvc.perform(get("/books/recommendation"))
        .andExpect(status().isBadRequest());
  }

  /**
   * Checkout succeeds when a copy is available.
   */
  @Test
  void checkout_success_returns200AndUpdatedBook() throws Exception {
    // Book with one available copy (default)
    Book book = new Book("C", 3);
    List<Book> list = new ArrayList<>();
    list.add(book);
    Mockito.when(mockApiService.getBooks()).thenReturn(list);

    mockMvc.perform(patch("/checkout").param("id", "3"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(3))
        .andExpect(jsonPath("$.copiesAvailable").value(0))
        .andExpect(jsonPath("$.amountOfTimesCheckedOut").value(1));

    Mockito.verify(mockApiService).updateBook(Mockito.argThat(b -> b.getId() == 3));
  }

  /**
   * Checkout returns 400 when no copies are available.
   */
  @Test
  void checkout_noCopies_returns400() throws Exception {
    Book book = new Book("D", 4);
    // Bring copiesAvailable down to 0
    book.deleteCopy(); // now totalCopies=0, copiesAvailable=0

    List<Book> list = new ArrayList<>();
    list.add(book);
    Mockito.when(mockApiService.getBooks()).thenReturn(list);

    mockMvc.perform(patch("/checkout").param("id", "4"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("No copies available")));
  }

  /**
   * Checkout returns 404 when the book does not exist.
   */
  @Test
  void checkout_notFound_returns404() throws Exception {
    Mockito.when(mockApiService.getBooks()).thenReturn(new ArrayList<>());

    mockMvc.perform(patch("/checkout").param("id", "999"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Book not found")));
  }

  /** getBook returns 404 when not found. */
  @Test
  void getBook_notFound_returns404() throws Exception {
    mockMvc.perform(get("/book/999"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Book not found")));
  }

  /** addCopy returns 404 when book is not found. */
  @Test
  void addCopy_notFound_returns404() throws Exception {
    mockMvc.perform(patch("/book/999/add"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("Book not found")));
  }

  /** addCopy returns 500 when the service throws an exception. */
  @Test
  void addCopy_exception_returns500() throws Exception {
    Mockito.when(mockApiService.getBooks()).thenThrow(new RuntimeException("oops"));
    mockMvc.perform(patch("/book/1/add"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString("Error occurred when adding a copy")));
  }

  /** available-books endpoint returns 500 when the service throws. */
  @Test
  void getAvailableBooks_exception_returns500() throws Exception {
    Mockito.when(mockApiService.getBooks()).thenThrow(new RuntimeException("boom"));
    mockMvc.perform(get("/books/available"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString(
        "Error occurred when getting all available books")));
  }

  /** recommendation endpoint returns 500 when the service throws. */
  @Test
  void recommendation_exception_returns500() throws Exception {
    Mockito.when(mockApiService.getBooks()).thenThrow(new RuntimeException("kaboom"));
    mockMvc.perform(get("/books/recommendation"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString(
        "Error occurred while generating recommendations.")));
  }

  /** checkout returns 500 when the service throws. */
  @Test
  void checkout_exception_returns500() throws Exception {
    Mockito.when(mockApiService.getBooks()).thenThrow(new RuntimeException("fail"));
    mockMvc.perform(patch("/checkout").param("id", "1"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString("Error during checkout.")));
  }
}
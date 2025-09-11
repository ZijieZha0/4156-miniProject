package dev.coms4156.project.individualproject;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.coms4156.project.individualproject.model.Book;
import dev.coms4156.project.individualproject.service.MockApiService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * MVC tests for the RouteController endpoints.
 * We use @WebMvcTest to focus on controller behavior and mock the service layer.
 */
@WebMvcTest
class RouteControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private MockApiService mockApiService;

  private List<Book> books;

  @BeforeEach
  void setUp() {
    books = new ArrayList<>();
    Book b1 = new Book("A", 1);
    Book b2 = new Book("B", 2);
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
  void getBook_notFound_returns404() throws Exception {
    mockMvc.perform(get("/book/999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAvailableBooks_currentBehaviorReturnsAllBooks() throws Exception {
    // Current controller returns mockApiService.getBooks() directly.
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
  
}
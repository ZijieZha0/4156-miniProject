package dev.coms4156.project.individualproject.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the Book entry model.
 */
@SuppressWarnings({"PMD.ShortVariable", "PMD.LongVariable", "PMD.OnlyOneReturn", 
    "PMD.ShortClassName"})
public class Book implements Comparable<Book> {

  /** Title of the book. */
  private String title;

  /** List of author(s). */
  private List<String> authors;

  /** Language of the book. */
  private String language;

  /** Shelving location of the book. */
  private String shelvingLocation;

  /** Publication date of the book. */
  private String publicationDate;

  /** Publisher of the book. */
  private String publisher;

  /** List of subject(s) of the book. */
  private List<String> subjects;

  /** Unique identifier of the book. */
  private int id;

  /** Number of times the book has been checked out. */
  private int amountOfTimesCheckedOut;

  /** Number of copies currently available. */
  private int copiesAvailable;

  /** List of return due dates for checked-out copies. */
  private List<String> returnDates;

  /** Total number of copies (available + checked out). */
  private int totalCopies;

  /**
   * Very basic Book constructor.
   *
   * @param title the title of the book
   * @param id the unique id of the book
   */
  public Book(final String title, final int id) {
    this.title = title;
    this.id = id;
    this.authors = new ArrayList<>();
    this.language = "";
    this.shelvingLocation = "";
    this.publicationDate = "";
    this.publisher = "";
    this.subjects = new ArrayList<>();
    this.amountOfTimesCheckedOut = 0;
    this.copiesAvailable = 1;
    this.returnDates = new ArrayList<>();
    this.totalCopies = 1;
  }

  /**
   * Complete Book constructor.
   */
  public Book(final String title, final List<String> authors, final String language,
              final String shelvingLocation, final String publicationDate, final String publisher,
              final List<String> subjects, final int id, final int copiesAvailable,
              final int totalCopies) {
    this.title = title;
    this.authors = authors;
    this.language = language;
    this.shelvingLocation = shelvingLocation;
    this.publicationDate = publicationDate;
    this.publisher = publisher;
    this.subjects = subjects;
    this.id = id;
    this.amountOfTimesCheckedOut = 0;
    this.copiesAvailable = copiesAvailable;
    this.returnDates = new ArrayList<>();
    this.totalCopies = totalCopies;
  }

  /** No-args constructor for Jackson. */
  public Book() {
    this.authors = new ArrayList<>();
    this.subjects = new ArrayList<>();
    this.returnDates = new ArrayList<>();
    this.language = "";
    this.shelvingLocation = "";
    this.publicationDate = "";
    this.publisher = "";
    this.title = "";
    this.amountOfTimesCheckedOut = 0;
    this.copiesAvailable = 1;
    this.totalCopies = 1;
    this.id = 0;
  }

  /**
   * Returns whether at least one copy is available for checkout.
   *
   * @return true if {@code copiesAvailable > 0}; false otherwise
   */
  public boolean hasCopies() {
    return copiesAvailable > 0;
  }

  /**
   * Indicates whether this book lists more than one author.
   *
   * @return true if there is more than one author; false otherwise
   */
  public boolean hasMultipleAuthors() {
    return authors.size() > 1;
  }

  /**
   * Deletes a single copy if available.
   *
   * @return true if a copy was deleted; false otherwise
   */
  public boolean deleteCopy() {
    if (totalCopies > 0 && copiesAvailable > 0) {
      totalCopies--;
      copiesAvailable--;
      return true;
    }
    return false;
  }

  /** Adds a copy to the book. */
  public void addCopy() {
    totalCopies++;
    copiesAvailable++;
  }

  /**
   * Checks out a copy if available and generates a due date.
   *
   * @return ISO_LOCAL_DATE due date string if successful; null otherwise
   */
  public String checkoutCopy() {
    if (copiesAvailable > 0) {
      copiesAvailable--;
      amountOfTimesCheckedOut++;
      final LocalDate today = LocalDate.now();
      final LocalDate dueDate = today.plusWeeks(2);
      final String dueDateStr = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
      returnDates.add(dueDateStr);
      return dueDateStr;
    }
    return null;
  }

  /**
   * Returns a previously checked-out copy.
   *
   * @param date the due date string
   * @return true if a matching date was removed; false otherwise
   */
  public boolean returnCopy(final String date) {
    if (!returnDates.isEmpty()) {
      for (int i = 0; i < returnDates.size(); i++) {
        if (returnDates.get(i).equals(date)) {
          returnDates.remove(i);
          copiesAvailable++;
          return true;
        }
      }
    }
    return false;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public List<String> getAuthors() {
    return authors;
  }

  public void setAuthors(final List<String> authors) {
    this.authors = authors;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(final String language) {
    this.language = language;
  }

  public String getShelvingLocation() {
    return shelvingLocation;
  }

  public void setShelvingLocation(final String shelvingLocation) {
    this.shelvingLocation = shelvingLocation;
  }

  public String getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(final String publicationDate) {
    this.publicationDate = publicationDate;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(final String publisher) {
    this.publisher = publisher;
  }

  public List<String> getSubjects() {
    return subjects;
  }

  public void setSubjects(final List<String> subjects) {
    this.subjects = subjects;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public int getAmountOfTimesCheckedOut() {
    return amountOfTimesCheckedOut;
  }

  public int getCopiesAvailable() {
    return copiesAvailable;
  }

  public List<String> getReturnDates() {
    return returnDates;
  }

  public void setReturnDates(final List<String> returnDates) {
    this.returnDates = returnDates != null ? returnDates : new ArrayList<>();
  }

  public int getTotalCopies() {
    return totalCopies;
  }

  public void setTotalCopies(final int totalCopies) {
    this.totalCopies = totalCopies;
  }

  @Override
  public int compareTo(final Book other) {
    return Integer.compare(this.id, other.id);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Book cmpBook = (Book) obj;
    return cmpBook.id == this.id;
  }

  @Override
  public String toString() {
    return String.format("(%d)\t%s", this.id, this.title);
  }
}
# Bugs Fixed (Ongoing Log)

This file is a running log of all defects found and fixed throughout the project.  
Each entry includes: Symptom → Cause → Fix → Validation → Files Changed.

---

## Compilation / Structural Fixes

### [B-001] `RouteController.addCopy()` missing return statement
- **Symptom**: Maven compilation failed with “missing return statement”.
- **Cause**: The `catch` block did not return a response, and not all code paths returned a value.
- **Fix**: Added proper `ResponseEntity` returns for both the not-found path (404) and the exception path (500). Removed an unused `StringBuilder`.
- **Files Changed**: `src/main/java/.../controller/RouteController.java`
- **Validation**: `mvn compile` succeeds; `PATCH /book/{id}/add` returns:
  - `200 OK` for existing book IDs,
  - `404 Not Found` for non-existing IDs,
  - `500 Internal Server Error` on unexpected exceptions.

### [B-002] `BOOK.equals()` type mismatch
- **Symptom**: Compilation error “java.lang.Object cannot be converted to BOOK”.
- **Cause**: The method assigned `Object` directly to `BOOK` without casting.
- **Fix**: Casted the object to `BOOK` (`BOOK cmpBook = (BOOK) obj;`).
- **Files Changed**: `src/main/java/.../model/BOOK.java`
- **Validation**: `mvn compile` succeeds; equality works when comparing two `BOOK` instances with the same `id`.

### [B-003] `BOOK.getLanguage()` missing return
- **Symptom**: Method had no return statement (potential compilation/runtime issue).
- **Cause**: Getter was incomplete.
- **Fix**: Returned the `language` field.
- **Files Changed**: `src/main/java/.../model/BOOK.java`
- **Validation**: `mvn compile` succeeds.

---

## Book Model Logic Fixes

### [B-004] `Book.hasCopies()` incorrect condition
- **Symptom**: Method returned `true` even when `copiesAvailable == 0`.
- **Cause**: Condition used `>= 0` instead of `> 0`.
- **Fix**: Updated condition to return `true` only when `copiesAvailable > 0`.
- **Files Changed**: `src/main/java/.../model/Book.java`
- **Validation**: Unit test confirms `hasCopies()` returns `false` when no copies remain, and `true` when at least one copy exists.

### [B-005] `Book.deleteCopy()` incorrect return value
- **Symptom**: Method returned `false` even when a copy was successfully deleted.
- **Cause**: Return logic was inverted.
- **Fix**: Now decrements counts and returns `true` if a copy was removed; returns `false` otherwise.
- **Files Changed**: `src/main/java/.../model/Book.java`
- **Validation**: Unit tests confirm copies decrease and return value is correct for both available and unavailable cases.

### [B-006] `Book.addCopy()` empty implementation
- **Symptom**: Calling `addCopy()` had no effect.
- **Cause**: Method body was left unimplemented.
- **Fix**: Implemented logic to increment both `totalCopies` and `copiesAvailable`.
- **Files Changed**: `src/main/java/.../model/Book.java`
- **Validation**: Unit tests confirm that invoking `addCopy()` increases both fields by 1.

### [B-007] `Book.checkoutCopy()` incorrect counter update
- **Symptom**: `amountOfTimesCheckedOut` decreased instead of increasing.
- **Cause**: Used `--` instead of `++` on checkout counter.
- **Fix**: Corrected to increment `amountOfTimesCheckedOut` and decrement `copiesAvailable` when checkout succeeds.
- **Files Changed**: `src/main/java/.../model/Book.java`
- **Validation**: Unit tests verify that due date is generated, counters update correctly, and return is `null` if no copies available.

### [B-008] `Book.returnCopy()` inverted empty check
- **Symptom**: Method only attempted to remove a return date when the list was empty.
- **Cause**: Condition used `if (returnDates.isEmpty())` instead of `if (!returnDates.isEmpty())`.
- **Fix**: Fixed condition and logic so that if a matching due date is found, it is removed and copies increment.
- **Files Changed**: `src/main/java/.../model/Book.java`
- **Validation**: Unit tests verify that valid dates are removed, copies increment, and method returns `true`; invalid dates return `false`.

### [B-009] `Book.setShelvingLocation()` assigns constant string
- **Symptom**: Always set field to literal `"shelvingLocation"` instead of the provided value.
- **Cause**: Hard-coded string assignment in setter.
- **Fix**: Changed setter to assign the argument parameter.
- **Files Changed**: `src/main/java/.../model/Book.java`
- **Validation**: Unit test confirms that the field value matches the provided parameter after calling setter.

---

## Controller / Service Fixes

### [B-010] Incorrect return & HTTP status in `RouteController.getAvailableBooks()`
- **Symptom**: Built `availableBooks` but returned `mockApiService.getBooks()`. On exception, still returned `200 OK`.
- **Cause**: Copy–paste mistake and improper error handling.
- **Fix**: Now returns `availableBooks`; on exceptions returns `500 INTERNAL_SERVER_ERROR` with log message.
- **Files Changed**: `src/main/java/.../controller/RouteController.java`
- **Validation**: Verified via unit tests and manual requests.

### [B-011] Self-assignment in `MockApiService.updateBook()`
- **Symptom**: Books list was never updated (`this.books = this.books;` was a no-op).
- **Cause**: Typo introduced during refactor.
- **Fix**: Corrected assignment to `this.books = tmpBooks;`.
- **Files Changed**: `src/main/java/.../service/MockApiService.java`
- **Validation**: Updating a book now properly replaces it in the list.

### [B-012] Swallowing exceptions in `MockApiService` constructor
- **Symptom**: Exceptions while loading mock data were silently ignored.
- **Cause**: Empty `catch` block.
- **Fix**: Added `LOG.error` for exception logging and fallback `books = new ArrayList<>(0)`.
- **Files Changed**: `src/main/java/.../service/MockApiService.java`
- **Validation**: PMD no longer flags `EmptyCatchBlock`; application usable when resource missing.

---

## Static Analysis / Hygiene Fixes

### [B-013] Replace `System.out/err` with SLF4J
- **Symptom**: PMD flagged `SystemPrintln` usage.
- **Cause**: Debugging prints left in code.
- **Fix**: Replaced with SLF4J `LOG.info` / `LOG.error`.
- **Files Changed**:  
  - `src/main/java/.../controller/RouteController.java`  
  - `src/main/java/.../service/MockApiService.java`
- **Validation**: PMD warnings removed; logs output via SLF4J.

### [B-014] Minor PMD/Checkstyle hygiene
- **Symptom**: PMD/Checkstyle flagged style issues (missing `final`, short variable names).
- **Cause**: Parameters/locals not declared `final`; short names like `id` conflicted with PMD rules.
- **Fix**: Declared locals/params as `final`; added `@SuppressWarnings("PMD.ShortVariable")` where appropriate.
- **Files Changed**:  
  - `src/main/java/.../controller/RouteController.java`  
  - `src/main/java/.../service/MockApiService.java`  
  - `src/main/java/.../model/Book.java`
- **Validation**: Reduced static analysis noise.

### [B-015] Loose coupling: prefer interfaces over implementations
- **Symptom**: PMD flagged `ArrayList` usage in APIs.
- **Cause**: Directly exposed implementation types.
- **Fix**: Changed to `List` in fields and method signatures where possible; internal code still uses `ArrayList`.
- **Files Changed**:  
  - `src/main/java/.../model/Book.java`  
  - `src/main/java/.../service/MockApiService.java`
- **Validation**: PMD warnings resolved; functionality unchanged.

### [B-016] Use diamond operator
- **Symptom**: PMD flagged explicit type arguments in `new TypeReference<List<Book>>() {}`.
- **Cause**: Redundant explicit types.
- **Fix**: Replaced with `new TypeReference<>() {}`.
- **Files Changed**: `src/main/java/.../service/MockApiService.java`
- **Validation**: PMD warning resolved.

### [B-017] Missing comments (CommentRequired)
- **Symptom**: PMD flagged missing field/method comments.
- **Cause**: Public methods/fields lacked Javadoc.
- **Fix**: Added Javadoc for key fields and public APIs; adjusted formatting for Checkstyle.
- **Files Changed**:  
  - `src/main/java/.../model/Book.java`  
  - `src/main/java/.../service/MockApiService.java`
- **Validation**: PMD/Checkstyle pass.

### [B-018] Comment too large (CommentSize)
- **Symptom**: PMD flagged overly long/large comments.
- **Cause**: Long Javadoc lines and blocks.
- **Fix**: Reformatted comments, split into shorter lines.
- **Files Changed**:  
  - `src/main/java/.../controller/RouteController.java`  
  - `src/main/java/.../service/MockApiService.java`  
  - `src/main/java/.../model/Book.java`
- **Validation**: PMD passes; readability improved.
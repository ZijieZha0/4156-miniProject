# Bugs Fixed (Ongoing Log)

This file is a running log of all defects found and fixed throughout the project (Step 0–Step 3).  
Each entry includes: Symptom → Cause → Fix → Validation → Files Changed → Commit.

---

## [B-001] `RouteController.addCopy()` missing return statement
- **Symptom**: Maven compilation failed with “missing return statement”.
- **Cause**: The `catch` block did not return a response, and not all code paths returned a value.
- **Fix**: Added proper `ResponseEntity` returns for both the not-found path (404) and the exception path (500). Removed an unused `StringBuilder`.
- **Files Changed**: `src/main/java/.../controller/RouteController.java`
- **Validation**: `mvn compile` succeeds; `PATCH /book/{id}/add` returns:
  - `200 OK` for existing book IDs,
  - `404 Not Found` for non-existing IDs,
  - `500 Internal Server Error` on unexpected exceptions.
- **Commit**: b0fcfa5

---

## [B-002] `BOOK.equals()` type mismatch
- **Symptom**: Compilation error “java.lang.Object cannot be converted to BOOK”.
- **Cause**: The method assigned `Object` directly to `BOOK` without casting.
- **Fix**: Casted the object to `BOOK` (`BOOK cmpBook = (BOOK) obj;`).
- **Files Changed**: `src/main/java/.../model/BOOK.java`
- **Validation**: `mvn compile` succeeds; equality works when comparing two `BOOK` instances with the same `id`.
- **Commit**: b0fcfa5

---

## [B-003] `BOOK.getLanguage()` missing return
- **Symptom**: Method had no return statement (potential compilation/runtime issue).
- **Cause**: Getter was incomplete.
- **Fix**: Returned the `language` field.
- **Files Changed**: `src/main/java/.../model/BOOK.java`
- **Validation**: `mvn compile` succeeds.
- **Commit**: b0fcfa5

---


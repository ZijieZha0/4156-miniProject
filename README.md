# 4156-Miniproject-2025-Students
This is the public repo for posting the miniproject assignments to the class. 

This is a template repository.  See https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template. 

---

## Steps to Install and Run PMD
PMD is already included as a Maven plugin in the project. 
To run it and generate reports, first navigate to the project’s root directory (the one containing `pom.xml`), then run:

```bash
# Clean and compile the project
mvn clean compile

# Run tests
mvn test

# Run PMD analysis
mvn pmd:pmd

# The PMD report can be found under:
target/site/pmd.html


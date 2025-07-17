# Git Connector

## Project Overview
The **GitHub Connector** is a Spring Boot application designed to fetch public repositories and commit data for a given GitHub user. It interacts with the GitHub API to retrieve repository and commit information, providing REST endpoints for easy access. The application supports pagination and handles rate limits effectively.

---

## Features
- Fetch public repositories for a GitHub user.
- Retrieve commit details for each repository (author, commit message and timestamp).
- Pagination support for large datasets.
- Error handling for user not found and rate limit scenarios.

---

## Setup Instructions

### Prerequisites
1. **Java 17**: Ensure JDK 17 is installed.
2. **Maven**: Install Maven for dependency management.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/github-connector.git
   cd github-connector

2. Build the project:  
      mvn clean install
3. Run the application:
      mvn spring-boot:run


<hr></hr>

### Usage Instructions

Fetch User Repositories

**Method**: GET
**Description**: Fetches public repositories for the specified GitHub user.

**Parameters**:
1. username (Query Parameter): GitHub username.
2. page (Query Parameter, optional): Page number for pagination (default: 1).

    
**Example**:
curl "{BASE_URL}/api/git/commits?username=rahuldhawan003&page=7"



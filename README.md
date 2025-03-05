# Banking Console Application

## Overview

This project is a Java-based banking console application that simulates fundamental banking operations through a command-line interface. It provides users with the ability to create accounts, deposit and withdraw funds, transfer money between accounts, check balances, view transaction histories, and close accounts. The application also incorporates essential banking business rules, such as minimum deposit requirements and withdrawal limits, and persists data to files for secure storage.

## Features

*   **Account Creation:** Create new accounts (Checking or Savings) with initial deposit (minimum deposit enforced).
*   **Deposit:** Deposit funds into existing accounts.
*   **Withdrawal:** Withdraw funds from existing accounts (sufficient balance required; withdrawal limits for Savings accounts enforced).
*   **Fund Transfer:** Transfer funds between accounts (sufficient balance required).
*   **Balance Inquiry:** Check account balances.
*   **Transaction History:** View transaction history for specific accounts.
*   **Account Closure:** Close accounts (zero balance required).
*   **Data Persistence:** Account and transaction data are saved to files for later use.
*   **Error Handling:**  Handles invalid input and common banking errors (e.g., insufficient funds).
*   **Business Rules:** Enforces minimum deposits and savings account withdrawal limits.

## Technologies Used

*   Java
*   File I/O (Object Serialization)
*   Data Structures: `HashMap`, `ArrayList`

## Setup and Installation

1.  **Prerequisites:**
    *   Java Development Kit (JDK) - Version 8 or higher is recommended.
2.  **Clone the Repository:**
    ```bash
    git clone https://github.com/coderzzone/Banking-Console-App-With-Java
    cd BankingConsoleApp
    ```
3.  **Compile the Code:**
    ```bash
    javac BankingConsoleApp.java
    ```

## Usage

1.  **Run the Application:**
    ```bash
    java BankingConsoleApp
    ```
2.  **Interact with the Menu:**
    *   The application will present a menu of options.
    *   Enter the number corresponding to your desired action (e.g., `1` to create an account).
    *   Follow the prompts to provide the required information.

## Data Storage

Account and transaction data are stored in the following files:

*   `accounts.dat`: Contains account information.
*   `transactions.dat`: Contains transaction history.

**Note:** These files are created automatically when the application is first run and updated when data changes.

## Business Rules Implemented

*   **Minimum Initial Deposit:**
    *   Savings Account: $50
    *   Checking Account: $0
*   **Savings Account Withdrawal Limit:** Maximum of 6 withdrawals per month.

## Error Handling

The application includes error handling for common scenarios, such as:

*   Invalid input (e.g., entering text when a number is expected).
*   Account not found.
*   Insufficient funds for withdrawal or transfer.
*   Attempting to close an account with a non-zero balance.
*   Exceeding the withdrawal limit for a savings account.

## Contributing

Contributions to this project are welcome! To contribute:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes.
4.  Commit your changes with descriptive messages.
5.  Push your branch to your forked repository.
6.  Submit a pull request.

## Future Enhancements

*   Implement a graphical user interface (GUI).
*   Add support for multiple users.
*   Implement interest calculation for savings accounts.
*   Enhance security measures.
*   Implement transaction fees.
*   Database integration (e.g., using MySQL, PostgreSQL, or MongoDB).

## License

This project is licensed under the [MIT License](LICENSE) - see the `LICENSE` file for details.

## Author

[coderzzone]

## Contact

[yaficofi@gmail.com]

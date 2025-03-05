import java.io.*;
import java.util.*;

public class BankingConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Account> accounts = new HashMap<>(); // Account Number -> Account
    private static final List<Transaction> transactions = new ArrayList<>(); // Store transaction history
    private static final String ACCOUNTS_FILE = "accounts.dat";
    private static final String TRANSACTIONS_FILE = "transactions.dat";

    public static void main(String[] args) {
        loadData(); // Load accounts and transactions from file
        displayMainMenu();
        saveData(); // Save accounts and transactions to file before exiting
    }

    // --- File I/O ---

    private static void loadData() {
        loadAccounts();
        loadTransactions();
    }

    private static void saveData() {
        saveAccounts();
        saveTransactions();
    }

    private static void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCOUNTS_FILE))) {
            Map<String, Account> loadedAccounts = (Map<String, Account>) ois.readObject();
            accounts.clear(); // Clear existing accounts before loading
            accounts.putAll(loadedAccounts);
            System.out.println("Accounts loaded from " + ACCOUNTS_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("Accounts file not found. Starting with an empty account list.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
            // Handle the error appropriately - possibly exit or start with an empty map.
        }
    }

    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
            oos.writeObject(accounts);
            System.out.println("Accounts saved to " + ACCOUNTS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    private static void loadTransactions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTIONS_FILE))) {
            List<Transaction> loadedTransactions = (List<Transaction>) ois.readObject();
            transactions.clear(); // Clear existing transactions before loading
            transactions.addAll(loadedTransactions);
            System.out.println("Transactions loaded from " + TRANSACTIONS_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("Transactions file not found. Starting with an empty transaction list.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
            // Handle the error appropriately - possibly exit or start with an empty list.
        }
    }

    private static void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
            oos.writeObject(transactions);
            System.out.println("Transactions saved to " + TRANSACTIONS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }


    private static void displayMainMenu() {
        while (true) {
            System.out.println("\n--- Banking Console ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer Funds");
            System.out.println("5. Check Balance");
            System.out.println("6. View Transaction History");
            System.out.println("7. Close Account");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice = getValidIntInput();

            // Consume the newline character after reading the integer
            scanner.nextLine();

            switch (choice) {
                case 1 -> createAccountWorkflow();
                case 2 -> depositWorkflow();
                case 3 -> withdrawWorkflow();
                case 4 -> transferFundsWorkflow();
                case 5 -> checkBalanceWorkflow();
                case 6 -> viewTransactionHistoryWorkflow();
                case 7 -> closeAccountWorkflow();
                case 8 -> {
                    System.out.println("Exiting...");
                    saveData(); // Save data on exit
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createAccountWorkflow() {
        System.out.println("\n--- Create Account ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter initial deposit amount: ");
        double initialDeposit = getValidDoubleInput();

        // Select account type
        System.out.println("Select Account Type:");
        System.out.println("1. Savings");
        System.out.println("2. Checking");
        System.out.print("Enter your choice (1 or 2): ");
        int accountTypeChoice = getValidIntInput(1, 2);

        // Consume the newline character after reading the integer
        scanner.nextLine();

        AccountType accountType = (accountTypeChoice == 1) ? AccountType.SAVINGS : AccountType.CHECKING;

        String accountNumber = createAccount(name, address, phoneNumber, initialDeposit, accountType);
        if (accountNumber != null) {
            System.out.println("Account created successfully!");
            System.out.println("Account Number: " + accountNumber);
        }
    }


    private static String createAccount(String name, String address, String phoneNumber, double initialDeposit, AccountType accountType) {
        if (initialDeposit < getMinimumInitialDeposit(accountType)) {
            System.out.println("Error: Initial deposit must be at least $" + getMinimumInitialDeposit(accountType) + " for a " + accountType + " account.");
            return null; // Return null if account creation fails
        }

        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber, name, address, phoneNumber, initialDeposit, accountType);
        accounts.put(accountNumber, account);

        // Record initial deposit as a transaction
        Transaction initialDepositTransaction = new Transaction(accountNumber, TransactionType.DEPOSIT, initialDeposit, "Initial deposit");
        transactions.add(initialDepositTransaction);

        return accountNumber; // Return account number on success
    }

    private static void depositWorkflow() {
        System.out.println("\n--- Deposit ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter deposit amount: ");
        double amount = getValidDoubleInput();
        deposit(accountNumber, amount);
    }

    private static void deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount. Amount must be positive.");
            return;
        }

        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        account.setBalance(account.getBalance() + amount);
        Transaction transaction = new Transaction(accountNumber, TransactionType.DEPOSIT, amount, "Deposit");
        transactions.add(transaction);
        System.out.println("Deposit successful. New balance: $" + account.getBalance());
    }


    private static void withdrawWorkflow() {
        System.out.println("\n--- Withdraw ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter withdrawal amount: ");
        double amount = getValidDoubleInput();
        withdraw(accountNumber, amount);
    }

    private static void withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount. Amount must be positive.");
            return;
        }

        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        if (account.getBalance() < amount) {
            System.out.println("Insufficient funds.");
            return;
        }

        if (account.getAccountType() == AccountType.SAVINGS &&
            (transactions.stream().filter(t -> t.getAccountNumber().equals(accountNumber) && t.getTransactionType() == TransactionType.WITHDRAWAL).count() >= 6)) {
                System.out.println("Savings account withdrawal limit reached.  You can only make 6 withdrawals per month.");
                return;
        }


        account.setBalance(account.getBalance() - amount);
        Transaction transaction = new Transaction(accountNumber, TransactionType.WITHDRAWAL, amount, "Withdrawal");
        transactions.add(transaction);
        System.out.println("Withdrawal successful. New balance: $" + account.getBalance());
    }


    private static void transferFundsWorkflow() {
        System.out.println("\n--- Transfer Funds ---");
        System.out.print("Enter source account number: ");
        String sourceAccountNumber = scanner.nextLine();

        Account sourceAccount = accounts.get(sourceAccountNumber);
        if (sourceAccount == null) {
            System.out.println("Source account not found.");
            return;
        }

        System.out.print("Enter destination account number: ");
        String destinationAccountNumber = scanner.nextLine();

        Account destinationAccount = accounts.get(destinationAccountNumber);
        if (destinationAccount == null) {
            System.out.println("Destination account not found.");
            return;
        }

        System.out.print("Enter transfer amount: ");
        double amount = getValidDoubleInput();

        transferFunds(sourceAccountNumber, destinationAccountNumber, amount);
    }


    private static void transferFunds(String sourceAccountNumber, String destinationAccountNumber, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid transfer amount. Amount must be positive.");
            return;
        }

        Account sourceAccount = accounts.get(sourceAccountNumber);
        Account destinationAccount = accounts.get(destinationAccountNumber);

        if (sourceAccount == null || destinationAccount == null) {
            System.out.println("One or both accounts not found.");
            return;
        }

        if (sourceAccount.getBalance() < amount) {
            System.out.println("Insufficient funds in source account.");
            return;
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        Transaction transferOut = new Transaction(sourceAccountNumber, TransactionType.TRANSFER_OUT, amount, "Transfer to " + destinationAccountNumber);
        transactions.add(transferOut);

        Transaction transferIn = new Transaction(destinationAccountNumber, TransactionType.TRANSFER_IN, amount, "Transfer from " + sourceAccountNumber);
        transactions.add(transferIn);

        System.out.println("Transfer successful.");
        System.out.println("Source account new balance: $" + sourceAccount.getBalance());
        System.out.println("Destination account new balance: $" + destinationAccount.getBalance());
    }



    private static void checkBalanceWorkflow() {
        System.out.println("\n--- Check Balance ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("Account balance: $" + account.getBalance());
    }

    private static void viewTransactionHistoryWorkflow() {
        System.out.println("\n--- Transaction History ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        Account account = accounts.get(accountNumber);  // Try to retrieve account for history

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber().equals(accountNumber)) {
                System.out.println(transaction); // Assuming Transaction has a toString method
                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found for account number: " + accountNumber);
        }
    }

    private static void closeAccountWorkflow() {
        System.out.println("\n--- Close Account ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        // Implement additional checks if needed (e.g., outstanding loans).
        if (account.getBalance() > 0) {
            System.out.println("Please withdraw the remaining balance before closing the account.");
            return;
        }

        accounts.remove(accountNumber);
        System.out.println("Account closed successfully.");
    }


    // Utility methods
    private static String generateAccountNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Generate a random 8-character alphanumeric ID
    }

    private static int getValidIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                // Consume the newline character after reading the integer
               // scanner.nextLine();
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next(); // Clear the invalid input from the scanner
                 // Consume the newline character after reading the invalid input
                 scanner.nextLine();
            }
        }
    }

    private static int getValidIntInput(int min, int max) {
        while (true) {
            int input = getValidIntInput();
            if (input >= min && input <= max) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter an integer between " + min + " and " + max + ".");
                // Consume the newline character after reading the invalid input
               // scanner.nextLine();
            }
        }
    }


    private static double getValidDoubleInput() {
        while (true) {
            try {
                double value = scanner.nextDouble();
                scanner.nextLine(); // Consume the newline character
                if (value >= 0) { // Ensure input is non-negative for money
                    return value;
                } else {
                    System.out.println("Please enter a non-negative amount.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear the invalid input from the scanner
                scanner.nextLine(); // Consume the newline character
            }
        }
    }


    private static double getMinimumInitialDeposit(AccountType accountType) {
        return switch (accountType) {
            case SAVINGS -> 50.0;
            case CHECKING -> 0.0; // Some checking accounts might have no minimum
            default -> 0.0; // Default to zero in case of unexpected types
        };
    }


    // Data Classes
    static class Account implements Serializable { // Implement Serializable
        private final String accountNumber;
        private final String name;
        private final String address;
        private final String phoneNumber;
        private double balance;
        private final AccountType accountType;

        public Account(String accountNumber, String name, String address, String phoneNumber, double balance, AccountType accountType) {
            this.accountNumber = accountNumber;
            this.name = name;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.balance = balance;
            this.accountType = accountType;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public AccountType getAccountType() {
            return accountType;
        }

        @Override
        public String toString() {
            return "Account{" +
                   "accountNumber='" + accountNumber + '\'' +
                   ", name='" + name + '\'' +
                   ", balance=" + balance +
                   ", accountType=" + accountType +
                   '}';
        }
    }

    enum AccountType implements Serializable { // Implement Serializable
        SAVINGS,
        CHECKING
    }


    static class Transaction implements Serializable { // Implement Serializable
        private final String accountNumber;
        private final TransactionType transactionType;
        private final double amount;
        private final String description;
        private final String transactionId;

        public Transaction(String accountNumber, TransactionType transactionType, double amount, String description) {
            this.accountNumber = accountNumber;
            this.transactionType = transactionType;
            this.amount = amount;
            this.description = description;
            this.transactionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Generate transaction ID
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public TransactionType getTransactionType() {
            return transactionType;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public String getTransactionId() {
            return transactionId;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                   "transactionId='" + transactionId + '\'' +
                   ", accountNumber='" + accountNumber + '\'' +
                   ", transactionType=" + transactionType +
                   ", amount=" + amount +
                   ", description='" + description + '\'' +
                   '}';
        }
    }

    enum TransactionType implements Serializable { // Implement Serializable
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_IN,
        TRANSFER_OUT
    }
}

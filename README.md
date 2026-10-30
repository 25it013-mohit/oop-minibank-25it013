# MiniBank

## Description
MiniBank is a Java-based banking application that demonstrates object-oriented programming, account management, transaction processing, persistence, and reporting.

## Features
- Open savings, current, and fixed deposit accounts.
- Deposit and withdraw money.
- Transfer funds between accounts.
- Generate account statements.
- List bank accounts.
- Process batch transactions.
- Save and load account data.
- Generate transaction reports.
- Test banking operations using JUnit 5.

## Technologies Used
- Java
- Object-Oriented Programming
- Collections and Generics
- Multithreading
- Java Serialization
- JUnit 5

## How to Run
1. Ensure Java is installed.
2. Open a terminal in the project directory.
3. Run the JAR using:

   `java -jar out/artifacts/MiniBank_jar/MiniBank.jar`

   If the JAR is located elsewhere, use its actual path.

## Testing
JUnit tests cover:
- Deposit increases the balance.
- Withdrawal beyond available funds throws an exception.
- Concurrent deposits maintain the correct balance.
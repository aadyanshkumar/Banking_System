import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BankService bankService = new BankService();
        Scanner sc = new Scanner(System.in);

        int loggedInUserId = -1;

        while (true) {
            if (loggedInUserId == -1) {
                System.out.println("\n=== Welcome to Java Banking App ===");
                System.out.println("1. Create Account");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        bankService.createAccount();
                        break;
                    case 2:
                        loggedInUserId = bankService.login();
                        break;
                    case 3:
                        System.out.println("Thank you for using the Banking App. Goodbye!");
                        sc.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option! Try again.");
                }
            } else {
                // User is logged in
                System.out.println("\n=== Banking Menu ===");
                System.out.println("1. Deposit Money");
                System.out.println("2. Withdraw Money");
                System.out.println("3. View Transaction History");
                System.out.println("4. View Balance");
                System.out.println("5. Logout");
                System.out.print("Choose an option: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        bankService.depositMoney(loggedInUserId);
                        break;
                    case 2:
                        bankService.withdrawMoney(loggedInUserId);
                        break;
                    case 3:
                        bankService.viewTransactionHistory(loggedInUserId);
                        break;
                    case 4:
                        bankService.checkBalance(loggedInUserId);
                        break;
                    case 5:
                        System.out.println("Logging out...");
                        loggedInUserId = -1;
                        break;
                    default:
                        System.out.println("Invalid option! Try again.");
                }
            }
        }
    }
}


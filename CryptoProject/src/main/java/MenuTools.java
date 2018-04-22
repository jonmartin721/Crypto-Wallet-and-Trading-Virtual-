/*
This class contains useful console tools for this project. We need tools because we want this project
to be easy to use and the interface to be uncluttered. This class also contains the menu options
*/

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.coinbase.v2.CoinbaseExchange;
import org.knowm.xchange.coinbase.v2.dto.CoinbasePrice;
import org.knowm.xchange.coinbase.v2.service.CoinbaseMarketDataService;
import org.knowm.xchange.currency.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;


class MenuTools {

    // This method is called by menu before showing the menu to make sure the user has an account (viewWallet)
    // If not, they can create it here.
    // This is where Driver points to.
    //TODO add input processing (cleaning) method
    static void launchScreen() {

        title();

        //if the loginInfo.ser file doesn't exist, create a blank one, save it, and send user to account creation
        if (!FileOperations.checkLoginInfoExists()) {

            LoginInfo loginInfo = new LoginInfo();
            FileOperations.saveLoginInfo(loginInfo);
            createNewAccount();
        }

        Scanner keyboard = new Scanner(System.in);
        System.out.println("1) Login");
        System.out.println("2) Create Wallet");
        System.out.println("0) Exit");

        int response = keyboard.nextInt();

        //Input validation
        while (response != 1 && response != 2 && response != 0) {

            lineBreak();
            title();
            System.out.println("Invalid response!");
            System.out.println("1) Login");
            System.out.println("2) Create Wallet");
            System.out.println("0) Exit");


            response = keyboard.nextInt();
        }

        //If they pass input validation, they will be directed to the next area.

        // Send launchScreen to the right place depending on their input
        if (response == 1) {
            logIn();
        } else if (response == 2) {
            createNewAccount();
        } else {
            System.out.println("Exiting....");
            System.exit(0);
        }

    }

    // This method creates a new viewWallet
    private static void createNewAccount() {

        Scanner keyboard = new Scanner(System.in);
        //TODO ask for Username + Password first
        //TODO make sure the username is unique, else give message and ask for another

        // Gather information about the viewWallet
        lineBreak();
        System.out.println("Let's create a Wallet for you.");
        System.out.println("Please enter the following information:");
        System.out.print("First Name: ");
        String firstName = keyboard.next();
        System.out.print("Last Name: ");
        String lastName = keyboard.next();
        System.out.println("\nThank you. Now enter a username and password; this will be what you use to login in the future.");
        System.out.print("Username: ");
        String username = keyboard.next();
        System.out.print("Password: ");
        String password = keyboard.next();

        // Add the login pair to the LoginInfo object
        LoginInfo loginInfo = FileOperations.loadLoginInfo();
        loginInfo.addUserAndPassword(username, password);

        // Save the LoginInfo object
        if (FileOperations.saveLoginInfo(loginInfo)) {
            System.out.println("Login information saved.");
        } else {
            System.out.println("Login information could not be saved.");
            System.exit(0);
        }

        // Create the Wallet, SAVE IT, and send the user to the menu
        Wallet wallet = new Wallet(firstName, lastName, username);
        FileOperations.saveWallet(wallet);
        System.out.println("\nThanks " + firstName + ", wallet created!");
        System.out.println("You can set goals, trade, and browse currencies at the main menu. Going there now..");
        promptEnterKey();


        menu(wallet);
    }

    // This method handles logging in and is run before the menu is displayed.
    private static void logIn() {


        lineBreak();
        int loginAttempts = 1;
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Username: ");
        String username = keyboard.next();
        System.out.print("Password: ");
        String password = keyboard.next();

        loginAttempts++;

        // Load login info
        LoginInfo loginInfo = FileOperations.loadLoginInfo();
        // If they chose to login, and for some reason the size of loginInfo saved file is 0 (because it is new)
        // send them to create an account instead of infinitely denying them access
        if (loginInfo.returnSize() == 0) {
            createNewAccount();
        }

        // Not authenticated
        while (!loginInfo.isAuthenticated(username, password)) {

            if (loginAttempts > 3) {

                // If they enter a wrong combo 3 times, kick them out
                System.out.println("Too many attempts, exiting program.");
                System.exit(0);
            }

            System.out.println("Not authenticated, try again.");
            System.out.println("Attempt " + loginAttempts + " of 3.");
            System.out.print("Username: ");
            username = keyboard.next();
            System.out.print("Password: ");
            password = keyboard.next();
            loginAttempts++;
        }

        // Authenticated
        System.out.println("Authenticated!");
        System.out.println("Going to the menu now...");

        //Load the wallet matching their username
        Wallet wallet = null;
        try {
            wallet = FileOperations.loadWallet(username);
        } catch (IOException | ClassNotFoundException e) {
            FileOperations.printException();
            System.out.println("Can't find a wallet file, sending you to create a new account...");
            System.out.println("This happens when a wallet file was not saved properly.");
            createNewAccount();
        }


        if (wallet != null) {
            menu(wallet);
        } else {
            System.out.println("Could not load wallet from username: " + username);
        }


    }

    // After authentication or account creation, the program comes here.
    private static void menu(Wallet wallet) {

        //creates objects and variables for menu system
        int selection;
        Scanner keyboard = new Scanner(System.in);

        //outputs the menu options
        MenuTools.lineBreak();
        MenuTools.title();
        System.out.println("\nWelcome " + wallet.getFirstName() + ", or should I say: " + wallet.getUsername());
        System.out.println("Remember to save and exit when you are done so that your data is safely saved!");
        System.out.println("\nChoose an option below by typing the number:");
        System.out.println("1) View Wallet");
        System.out.println("2) Browse Currencies and Trade");
        System.out.println("3) Goals and Performance");
        System.out.println("4) Deposit USD");
        System.out.println("5) Withdraw USD");
        System.out.println("6) Change Password");
        System.out.println("7) Help and About");
        System.out.println("0) Save & Exit");
        System.out.println("\nUSD Balance: " + outputMoneyFormat(wallet.getUSDBalance()));
        //TODO add total holdings here

        //captures the user selection
        System.out.print("\nSelection? ");

        selection = keyboard.nextInt();

        boolean again=true;

        while (again) {

            switch (selection) {

                case 1:
                    MenuTools.lineBreak();
                    viewWallet(wallet);
                    break;

                case 2:
                    MenuTools.lineBreak();
                    performTrades(wallet);
                    break;

                case 3:
                    MenuTools.lineBreak();
                    goalsAndPerformance(wallet);
                    break;

                case 4:
                    MenuTools.lineBreak();
                    depositUSD(wallet);
                    break;

                case 5:
                    MenuTools.lineBreak();
                    withdrawUSD(wallet);
                    break;

                case 6:
                    MenuTools.lineBreak();
                    changePassword(wallet);
                    break;

                case 7:
                    MenuTools.lineBreak();
                    help(wallet);
                    break;

                case 0:
                    MenuTools.lineBreak();
                    System.out.println("Saving wallet to file...");

                    //saves the wallet
                    if (FileOperations.saveWallet(wallet)) {
                        System.out.println("Wallet saved!");
                    } else {
                        System.out.println("Wallet could not be saved.");
                        System.exit(0);
                    }

                    System.out.println("Exiting program...");
                    EchoClient.exitProgramAndServer();
//                    System.exit(0);
//                    break;

                default:
                    System.out.println("\nEnter a valid choice!");
                    selection = keyboard.nextInt(); // User is prompted to enter a choice again
                    again=true;
                    break;

            }

        }
    }

    // Shows basic summarized info about the current wallet.
    private static void viewWallet(Wallet wallet) {
        actionMessageBox("View Wallet Info");
        //Here we want to display summarized information on wallets. Show everything, but in a nice way. Doesn't have to be perfect.
        wallet.showWalletData();
        menu(wallet);
    }

    // Uses Coinbase exchange to output information
    private static void performTrades(Wallet wallet) {

        actionMessageBox("View and Trade");

        System.out.println("\nThe information below is from the Coinbase exchange, a widely trusted exchange. It may change very quickly.");
        System.out.println("Right now, there are just 4 cryptocurrencies, organized by symbol.");
        System.out.println("\nQUERIES:");
        System.out.println("Type the symbol to " +
                "trade or see more info about it, 'r' to reload all data, or 'q' to return to main menu.");

        //Init XChange resources w/ Coinbase
        Exchange coinbaseExchange =
                ExchangeFactory.INSTANCE.createExchange(CoinbaseExchange.class.getName());
        CoinbaseMarketDataService marketDataService =
                (CoinbaseMarketDataService) coinbaseExchange.getMarketDataService();

        //Here we have a list of popular cryptos
        ArrayList<String> cryptoList = new ArrayList<>();
        cryptoList.add("BTC");
        cryptoList.add("ETH");
        cryptoList.add("LTC");
        cryptoList.add("BCH");

        //setup table header
        String leftAlignFormat = "| %-20s     | %-6s     | %-13s |%n";
        System.out.format("\nExchange Rates:\n");
        System.out.format("+--------------------------+------------+---------------+%n");
        System.out.format("| Name                     | Symbol     | Value         |%n");
        System.out.format("+--------------------------+------------+---------------+%n");


        for (String aCryptoList : cryptoList) {
            Currency thisCurrency = new Currency(aCryptoList);

            //loop exchange data
            try {
                CoinbasePrice spotRate = marketDataService.getCoinbaseSpotRate(thisCurrency, Currency.USD);
                System.out.format(leftAlignFormat, thisCurrency.getDisplayName(), thisCurrency.getSymbol(), spotRate);
            } catch (IOException e) {
                System.out.println("Error: couldn't get info for this exchange.");
                e.printStackTrace();
            }
        }

        //ending line
        System.out.format("+--------------------------+------------+---------------+%n");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("\nLast updated: " + timestamp);

        System.out.println("\nQuery: ");
        Scanner keyboard = new Scanner(System.in);
        String query = keyboard.next();

        //while they have not entered anything valid
        query = query.toUpperCase();
        //TODO fix this while loop
        while (!isQueryValid(query)) {
            System.out.println("Invalid query. Type a symbol to see more info or trade, 'q' to exit to the menu, or 'r' to reload data.");
            System.out.println("Query: ");
            query = keyboard.next();
            return;
        }

        //after they have entered something valid, do something with the query
        switch (query) {
            case "Q":
                menu(wallet);
                break;
            case "R":
                performTrades(wallet);
                break;
            case "BTC":
                displayCryptoDetail(query);
                break;
            case "XLT":
                displayCryptoDetail("LTC");
                break;
            case "LTC":
                displayCryptoDetail(query);
                break;
            case "BCH":
                displayCryptoDetail(query);
                break;
            case "ETH":
                displayCryptoDetail(query);
                break;
        }


        promptEnterKey();


    }

    // Displays basic crypto info
    private static void displayCryptoDetail(String symbol) {

        Currency thisCurrency = new Currency(symbol);

        //TODO remove XChange library after refactoring to CryptoCompare API
        Exchange coinbaseExchange =
                ExchangeFactory.INSTANCE.createExchange(CoinbaseExchange.class.getName());
        CoinbaseMarketDataService marketDataService =
                (CoinbaseMarketDataService) coinbaseExchange.getMarketDataService();
        CoinbasePrice spotRate = null;

        try {
            spotRate = marketDataService.getCoinbaseSpotRate(thisCurrency, Currency.USD);
        } catch (IOException e) {
            networkException();
        }

        lineBreak();
        System.out.println(symbol + " details:");
        System.out.println("Name: " + thisCurrency.getDisplayName());
        System.out.println("Spot Rate: " + spotRate);
        System.out.println("\nWhat kind of trade?");
        System.out.println("1 - Buy with Crypto");
        System.out.println("2 - Buy with USD");

        //TODO finish this interface area
    }

    // This method lets users view and set goals as well as view performance.
    private static void goalsAndPerformance(Wallet wallet) {

        // Doing a nested switch statement to break out their Set goal/View goal/View performance options
        // 'GAP' represents the name of this method Goals And Performance
        Scanner keyboardGAP = new Scanner(System.in);
        int selectionGAP;
        System.out.println("Select your option below");
        System.out.println("1) Set your goals you'd like to achieve");
        System.out.println("2) View your goals as they are presently");
        System.out.println("3) View your performance");
        System.out.println("4) Return to main menu");

        selectionGAP = keyboardGAP.nextInt();

        switch (selectionGAP) {

            case 1:
                // User is setting their goals here
                MenuTools.lineBreak();
                System.out.println("Let's review some goals you'd like to set.");
                System.out.println("Enter the percentage of return you'd like to achieve:  ");

                Scanner keyboardGAP1 = new Scanner(System.in);
                float goal = keyboardGAP1.nextFloat();
                System.out.println("You input: " + goal + "%");
                System.out.println("To confirm, select 1-yes  0-no");
                int confirmInput = keyboardGAP1.nextInt();

                // Confirm user has input the amount of their goal
                while (confirmInput == 0) {
                    System.out.println("Re-enter the percentage of return you'd like to achieve:  ");
                    goal = keyboardGAP.nextFloat();
                    System.out.println("You input: " + goal + "%");
                    System.out.println("To confirm, select 1-yes  0-no");
                    confirmInput = keyboardGAP.nextInt();
                }
                // Basically typecasting user's goal to BigDecimal so it can be passed into setGoal() method in Wallet class
                BigDecimal confirmedUsersGoal = BigDecimal.valueOf(goal);
                // confirmedUsersGoal passes a percentage not a dollar amount
                wallet.setGoal(confirmedUsersGoal);
                promptEnterKey();
                goalsAndPerformance(wallet);
                break;

            case 2:
                // User is viewing the goals they set.
                MenuTools.lineBreak();
                System.out.println("Let's view the goals you set.");
                System.out.println("These are your holdings so far:  " + MenuTools.outputMoneyFormat(wallet.getTotalHoldings()));
                System.out.println();
                promptEnterKey();
                goalsAndPerformance(wallet);
                // JON- I chose getHoldings() because it returns an ArrayList of the Cryptocurriences
                // which I'm thinking has all the aggregate information they are looking for?
                break;

            case 3:
                // User is viewing their performance
                MenuTools.lineBreak();
                System.out.println("Let's view the your performance.");
                System.out.println("These are your holdings so far:  " + MenuTools.outputMoneyFormat(wallet.getTotalHoldings()));
                wallet.showTrades();
                promptEnterKey();
                goalsAndPerformance(wallet);
                break;

            case 4:
                // Returning user to main menu
                menu(wallet);

            default:
                System.out.println("\nExiting program...");
                //System.exit(0);
                EchoClient.exitProgramAndServer();
                //break;

        }


    }

    // Deposits USD to the wallet
    private static void depositUSD(Wallet wallet) {

        actionMessageBox("Deposit");

        BigDecimal previousBalance = wallet.getUSDBalance();
        System.out.println("\nUSD Balance: " + wallet.getUSDBalance());
        System.out.println("Enter amount to deposit: ");

        Scanner keyboard = new Scanner(System.in);
        BigDecimal amountDeposit = keyboard.nextBigDecimal();
        if (wallet.deposit(amountDeposit)) {
            System.out.println("Amount deposited successfully!");
            System.out.println("\nBefore: " + outputMoneyFormat(previousBalance));
            System.out.println("Deposited: " + outputMoneyFormat(amountDeposit));
            System.out.println("After: " + outputMoneyFormat(wallet.getUSDBalance()));
            promptEnterKey();
        } else {
            System.out.println("Amount not deposited, incorrect amount specified. Try again later.");
        }

        //saving the wallet for safety
        FileOperations.saveWallet(wallet);
        menu(wallet);
    }

    // Withdraws USD from the wallet
    private static void withdrawUSD(Wallet wallet) {

        actionMessageBox("Withdraw");

        BigDecimal previousBalance = wallet.getUSDBalance();
        System.out.println("\nUSD Balance: " + wallet.getUSDBalance());
        System.out.print("Enter amount to withdraw: ");

        Scanner keyboard = new Scanner(System.in);
        BigDecimal amountWithdraw = keyboard.nextBigDecimal();
        if (wallet.withdraw(amountWithdraw)) {
            System.out.println("Amount withdrawn successfully!");
            System.out.println("\nBefore: " + outputMoneyFormat(previousBalance));
            System.out.println("Withdrawn: " + outputMoneyFormat(amountWithdraw));
            System.out.println("After: " + outputMoneyFormat(wallet.getUSDBalance()));
            promptEnterKey();
        } else {
            System.out.println("Amount not withdrawn, incorrect amount specified. Try again later.");
        }

        //saving the wallet for safety
        FileOperations.saveWallet(wallet);
        menu(wallet);
    }

    // Changes a user's password.
    private static void changePassword(Wallet wallet) {
        actionMessageBox("Change Password");

        Scanner keyboard = new Scanner(System.in);

        System.out.print("Enter a new password: ");
        String newPassword = keyboard.next();
        System.out.print("Enter again: ");

        //while the second password entered doesn't equal the first
        while (!keyboard.next().equals(newPassword)) {
            System.out.println("Passwords do not match. Try again.");

            System.out.print("Enter a new password: ");
            newPassword = keyboard.next();
            System.out.print("Enter again: ");
        }

        if (wallet.changePassword(newPassword)) {
            System.out.println("\nPassword changed!");
        } else {
            System.out.println("\nPassword not changed!");
        }

        menu(wallet);

    }

    // Information about the program and cryptocurrencies
    private static void help(Wallet wallet) {

        actionMessageBox("Help");

        System.out.println("\nThis application is a VIRTUAL trading application that is both a proof of concept, and " +
                "a working trade application.");
        System.out.println("This application was created for our Java class, but can eventually be" +
                " adapted to perform a wider variety of tasks.");
        System.out.println("\nMenu Help: ");
        System.out.println("1) View Wallet - Shows a variety of wallet information displayed as a summary.");
        System.out.println("2) Browse Currencies and Trade - Lets you view and trade cryptocurrencies");
        System.out.println("3) Goals and performance - This function allows the user to enter the percentage of return " +
                "they would like to achieve.");
        System.out.println("It keeps track of the goals set, and allows the user to view their performance" +
                " as they trade and invest.");
        System.out.println("4) Deposit USD - Deposit an amount of USD to your wallet. (VIRTUAL)");
        System.out.println("5) Withdraw USD - Withdraw an amount of USD to an external bank. (VIRTUAL)");
        System.out.println("6) Change Password - Password changing.");
        System.out.println("7) Help and About - Loads the help menu (this menu).");
        System.out.println("0) Save and Exit - Saves trading information to wallet and exits.");

        System.out.println("\nResources used:");
        System.out.println("- XChange");
        System.out.println("- Coinbase Exchange API");
        System.out.println("- Lots of Google!");
        System.out.println("\n### Group members ###" +
                "\nJonathan Martin - Chief Programmer" +
                "\nAmee Stevenson - All Purpose Programmer" +
                "\nBhagyalakshmi Muthucumar - Documentation and Implementation");


        //saving the wallet for safety
        FileOperations.saveWallet(wallet);
        promptEnterKey();
        menu(wallet);

    }

    // Returns a properly formatted currency string depending on locale.
    static String outputMoneyFormat(BigDecimal n) {
        return NumberFormat.getCurrencyInstance().format(n);
    }

    // This makes the method continue when enter is pressed.
    static void promptEnterKey() {

        System.out.print("Press enter to continue...");
        Scanner enterKey = new Scanner(System.in);
        enterKey.nextLine();
    }

    // Takes a string and outputs a pretty box at the top of any selected menu landing option
    private static void actionMessageBox(String d) {

        int responseLength1,            // captures the length of the string input
                responseLength2,        // I'm adding the #-signs to a total of 18
                numOfSpaceOnEachSide,   // contains the number of spaces on each side
                leftSide,               // just clarifies within code the space added is on leftside
                rightSide,              // just clarifies within code the space added is on leftside
                totalLength;            // contains total length of formatted output

        responseLength1 = d.length();

        // Adding the default space require for output
        responseLength2 = responseLength1 + 18;

        // Tells how many spaces on each side space is needed
        numOfSpaceOnEachSide = ((responseLength2 - responseLength1) / 2);

        // Calculate the number needed to format the output with spacing including the string
        leftSide = numOfSpaceOnEachSide + (responseLength1 - 2);

        // The actual formatting syntax for leftside
        String space1 = String.format("#" + "%" + leftSide + "s", d);

        // This is redundant but there for the sake of knowing which side your working with
        rightSide = leftSide;

        // This combats the spacing on the rightside of the string input
        rightSide = ((rightSide / 5) * 2);

        // The actual formatting syntax for rightside
        String space2 = String.format("%" + rightSide + "s", "#");

        // Concatenates the entire string
        String space3 = space1 + space2;
        totalLength = space3.length();

        // Here is the formatted output
        for (int x = 0; x < totalLength; x++) {
            System.out.print("#");
        }
        System.out.println();
        System.out.println(space3);
        for (int x = 0; x < totalLength; x++) {
            System.out.print("#");
        }
        System.out.println();
    }

    // Adds a nice separator to different activities, doesn't clear the screen
    private static void lineBreak() {

        System.out.println("\n-----------------------------------------------------------");
        System.out.println("///////////////////////////////////////////////////////////");
        System.out.println("-----------------------------------------------------------\n");

    }

    // Outputs the title and version of the program.
    private static void title() {
        System.out.println("Virtual Cryptocurrency Wallet and Trading v0.50");
    }

    // Used for wallet data viewing
    static void lineDivider() {
        System.out.println("=====================================================");
    }

    static void longerLineDivider(){
        System.out.println("==================================================================================================");
    }

    // Handles query processing for the browsing and trading area
    //TODO redo query validation for CryptoCompare API
    private static boolean isQueryValid(String query) {

        switch (query) {
            case "Q":
                return true;
            case "R":
                return true;
            case "BTC":
            case "LTC":
            case "ETH":
            case "XLT":
            case "BCH":
                return true;
        }

        return false;
    }

    static void networkException() {
        System.out.println("Could not get network data.");
    }
}


/*
This class contains useful console tools for this project. We need tools because we want this project
to be easy to use and the interface to be uncluttered. This class also contains the menu options
*/

import POJOs.SingleCryptoData;
import Service.APICalls;
import Service.RequestData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;


class MenuTools {

    // This method is called by menu before showing the menu to make sure the user has an account (viewWallet)
    // If not, they can create it here.
    static void launchScreen() {

        lineBreak();
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

            title();
            System.out.println("\nInvalid response!");
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
        //Review- TODO ask for Username + Password first
        //TODO make sure the username is unique, else give message and ask for another

        // Gather information about the viewWallet
        lineBreak();
        System.out.println("Let's create a Wallet for you.");
        System.out.println("Please enter the following information:");
        System.out.println("\nEnter a username and password; this will be what you use to login in the future.");
        System.out.print("Username: ");
        String username = keyboard.next();

        //Check if username is unique. Otherwise request new username
        FileOperations.checkUsername(username);

        System.out.print("Password: ");
        String password = keyboard.next();
        System.out.print("First Name: ");
        String firstName = keyboard.next();
        System.out.print("Last Name: ");
        String lastName = keyboard.next();




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

        //saves wallet
        FileOperations.saveWallet(wallet);

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
        BigDecimal holdingsValue = wallet.getTotalHoldings();
        System.out.println("Total Holdings Value (less USD): " + outputMoneyFormat(holdingsValue));
        System.out.println("Total Wallet Value: " + outputMoneyFormat(wallet.getUSDBalance().add(holdingsValue)));

        //captures the user selection
        System.out.print("\nSelection? ");

        selection = keyboard.nextInt();

        switch (selection) {

            case 1:
                MenuTools.lineBreak();
                viewWallet(wallet);
                break;

            case 2:
                MenuTools.lineBreak();
                viewAndTrade(wallet);
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
                System.exit(0);
                break;

            default:
                System.out.println("\nInvalid choice. Enter a valid choice next time!");
                promptEnterKey();
                menu(wallet);
                break;

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
    private static void viewAndTrade(Wallet wallet) {

        //saves the wallet
        if (FileOperations.saveWallet(wallet)) {
            System.out.println("Wallet saved!");
        } else {
            System.out.println("Wallet could not be saved.");
        }

        actionMessageBox("View and Trade");

        System.out.println("\nThe information below is from the CryptoCompareAPI exchange, a widely trusted data source. Data may change quickly.");
        System.out.println("Below are the top 11 cryptocurrencies by market cap (as of April 26th, 2018).");
        System.out.println("Loading....");

        ArrayList<SingleCryptoData> cryptos;

        try {
            cryptos = APICalls.getFullData();
        } catch (IOException e) {
            e.printStackTrace();
            cryptos = null;
        }
        //setup table header
        String leftAlignFormat = "| %-15s  | %-6s     | %-13s | %-9s    | %-9s    |%n";


        System.out.format("\nExchange Rates:\n");
        System.out.format("+------------------+------------+---------------+--------------+--------------+%n");
        System.out.format("| Name             | Symbol     | Value (USD)   | 24H Change   | Amount Held  |%n");
        System.out.format("+------------------+------------+---------------+--------------+--------------+%n");


        if (cryptos != null) {
            for (int i = 0; i < cryptos.size(); i++) {
                System.out.format(leftAlignFormat,
                        cryptos.get(i).getName(),                               //name
                        cryptos.get(i).getRaw().getFromSymbol(),                //symbol
                        "$" + cryptos.get(i).getRaw().getPrice().toString(),    //price
                        (cryptos.get(i).getRaw().getChangePercent24Hour() > 0) ? "+" + MenuTools.outputTwoDecimalFormat(cryptos.get(i).getRaw().getChangePercent24Hour()) +
                                "%" : MenuTools.outputTwoDecimalFormat(cryptos.get(i).getRaw().getChangePercent24Hour()) + "%",
                        MenuTools.outputTwoDecimalFormat(wallet.getHoldings().get(i).getAmountHeld()));     //24 hour change
            }
        }


        //ending line
        System.out.format("+------------------+------------+---------------+--------------+--------------+%n");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("\nLast updated: " + timestamp);
        System.out.println("\nUSD Balance: " + outputMoneyFormat(wallet.getUSDBalance()));
        System.out.println("\nQUERIES:");
        System.out.println("Type the symbol to " +
                "trade or see more info about it, 'r' to reload all data, or 'q' to return to main menu.");
        System.out.println("\nQuery: ");
        Scanner keyboard = new Scanner(System.in);
        String query = keyboard.next();

        //while they have not entered anything valid, keep asking
        query = query.toUpperCase();
        while (!isQueryValid(query)) {
            System.out.println("Invalid query. Type a symbol to see more info or trade, 'q' to exit to the menu, or 'r' to reload data.");
            System.out.println("Query: ");
            query = keyboard.next().toUpperCase();
        }

        //after they have entered something valid, do something with the query
        switch (query) {
            case "Q":
                menu(wallet);
                break;
            case "R":
                viewAndTrade(wallet);
                break;
            default:
                displayCryptoDetail(query, wallet);
                break;
        }


        promptEnterKey();


    }

    // Displays basic crypto info
    private static void displayCryptoDetail(String symbol, Wallet wallet) {

        lineBreak();
        //get the individual crypto data from API
        try {
            ArrayList<SingleCryptoData> cryptos = APICalls.getFullData();

            SingleCryptoData targetCrypto = new SingleCryptoData();

            for (SingleCryptoData crypto : cryptos) {

                if (crypto.getRaw().getFromSymbol().equals(symbol)) {
                    targetCrypto = crypto;
                }

            }

            int cryptoPosition = -1;
            BigDecimal amountHeld = new BigDecimal(0);
            ArrayList<Cryptocurrency> holdings;
            holdings = wallet.getHoldings();
            //Pull info on how much the user holds of this crypto
            for (int i = 0; i < holdings.size(); i++) {
                if (holdings.get(i).getSymbol().equals(symbol)) {
                    amountHeld = holdings.get(i).getAmountHeld();
                    cryptoPosition = i;
                    break;
                }
            }


            //display a LOT of data on the crypto

            //Total
            System.out.println("Name:           " + targetCrypto.getName());
            System.out.println("Symbol:         " + targetCrypto.getRaw().getFromSymbol());
            System.out.println("Price:          " + outputMoneyFormat(targetCrypto.getRaw().getPrice()));
            System.out.println("Total Volume:   " + outputTwoDecimalFormat(targetCrypto.getRaw().getLastVolumeTotal()));
            System.out.println("Market Cap:     " + outputTwoDecimalFormat(targetCrypto.getRaw().getMarketCap()));
            System.out.println("Supply:         " + outputTwoDecimalFormat(targetCrypto.getRaw().getSupply()));
            //24 Hour
            System.out.println("24H Change:     " + outputTwoDecimalFormat(targetCrypto.getRaw().getChange24Hour()));
            System.out.println("24H Change %:   " + outputTwoDecimalFormat(targetCrypto.getRaw().getChangePercent24Hour()) + "%");
            System.out.println("24H Open:       " + outputTwoDecimalFormat(targetCrypto.getRaw().getOpen24Hour()));
            System.out.println("24H High:       " + outputTwoDecimalFormat(targetCrypto.getRaw().getHigh24Hour()));
            System.out.println("24H Low:        " + outputTwoDecimalFormat(targetCrypto.getRaw().getLow24Hour()));
            System.out.println("24H Volume:     " + outputTwoDecimalFormat(targetCrypto.getRaw().getVolume24Hour()));
            //Options
            System.out.println("\nAmount held:  " + outputSixDecimalFormat(amountHeld));
            System.out.println("Value of amount held in USD: " + outputMoneyFormat(amountHeld.multiply(BigDecimal.valueOf(targetCrypto.getRaw().getPrice()))));
            System.out.println("\nOptions:");
            System.out.println("\n1) Buy with USD");
            System.out.println("2) Sell to USD");
            System.out.println("0) Return to browse");

            //take query
            int input;
            boolean result;
            Scanner keyboard = new Scanner(System.in);
            input = keyboard.nextInt();
            while (input != 1 && input != 2 && input != 0) {

                System.out.println("Invalid choice.");
                System.out.println("\nOptions:");
                System.out.println("\n1) Buy with USD");
                System.out.println("2) Sell to USD");
                System.out.println("0) Return to browse");
                input = keyboard.nextInt();
            }

            switch (input) {

                case 0:
                    viewAndTrade(wallet);
                    break;
                case 1:
                    result = Trade.tradeUsdToCrypto(cryptoPosition, wallet);
                    if (result) {
                        System.out.println("Trade successful! Wallet updated.");
                        promptEnterKey();
                    } else {
                        System.out.println("Trade not successful. Make sure to be entering a valid amount.");
                        promptEnterKey();
                    }
                    viewAndTrade(wallet);
                    break;
                case 2:
                    result = Trade.tradeCryptoToUSD(cryptoPosition, wallet);
                    if (result) {
                        System.out.println("Trade successful! Wallet updated.");
                        promptEnterKey();
                    } else {
                        System.out.println("Trade not successful. Make sure to be entering a valid amount.");
                        promptEnterKey();
                    }

                    viewAndTrade(wallet);
                    break;
            }


        } catch (IOException e) {
            e.printStackTrace();
            APICalls.apiError();
            viewAndTrade(wallet);
        }

    }

    // This method lets users view and set goals as well as view performance.
    private static void goalsAndPerformance(Wallet wallet) {

        // Doing a nested switch statement to break out their Set goal/View goal/View performance options
        // 'GAP' represents the name of this method Goals And Performance
        Scanner keyboardGAP = new Scanner(System.in);
        int selectionGAP;
        System.out.println("Select your option below:");
        System.out.println("\n1) Set your goals you'd like to achieve");
        System.out.println("2) View your goals as they are presently");
        System.out.println("3) View your performance");
        System.out.println("0) Return to main menu");

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
                System.out.println("Confirm?");
                System.out.println("\n1) Yes");
                System.out.println("0) No");
                int confirmInput = keyboardGAP1.nextInt();

                // Confirm user has input the amount of their goal
                while (confirmInput == 0) {
                    System.out.println("Re-enter the percentage of return you'd like to achieve:  ");
                    goal = keyboardGAP.nextFloat();
                    System.out.println("You input: " + goal + "%");
                    System.out.println("To confirm, select 1-yes  0-no");
                    confirmInput = keyboardGAP.nextInt();

                }

                // If goal percentage has not been set, it will show a message
                if (!wallet.setGoal(BigDecimal.valueOf(goal))) {

                    System.out.println("Success in setting goal percentage!!");

                } else {
                    System.out.println("Goal percentage not set. Re-enter your goal percentage!");
                    goal = keyboardGAP1.nextInt();

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
                break;

            case 0:
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
                break;

            default:
                System.out.println("Invalid choice. Enter a correct choice next time!");
                goalsAndPerformance(wallet);
                break;

        }


    }

    // Deposits USD to the wallet
    private static void depositUSD(Wallet wallet) {

        actionMessageBox("Deposit");

        BigDecimal previousBalance = wallet.getUSDBalance();
        System.out.println("\nUSD Balance: " + outputMoneyFormat(previousBalance));
        System.out.println("Enter amount to deposit: ");

        Scanner keyboard = new Scanner(System.in);
        BigDecimal amountDeposit = keyboard.nextBigDecimal();
        FileOperations.inputClean(String.valueOf(amountDeposit));// cleans the input

        if (wallet.deposit(amountDeposit)) {
            System.out.println("Amount deposited successfully!");
            System.out.println("\nBefore: " + outputMoneyFormat(previousBalance));
            System.out.println("Deposited: " + outputMoneyFormat(amountDeposit));
            System.out.println("After: " + outputMoneyFormat(wallet.getUSDBalance()));
            wallet.setTotalUsdDeposited(wallet.getTotalUsdDeposited().add(amountDeposit)); //updates the total amount deposited
            promptEnterKey();
        } else {
            System.out.println("Amount not deposited, incorrect amount specified. Try again later.");
        }

        //saving the wallet for safety
        menu(wallet);
    }

    // Withdraws USD from the wallet
    private static void withdrawUSD(Wallet wallet) {

        actionMessageBox("Withdraw");

        BigDecimal previousBalance = wallet.getUSDBalance();
        System.out.println("\nUSD Balance: " + outputMoneyFormat(previousBalance));
        System.out.print("Enter amount to withdraw: ");

        Scanner keyboard = new Scanner(System.in);
        BigDecimal amountWithdraw = keyboard.nextBigDecimal();
        FileOperations.inputClean(String.valueOf(amountWithdraw));// cleans the input

        if (wallet.withdraw(amountWithdraw)) {
            System.out.println("Amount withdrawn successfully!");
            System.out.println("\nBefore: " + outputMoneyFormat(previousBalance));
            System.out.println("Withdrawn: " + outputMoneyFormat(amountWithdraw));
            System.out.println("After: " + outputMoneyFormat(wallet.getUSDBalance()));
            wallet.setTotalUsdWithdrawn(wallet.getTotalUsdWithdrawn().add(amountWithdraw)); //updates the total amount withdrawn
            promptEnterKey();
        } else {
            System.out.println("Amount not withdrawn, incorrect amount specified. Try again later.");
        }


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
        System.out.println("- CryptoCompareAPI");
        System.out.println("- Retrofit");
        System.out.println("- Gson");
        System.out.println("- Lots of Google!");
        System.out.println("\n### Group members ###" +
                "\nJonathan Martin - Chief Programmer" +
                "\nAmee Stevenson - All Purpose Programmer" +
                "\nBhagyalakshmi Muthucumar - Documentation and QA");


        //saving the wallet for safety
        FileOperations.saveWallet(wallet);
        promptEnterKey();
        menu(wallet);

    }

    // Returns a properly formatted currency string depending on locale.
    static String outputMoneyFormat(BigDecimal n) {
        return NumberFormat.getCurrencyInstance().format(n);
    }

    // Returns a properly formatted currency string depending on locale. (Uses double)
    static String outputMoneyFormat(Double n) {
        return NumberFormat.getCurrencyInstance().format(n);
    }

    // Returns a two decimal formatted result from a double.
    private static String outputTwoDecimalFormat(Double n) {
        NumberFormat twoDecimalFormat = DecimalFormat.getInstance(Locale.US);
        twoDecimalFormat.setRoundingMode(RoundingMode.FLOOR);
        twoDecimalFormat.setMinimumFractionDigits(2);
        twoDecimalFormat.setMaximumFractionDigits(2);

        return twoDecimalFormat.format(n);
    }

    private static String outputTwoDecimalFormat(BigDecimal n) {
        NumberFormat twoDecimalFormat = DecimalFormat.getInstance(Locale.US);
        twoDecimalFormat.setRoundingMode(RoundingMode.FLOOR);
        twoDecimalFormat.setMinimumFractionDigits(2);
        twoDecimalFormat.setMaximumFractionDigits(2);

        return twoDecimalFormat.format(n);
    }

    static String outputSixDecimalFormat(BigDecimal n) {
        NumberFormat sixDecimalFormat = DecimalFormat.getInstance(Locale.US);
        sixDecimalFormat.setRoundingMode(RoundingMode.FLOOR);
        sixDecimalFormat.setMinimumFractionDigits(2);
        sixDecimalFormat.setMaximumFractionDigits(2);
        return sixDecimalFormat.format(n);
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
        System.out.println("Virtual Cryptocurrency Wallet and Trading v1.0");
    }

    // Handles query processing for the browsing and trading area
    private static boolean isQueryValid(String query) {

        switch (query) {
            case "Q": //go back to the main menu
                return true;
            case "R": //reload
                return true;
        }

        //if the query is a symbol
        ArrayList<String> cryptoList = RequestData.getCryptoList();
        for (String aCryptoList : cryptoList) {
            if (aCryptoList.equals(query))
                return true;
        }

        return false;
    }
}


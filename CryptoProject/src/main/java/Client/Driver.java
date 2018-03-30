package Client;

/*
This class should be the driver and feature methods that use the other classes in the application.
 */

import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {

        //main is just the starting point for this application and doesn't do anything other than call menu();
        menu();

    }

    //has the user choose what they want to do
    //TODO move menu choosing into ConsoleUtils
    private static void menu() {

        //creates objects and variables for menu system
        int selection;
        Scanner keyboard = new Scanner(System.in);

        //outputs the menu options
        ConsoleUtils.lineBreak();
        System.out.println("Simulated Client.Cryptocurrency Client.Wallet and Trading v0.01");
        System.out.println("\nChoose an option below by typing the number:");
        System.out.println("1) Browse Currencies");
        System.out.println("2) View Client.Wallet");
        System.out.println("3) Trading");
        System.out.println("4) Use Indicators");
        System.out.println("5) Help");
        System.out.println("0) Exit");

        //captures the user selection
        System.out.print("\nSelection? ");

        selection = keyboard.nextInt();

        switch (selection) {

            case 0:
                ConsoleUtils.lineBreak();
                System.out.println("Exiting program...");
                System.exit(0);
                break;

            case 1:
                ConsoleUtils.lineBreak();
                browse();
                break;

            case 2:
                ConsoleUtils.lineBreak();
                wallet();
                break;

            case 3:
                ConsoleUtils.lineBreak();
                trading();
                break;

            case 4:
                ConsoleUtils.lineBreak();
                indicators();
                break;

            case 5:
                ConsoleUtils.lineBreak();
                help();
                break;

            default:
                System.out.println("\nEnter a valid choice!");
                menu();
                break;

        }


    }

    //TODO move all the below methods into ConsoleUtils
    //this method will help the user to browse historical and current values of cryptocurrencies using CCXT
    private static void browse() {

        System.out.println("Loading cryptocurrency browser ...");
        ConsoleUtils.underConstruction();
        System.out.println("This section of the program will show current and past cryptocurrency values.");
        System.out.println("Press enter to return to the menu.");
        ConsoleUtils.promptEnterKey();
        menu();

    }

    //this holds wallet information (funds, currencies)
    private static void wallet() {

        System.out.println("Opening wallet ...");
        ConsoleUtils.underConstruction();
        System.out.println("This section will show you your wallet and funds.");
        System.out.println("Press enter to return to the menu.");
        ConsoleUtils.promptEnterKey();
        menu();
    }

    //this has trading views and options to buy and sell
    private static void trading() {

        System.out.println("Launching trading system ...");
        ConsoleUtils.underConstruction();
        System.out.println("This section will allow you to buy and sell cryptocurrencies.");
        System.out.println("Press enter to return to the menu.");
        ConsoleUtils.promptEnterKey();
        menu();

    }

    //this has custom algorithms to help people predict what to do
    private static void indicators() {

        System.out.println("Loading Indicators ...");
        ConsoleUtils.underConstruction();
        System.out.println("This section will use algorithms to help you make better decisions.");
        System.out.println("Press enter to return to the menu.");
        ConsoleUtils.promptEnterKey();
        menu();

    }

    //this has information about the program and cryptocurrencies
    private static void help() {

        System.out.println("Loading help ...");
        ConsoleUtils.underConstruction();
        System.out.println("This section will display About and Help information.");
        System.out.println("Press enter to return to the menu.");
        ConsoleUtils.promptEnterKey();
        menu();

    }

}
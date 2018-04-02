/*
The wallet is a the class that is essentially the user profile; it is the container for this entire application.
Each wallet object has funding, holdings, personal information, trade history, and goals.
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class Wallet {

    private ArrayList<Cryptocurrency> holdings;
    private UUID walletID;
    private String username;
    private EncryptionService encryption;
    private String firstName;
    private String lastName;
    private ArrayList<Trade> trades;
    private byte[] password;
    private BigDecimal totalAmountTraded;
    private BigDecimal totalUSDdeposited;
    private BigDecimal totalUSDwithdrawn;
    private BigDecimal USDBalance;

    // Parameterized constructor
    // The user will always supply a username, first name, and last name.
    public Wallet(String firstName, String lastName, String username) {
        holdings = new ArrayList<>();
        walletID = UUID.randomUUID();
        this.username = username;
        encryption = new EncryptionService();
        this.firstName = firstName;
        this.lastName = lastName;
        trades = new ArrayList<>();
        password = new byte[0];
        USDBalance = new BigDecimal(0);
        totalAmountTraded = new BigDecimal(0);
        totalUSDdeposited = new BigDecimal(0);
        totalUSDwithdrawn = new BigDecimal(0);

    }

    // this method adds up the usd value of each cryptocurrency (current value * amount held) and returns it. it does NOT
    // include USD.
    public BigDecimal getTotalHoldings() {

        BigDecimal totalValue = new BigDecimal(0);

        for (Cryptocurrency holding : holdings) {
            totalValue = totalValue.add(holding.getCurrentHoldingValue());
        }
        return totalValue;
    }

    // this method adds an amount to the usd. They can always add money, no check needed.
    public boolean deposit(BigDecimal addAmount) {
        USDBalance = USDBalance.add(addAmount);
        return true;
    }

    // this method checks that the user can withdraw, then if they can it does it. otherwise returns false.
    public boolean withdraw(BigDecimal withdrawAmount) {
        if (USDBalance.compareTo(withdrawAmount) >= 0) {
            USDBalance = USDBalance.subtract(withdrawAmount);
            return true;
        } else {
            return false;
        }
    }

    // This method shows basic info on each trade.
    public void showTrades() {

        trades.forEach(trade -> {
            System.out.println("Trade ID: " + String.valueOf(trade.getTradeID()));
            System.out.println("Date of Trade: " + String.valueOf(trade.getDateTime()));
            System.out.println("From: " + String.valueOf(trade.getFromSymbol()));
            System.out.println("To: " + String.valueOf(trade.getToSymbol()));
            System.out.println("Amount: " + String.valueOf(trade.getFromAmount().subtract(trade.getToAmount())));

        });


    }

    //TODO Set password in Wallet using this
    public boolean setPassword(EncryptionService object) {
        // this.password = new byte[object.getEncryptedAttemptedPassword()]; ??
        // return true;
        // return false;
        // Jon - leave this to me. I'll be helping.
        return true;

    }

    //TODO Change password (no confirmation required)
    public boolean changePassword() {
        // ASSUMING THIS WILL CONNECT WITH PasswordEncryptionService class?
        // COME BACK TO THIS
        // Jon - this calls its own object of the PasswordEncryptionService. This will simply ask for and set a new pass.

        return true;
    }

    //TODO Set goals based on comments below.
    public boolean setGoal(BigDecimal goal) {

        return true;
    }


    //TODO (Review)Show important wallet data.
    public void showWalletData() {

    }

    public ArrayList<Cryptocurrency> getHoldings() {
        return new ArrayList<>();
    }

    public void setHoldings(ArrayList<Cryptocurrency> holdings) {
        this.holdings = holdings;
    }

    public BigDecimal getUSDBalance() {
        return USDBalance;
    }


    public UUID getWalletID() {
        return walletID;
    }


    public String getUsername() {
        return username;
    }

}

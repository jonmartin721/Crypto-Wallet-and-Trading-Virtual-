/*
This class is instantiated and put in every Wallet as a cryptocurrency held by that user. It contains all the info needed
on each cryptocurrency.
 */

import jdk.jfr.Percentage;

import java.math.BigDecimal;

public class Cryptocurrency extends Currency {


    private String symbol;
    private BigDecimal currentValue;
    private Percentage last4Hours;

    //TODO Implement API (XChange)
    //this method returns from the API what the current value of this crypto is
    public BigDecimal getCurrentCryptoValue() {

        return new BigDecimal(0); //0 for now
    }

    //this methods multiplies current crypto value by amountHeld and returns the value in USD of this holding
    public BigDecimal getCurrentHoldingValue() {

        BigDecimal holdingValue;
        holdingValue = this.getCurrentCryptoValue();
        holdingValue = holdingValue.multiply(BigDecimal.valueOf(getAmountHeld()));
        return holdingValue;

    }

    public Double getAmountHeld() {
        return amountHeld;
    }

    public void setAmountHeld(Double amountHeld) {
        this.amountHeld = amountHeld;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}

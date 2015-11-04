package Finance;

import java.text.DecimalFormat;

/**
 * Created by guyazran on 11/4/15.
 * Money is a class that represents an amount of money and it's currency
 */
public class Money {
    private double amount;
    private Currency currency;

    public Money(double amount, Currency currency){
        this.amount = amount;
        this.currency = currency;
    }

    public Money(){
        this(0, Currency.USD);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("####0.00");
        return df.format(amount) + currency.toString();
    }
}


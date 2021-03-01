package cashew.domain;

import cashew.error.InvalidParamsException;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CARD")
public class CardPayment extends Payment{
    
    @Column(length = 19)
    private String number;

    @Column(length = 7)
    private String expiry;

    @Column(length = 3)
    private String cvv;
    
    static final int CVV_SIZE = 3;
    static final int CURRENT_YEAR = 2021;

    public CardPayment(){
        super();
    }

    public CardPayment(Map<String, String> params){
        super(params);
        number = params.get("number");
        cvv = params.get("cvv");
        expiry = params.get("expiry");
    }

    public String getNumber(){
        return this.number;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public String getExpiry(){
        return this.expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv(){
        return this.cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public boolean validate() throws InvalidParamsException {
        super.checkFields();
        if(number == null) {
            throw new InvalidParamsException("Card number is required");
        }
        if(expiry == null) {
            throw new InvalidParamsException("Card expiry date is required");
        }
        if(cvv == null) {
            throw new InvalidParamsException("Card CVV is required");
        }

        if(cvv.length() != CVV_SIZE) {
            throw new InvalidParamsException("Invalid CVV.");
        }
        
        if(!(number.length() == 16 || number.length() == 19)) {
            throw new InvalidParamsException("Invalid card number.");
        }

        String[] dates = expiry.split("/");
        
        if(Integer.parseInt(dates[1]) < CURRENT_YEAR){
            throw new InvalidParamsException("Invalid card. Already expired");
        }

        if(Integer.parseInt(dates[0]) < 3 && Integer.parseInt(dates[1]) <= 2021){
            throw new InvalidParamsException("Invalid card. Already expired");
        }
        return true;
    }
}
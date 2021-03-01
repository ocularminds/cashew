package cashew.domain;

import cashew.Constant;
import cashew.error.InvalidParamsException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name="payments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="typ", discriminatorType = DiscriminatorType.STRING)
public abstract class Payment {

    @Id
    @Column(length = 22)
    private String id;

    @Column(length = 22)
    private String reference;

    @Column(length = 40)
    private String email;

    @Column(length = 25)
    private String merchant;

    private BigInteger amount;

    @Column(length = 3)
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    Date date;
    protected transient Map<String,String> params;

    public Payment() {
        amount = BigInteger.ZERO;
        params = new HashMap<>();
    }

    public Payment(Map<String,String> params) {
        this.date = new Date();
        this.params = params;  
        this.reference = params.get("reference");
        this.merchant = params.get("merchant");
        this.email = params.get("email");
        this.amount = new BigInteger(params.get("amount")); 
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getReference(){
        return this.reference;
    }

    public void setReference(String ref){
        this.reference = ref;
    }

    public String getMerchant() {
        return this.merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public BigInteger getAmount(){
        return this.amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public Date getDate(){
        return this.date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void checkFields() throws InvalidParamsException{        
        if (this.amount == null || this.amount.compareTo(Constant.HUNDRED) == -1) {
            throw new InvalidParamsException("Invalid amount " + this.amount);
        }
        if (this.reference == null || this.reference.isEmpty()) {
            throw new InvalidParamsException("Invalid reference " + this.reference);
        }
        if (this.merchant == null || this.merchant.isEmpty()) {
            throw new InvalidParamsException("Invalid customer name.");
        }
        if (this.email == null || this.email.isEmpty()) {
            throw new InvalidParamsException("Invalid email.");
        }
    }

    public abstract boolean validate() throws InvalidParamsException;
}
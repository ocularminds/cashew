package cashew.domain;
import cashew.error.InvalidParamsException;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BANK")
public class BankPayment extends Payment implements java.io.Serializable {

    @Column(length = 12)
    private String account;

    @Column(length = 5)
    private String bank;

    static final int ACCOUNT_SIZE = 10;
    public static final long serialVersionUID = 43287411;

    public BankPayment(){
        super();
    }

    public BankPayment(Map<String,String> params) {
        super(params);
        this.account = params.get("account");
        this.bank = params.get("bank");

    }

    public String getAccount(){
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBank(){
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public boolean validate() throws InvalidParamsException {
        super.checkFields();
        if(this.account == null) {
            throw new InvalidParamsException("Account number is required.");
        }
        
        if(this.bank == null) {
            throw new InvalidParamsException("Bank code is required.");
        }
        if(this.account.length() != ACCOUNT_SIZE) {
            throw new InvalidParamsException("Invalid account number!");
        }
        return true;
    }
}
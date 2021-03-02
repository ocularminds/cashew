package cashew.domain;

import cashew.error.InvalidParamsException;
import java.util.Map;
import java.util.regex.Pattern; 
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("MOBILE")
public class MobilePayment extends Payment {

    static final String REGREX_FOR_PHONE = "^(?:[0-9]?){6,14}[0-9]";

    @Column(length = 20)
    private String mobile;

    public MobilePayment(){
        super();
    }
    
    public MobilePayment(Map<String,String> params) {
        super(params);
        this.mobile = params.get("mobile");
    }

    public String getMobile(){
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public boolean validate() throws InvalidParamsException {
        super.checkFields();
        if(this.mobile == null) {
            throw new InvalidParamsException("Mobile phone number is required");
        }
        if(!Pattern.matches(REGREX_FOR_PHONE, this.mobile)) {
            throw new InvalidParamsException("Invalid phone number specified.");
        }
        return true;
    }
}
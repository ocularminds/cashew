package cashew.processor;

import cashew.Errors;
import cashew.Fault;
import cashew.domain.BankPayment;
import cashew.domain.CardPayment;
import cashew.domain.MobilePayment;
import cashew.domain.Payment;
import cashew.error.AuthorizationError;
import cashew.error.InvalidParamsException;

public class PaymentProcessor implements Processor {
    
    @Override
    public Fault process(Payment payment, String hash) {
       try{
           payment.validate();
           return validateHash(payment, hash);
       } catch (InvalidParamsException ex) {
           return new Fault(Errors.ERROR_12_INVALID_TRANSACTION, ex.getMessage());
       }
    }

    private Fault validateHash(Payment input, String hash) {
        if (hash == null || hash.isEmpty()) {
            throw new InvalidParamsException("Bad request or attempted fraud.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(input.getAmount());
        sb.append(input.getMerchant());
        sb.append(input.getEmail());
        sb.append(input.getReference());
        String hashed = String.format("%06d", sb.toString().length());
        if (!hashed.equalsIgnoreCase(hash)) {
            return new Fault(Errors.ERROR_59_SUSPECTED_FRAUD, "Bad request or attempted fraud.");
        }
        return new Fault("00", "Success");
    }
}
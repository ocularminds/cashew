package cashew.service;

import java.math.BigInteger;
import java.util.HashMap;
import cashew.Fault;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import cashew.Constant;
import cashew.error.AuthorizationError;
import cashew.error.NotFoundError;
import cashew.error.InvalidParamsException;
import cashew.domain.Payment;
import cashew.domain.BankPayment;
import cashew.domain.CardPayment;
import cashew.domain.MobilePayment;
import cashew.processor.Handlers;
import cashew.processor.Processor;
import cashew.processor.PaymentProcessor;
import cashew.repository.BankPaymentRepository;
import cashew.repository.CardPaymentRepository;
import cashew.repository.MobilePaymentRepository;
import cashew.repository.PaymentRepository;
/**
 *
 * @author Jejelowo B. Festus
 */
@Service
public class PaymentService {

    private final transient PaymentRepository repository;
    private final transient BankPaymentRepository bankPaymentRepository;
    private final transient CardPaymentRepository cardPaymentRepository;
    private final transient MobilePaymentRepository mobilePaymentRepository;
    private final transient PaymentProcessor processor;
    static final String FAILED = "Not authorized";
    private final transient Counter success;
    private final transient Counter failures;
    static final Logger LOG = Logger.getLogger(PaymentService.class.getName());

    @Autowired
    public PaymentService(
        PaymentRepository paymentRepository,
        BankPaymentRepository bankPaymentRepository,
        CardPaymentRepository cardPaymentRepository,
        MobilePaymentRepository mobilePaymentRepository,
        MeterRegistry meterRegistry) {       
        this.success = meterRegistry.counter("services.payment.success");
        this.failures = meterRegistry.counter("services.payment.failure");
        this.repository = paymentRepository;
        this.bankPaymentRepository = bankPaymentRepository;
        this.cardPaymentRepository = cardPaymentRepository;
        this.mobilePaymentRepository = mobilePaymentRepository;
        this.processor = new PaymentProcessor();
    }

    public boolean isNotPopulated() {
        return this.repository.count() == 0;
    }

    /**
     * Persists a payment data to storage.
     *
     * @param token
     * @param input Payment data
     * @return Fault payment response details
     */
    public Fault create(Map<String,String> params) throws InvalidParamsException {
        if (params.get("method") == null || params.get("method").isEmpty()) {
            incrementFailures();
            throw new InvalidParamsException("No payment method selected.");
        }
        if (null == params.get("hash") || params.get("hash").isEmpty()) {
            incrementFailures();
            throw new InvalidParamsException("Missing payment hash");
        }
        
        String method = params.get("method").toUpperCase(Locale.ROOT);
        Fault fault = Handlers.valueOf(method).handle(processor, params);
        if(fault.isSuccess()){
            incrementSuccess();
        } else{
            incrementFailures();
        }

        Object payment = fault.getData();
        Payment output;
        if(payment instanceof BankPayment){
            output = bankPaymentRepository.save((BankPayment)payment);
        } else if(payment instanceof CardPayment){
            output = cardPaymentRepository.save((CardPayment)payment);
        } else if(payment instanceof MobilePayment){
            output = mobilePaymentRepository.save((MobilePayment)payment);
        } else{
            return fault;
        }
        
        if(output != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", output.getId());
            data.put("reference", output.getReference());
            data.put("status", output.getStatus());
            fault.setData(data);
        }
        return fault;
    }

    /**
     * Retrieves single payment from storage.
     *
     * @param id Payment Id
     * @return Payment details
     */
    public Payment findById(String id) {
        return repository.findById(id).orElseThrow(
            () -> new NotFoundError("Invalid payment  " + id)
        );
    }    

    /**
     * Retrieves all payment data
     *
     * @return Payment records
     */
    @SuppressWarnings("unchecked")
    public List<Payment> findAll() {
        return repository.findAll();
    }
    
    public List<BankPayment> getAllBankPayments() {
        return bankPaymentRepository.findAll();
    }

    public List<CardPayment> getAllCardPayments(){
        return cardPaymentRepository.findAll();
    }

    public List<MobilePayment> getAllMobilePayments() {
        return mobilePaymentRepository.findAll();
    }
    
    /**
     * Retrieves all payment data
     *
     * @param merchantId
     * @return Payment records
     */
    @SuppressWarnings("unchecked")
    public List<Payment> findByMerchant(String merchant) {
        return repository.findAllMerchantPayments(merchant);
    }

    private void incrementFailures(){
        if(failures != null) failures.increment();
    }

    private void incrementSuccess(){
        if(success != null) success.increment();
    }
}

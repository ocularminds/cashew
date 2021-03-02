package cashew.service;

import java.math.BigInteger;
import cashew.Fault;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import cashew.Constant;
import cashew.common.JsonParser;
import cashew.error.AuthorizationError;
import cashew.error.InvalidParamsException;
import cashew.domain.Payment;
import cashew.domain.BankPayment;
import cashew.domain.CardPayment;
import cashew.domain.MobilePayment;
import cashew.repository.PaymentRepository;
import cashew.repository.BankPaymentRepository;
import cashew.repository.CardPaymentRepository;
import cashew.repository.MobilePaymentRepository;

/**
 * Test cases for the ProviderService public methods
 *
 * @author Babatope Festus
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {
    
    @Mock
    transient PaymentRepository paymentRepository;
    @Mock
    transient BankPaymentRepository bankPaymentRepository;
    @Mock
    transient CardPaymentRepository cardPaymentRepository;
    @Mock
    transient MobilePaymentRepository mobilePaymentRepository;

    @InjectMocks
    transient PaymentService service;
    static final String TEST_PHONE_HUMBER = "9715678458002";
    static final String TEST_MERCHANT = "Test Merchant";
    static final String TEST_EMAIL = "test@email.org";
    static final String FIELD_MOBILE = "mobile";
    static final String FIELD_ACCOUNT = "account";
    static final String FIELD_AMOUNT = "amount";
    static final String FIELD_CARD = "card";
    static final String FIELD_EMAIL = "email";
    static final String FIELD_HASH = "hash";
    static final String FIELD_MERCHANT = "merchant";
    static final String FIELD_METHOD = "method";
    static final String FIELD_REFERENCE = "reference";

    @Test
    public void testCountResultTotalCountFromRepository() {
        when(paymentRepository.count()).thenReturn(123L);
        long paymentCount = paymentRepository.count();
        Assert.assertEquals(123L, paymentCount);
        Mockito.verify(paymentRepository).count();
    }

    @Test
    public void testFindAllByMerchant() {
        List<Payment> records = new ArrayList<>();
        String email = "test.mail@lycos.com";
        String merchant = "Shopping Germane";
        String number = Long.toString((long) (Math.random() * 567890));
        BigInteger amount = new BigInteger(number);
        Map<String,String> payment = new HashMap<>();
        payment.put(FIELD_MOBILE,  TEST_PHONE_HUMBER);
        payment.put(FIELD_MERCHANT, merchant);
        payment.put(FIELD_AMOUNT,  amount.toString());
        payment.put(FIELD_EMAIL,  email);
        payment.put(FIELD_REFERENCE, number);
        records.add(new CardPayment(payment));

        number = Long.toString((long) (Math.random() * 567890));
        amount = new BigInteger(number);
        payment = new HashMap<>();
        payment.put(FIELD_MOBILE,  TEST_PHONE_HUMBER);
        payment.put(FIELD_MERCHANT, merchant);
        payment.put(FIELD_AMOUNT,  amount.toString());
        payment.put(FIELD_EMAIL,  email);
        payment.put(FIELD_REFERENCE, number);
        records.add(new CardPayment(payment));
        when(paymentRepository.findAllMerchantPayments(merchant)).thenReturn(records);
        List<Payment> result = service.findByMerchant(merchant);
        Assert.assertEquals(result.size(), records.size());
        Mockito.verify(paymentRepository).findAllMerchantPayments(merchant);
    }

    @Test
    public void testPaymentSuccessWithValidFields() {
        final String EXPECTED_ERROR = "00";
        String number = Long.toString((long) (Math.random() * 567890));
        BigInteger amount = new BigInteger(number);
        Map<String,String> params = new HashMap<>();
        params.put(FIELD_MOBILE,  "9715678458009");
        params.put(FIELD_REFERENCE, number);
        params.put(FIELD_AMOUNT,  amount.toString());
        params.put(FIELD_MERCHANT,  "James Bello");
        params.put(FIELD_EMAIL,  "ty@bello.com");
        params.put(FIELD_METHOD,  "mobile");
        params.put("hash", hash(params));
        
        Fault fault = service.create(params);
        System.out.println(JsonParser.toJson(fault));
        Assert.assertEquals(EXPECTED_ERROR, fault.getError());
    }

    @Test
    public void testPaymentFailedWhenNoPaymentMethodIsSelected() {
        final String EXPECTED_MESSAGE = "No payment method selected.";
        String number = Long.toString((long) (Math.random() * 567890));
        BigInteger amount = new BigInteger(number);
        Map<String,String> payment = new HashMap<>();
        payment.put(FIELD_MOBILE,  TEST_PHONE_HUMBER);
        payment.put(FIELD_AMOUNT,  amount.toString());
        Exception exception = assertThrows(InvalidParamsException.class, () -> {
            service.create(payment);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
    }

    @Test
    public void testPaymentFailedWithInvalidParams() {
        String number = Long.toString((long) (Math.random() * 567890));
        BigInteger amount = new BigInteger(number);
        Map<String,String> payment = new HashMap<>();
        payment.put(FIELD_MOBILE,  TEST_PHONE_HUMBER);
        payment.put(FIELD_AMOUNT,  amount.toString());
        payment.put(FIELD_METHOD,  FIELD_MOBILE);
        payment.put(FIELD_HASH,  "09800");
        Fault fault = service.create(payment);
        String actualMessage = fault.getDescription();
        assertTrue(actualMessage.contains("Invalid"));
    }

    @Test
    public void testPaymentFailedWithNegativeAmount() {
        String merchant = "Speedoo Market";
        String number = Long.toString((long) (Math.random() * 567890));
        BigInteger amount = new BigInteger(number).multiply(new BigInteger("-1"));
        Map<String,String> payment = new HashMap<>();
        payment.put(FIELD_MOBILE,  TEST_PHONE_HUMBER);
        payment.put(FIELD_AMOUNT,  amount.toString());
        payment.put(FIELD_REFERENCE, number);
        payment.put(FIELD_MERCHANT,  merchant);
        payment.put(FIELD_METHOD,  FIELD_MOBILE);
        payment.put(FIELD_HASH,  "012");
        final String EXPECTED_MESSAGE = "Invalid amount " + amount;
        Fault fault = service.create(payment);
        String actualMessage = fault.getDescription();        
        assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
    }

    @Test
    public void testPaymentWhenThereIsNoHash() {
        String number = Long.toString((long) (Math.random() * 567890));
        Map<String,String> params = new HashMap<>();
        params.put(FIELD_MOBILE,  TEST_PHONE_HUMBER);
        params.put(FIELD_REFERENCE, number);
        params.put(FIELD_AMOUNT,  number);
        params.put(FIELD_MERCHANT,  "James Bello");
        params.put(FIELD_EMAIL,  "ty@bello.com");
        params.put(FIELD_METHOD,  FIELD_MOBILE);    
        Exception exception = assertThrows(InvalidParamsException.class, () -> {
            service.create(params);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Missing payment hash"));
    }

    @Test
    public void testPaymentFailedWithUnknownHash() {
        String number = Long.toString((long) (Math.random() * 567890));
        BigInteger amount = new BigInteger(number);
        Map<String,String> payment = new HashMap<>();
        payment.put(FIELD_MOBILE,  TEST_PHONE_HUMBER);
        payment.put(FIELD_REFERENCE, number);
        payment.put(FIELD_AMOUNT,  amount.toString());
        payment.put(FIELD_MERCHANT,  "James Bello");
        payment.put(FIELD_EMAIL,  "ty@bello.com");
        payment.put("method", FIELD_MOBILE);
        payment.put("hash", "fb1234ok0987654321eeadadffffd00000009876");
        Fault fault = service.create(payment);
        String actualMessage = fault.getDescription();
        assertTrue(fault.isFailed());
        assertTrue(actualMessage.contains("fraud"));
    }

    @Test
    public void testPaymentForFailedForInvalidCard() {        
        String EXPECTED_MESSAGE = "Invalid card number.";
        String card = "51812345600590001208";
        String paymentId = Long.toString((long) (Math.random() * 567890));
        Map<String,String> params = new HashMap<>();
        params.put(FIELD_MERCHANT,  TEST_MERCHANT);
        params.put(FIELD_REFERENCE, paymentId);
        params.put(FIELD_AMOUNT,  Constant.HUNDRED.toString());
        params.put(FIELD_EMAIL,  TEST_EMAIL);
        params.put("card", card);
        params.put("cvv", "123");
        params.put("expiry", "08/2219");
        Payment payment = new CardPayment(params);
        Exception exception = assertThrows(InvalidParamsException.class, () -> {
            payment.validate();
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
    }
    
    @Test
    public void testPaymentForFailedForExpiredCard() {        
        String EXPECTED_MESSAGE = "Invalid card. Already expired";
        String card = "5181234560059000";
        String paymentId = Long.toString((long) (Math.random() * 567890));
        Map<String,String> params = new HashMap<>();
        params.put(FIELD_MERCHANT,  TEST_MERCHANT);
        params.put(FIELD_REFERENCE, paymentId);
        params.put(FIELD_AMOUNT,  Constant.HUNDRED.toString());
        params.put(FIELD_EMAIL,  TEST_EMAIL);
        params.put("card", card);
        params.put("cvv", "095");
        params.put("expiry", "08/1989");
        Payment payment = new CardPayment(params);
        Exception exception = assertThrows(InvalidParamsException.class, () -> {
            payment.validate();
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
    }

    @Test
    public void testMobilePaymentWhenPhoneNumberIsNotValid() {       
        String EXPECTED_MESSAGE = "Invalid phone number specified.";
        String phone = "971-00-0000-32";
        String paymentId = Long.toString((long) (Math.random() * 567890));
        Map<String,String> params = new HashMap<>();
        params.put(FIELD_MERCHANT,  TEST_MERCHANT);
        params.put(FIELD_REFERENCE, paymentId);
        params.put(FIELD_AMOUNT,  Constant.HUNDRED.toString());
        params.put(FIELD_EMAIL,  TEST_EMAIL);
        params.put(FIELD_MOBILE,  phone);
        Payment payment = new MobilePayment(params);
        Exception exception = assertThrows(InvalidParamsException.class, () -> {
            payment.validate();
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
    }

    @Test
    public void testBankPaymentFialedWhenAccountIsNotValid() throws Exception {
        String EXPECTED_MESSAGE = "Invalid account number!";
        String account = "8123456005900012";
        String paymentId = Long.toString((long) (Math.random() * 567890));
        Map<String,String> params = new HashMap<>();
        params.put(FIELD_MERCHANT,  TEST_MERCHANT);
        params.put(FIELD_REFERENCE, paymentId);
        params.put(FIELD_AMOUNT,  Constant.HUNDRED.toString());
        params.put(FIELD_EMAIL,  TEST_EMAIL);
        params.put(FIELD_ACCOUNT, account);
        params.put("bank", "089");
        Payment payment = new BankPayment(params);
        Exception exception = assertThrows(InvalidParamsException.class, () -> {
            payment.validate();
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(EXPECTED_MESSAGE));
    }
    
    private String hash(Map<String,String> input) {        
        StringBuilder sb = new StringBuilder();
        sb.append(input.get(FIELD_AMOUNT));
        sb.append(input.get(FIELD_MERCHANT));
        sb.append(input.get(FIELD_EMAIL));
        sb.append(input.get(FIELD_REFERENCE));
        return String.format("%06d",sb.toString().length());
    }
}

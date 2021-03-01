package cashew.repository;

import cashew.domain.BankPayment;
import cashew.domain.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface BankPaymentRepository extends PaymentBaseRepository<BankPayment> { /* ... */ }

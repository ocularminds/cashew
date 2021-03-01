package cashew.repository;

import cashew.domain.CardPayment;
import cashew.domain.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface CardPaymentRepository extends PaymentBaseRepository<CardPayment> { /* ... */ }

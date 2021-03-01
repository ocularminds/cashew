package cashew.repository;

import cashew.domain.BankPayment;
import cashew.domain.CardPayment;
import cashew.domain.MobilePayment;
import cashew.domain.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PaymentBaseRepository<T extends Payment> extends JpaRepository<T, String> {
        
    @Query("select u from #{#entityName} as u where u.merchant = ?1 ")
    List<T> findAllMerchantPayments(String merchant);

    @Query("from BankPayment")
    List<BankPayment> findAllBankPayments();
    
    @Query("from CardPayment")
    List<CardPayment> findAllCardPayments();
    
    @Query("from MobilePayment")
    List<MobilePayment> findAllMobilePayments();
}
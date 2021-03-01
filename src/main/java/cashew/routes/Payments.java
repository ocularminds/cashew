package cashew.routes;

import cashew.Fault;
import cashew.domain.BankPayment;
import cashew.domain.CardPayment;
import cashew.domain.MobilePayment;
import cashew.domain.Payment;
import cashew.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;

/**
 * Merchants is responsible for creating RESTful endpoints for Merchants
 *
 * @author Festus B. Jejelowo
 */
@RestController
@RequestMapping(value = {"/payments"})
public class Payments extends Route {

    final transient PaymentService service;

    @Autowired
    public Payments(PaymentService paymentService, MessageSource src) {
        super(src);
        this.service = paymentService;
    }

    /**
     * Returns available merchants with code 200
     *
     * @return
     */
    @GetMapping()
    public List<Payment> findAll() {
        return service.findAll();
    }

    /**
     * Returns 201 and new entity if operation successful or 400 if invalid data
     * supplied.
     *
     * @param authorization
     * @param payment
     * @return
     * @throws java.lang.Exception
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fault send(@RequestBody Map<String,String> params) throws Exception {
        return service.create(params);
    }

    /**
     * Retrieves given payment if payment id is valid.
     * Return 404 if specified ID does not match a payment
     *
     * @param id
     */
    @GetMapping(value = "/{id}")
    public Payment getPayment(@PathVariable(name = "id", required = true) String id) throws Exception {
        return service.findById(id);
    }

    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Payment> search(@RequestBody Map<String,String> params) throws Exception {
        return Collections.EMPTY_LIST;
    }

    @GetMapping(value = "/banks")
    @ResponseStatus(HttpStatus.OK)
    public List<BankPayment> banks() throws Exception {
        return service.getAllBankPayments();
    }

    @GetMapping(value = "/cards")
    @ResponseStatus(HttpStatus.OK)
    public List<CardPayment> cards() throws Exception {
        return service.getAllCardPayments();
    }

    @GetMapping(value = "/mobile")
    @ResponseStatus(HttpStatus.OK)
    public List<MobilePayment> mobile() throws Exception {
        return service.getAllMobilePayments();
    }
}

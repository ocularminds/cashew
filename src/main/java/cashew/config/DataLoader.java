package cashew.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import cashew.Fault;
import cashew.service.PaymentService;

/**
 *
 * @author Festus Jejelowo
 * @author mail.festus@gmail.com
 *
 * This class to set some initial data in the database at the launch of the
 * application.
 */
@Component
public class DataLoader implements ApplicationRunner {

    private final transient PaymentService service;

    static final Logger LOG = Logger.getLogger(DataLoader.class.getName());

    @Autowired
    public DataLoader(PaymentService paymentService) {
        this.service = paymentService;
    }

    @Override
    public void run(ApplicationArguments aa) throws Exception {
        if (service.isNotPopulated()) {
            LOG.info("data loading started ..");
            createPayments();
            LOG.info("data loading completed.");
        }
    }

    private void createPayments() {        
        String[] records = {
            "8901,Amala-Store,MOBILE,mail.festus@gmail.com", 
            "7913,BonMachee,CARD,creditsuzze@mimi.org"    ,
            "2048,Speedoo,BANK,speak2donal@lycos.com",
            "7913,Speedoo,CARD,omosetan.omorele@yahoo.com",
            "7913,Altiva,MOBILE,agubaniro.michael@aaltiva.com",
            "3013,Altiva,CARD,agubaniro.michael@aaltiva.com"
        };
        LOG.log(Level.INFO, "populating payments");
        Arrays.asList(records).forEach(e -> {
            LOG.log(Level.INFO, "\tcreating payment: {0}", e);
            String[] data = e.split(",");
            Map<String,String> params = new HashMap<>();
            params.put("amount", data[0]);
            params.put("merchant", data[1]);
            params.put("method", data[2]);
            params.put("email", data[3]);
            if (data[2].equals("MOBILE")) {
                params.put("mobile", "234901234567");
            } else if (data[2].equals("CARD")) {
                params.put("number", "5123000000000005");
                params.put("expiry", "12/2022");
                params.put("cvv", "567");
            } else {
                params.put("account", "0010840001");
                params.put("bank", "023");
            }
            String ref = "PAY-REF-"+String.format("%06d", (int)(Math.random()*9999));
            String S = data[0]+data[1]+data[3]+ref;
            params.put("reference", ref);
            params.put("hash", String.format("%06d", S.length()));
            service.create(params);
        });
    }
}

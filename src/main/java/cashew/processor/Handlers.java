package cashew.processor;

import cashew.Fault;
import cashew.common.IdGenerator;
import cashew.common.Identifier;
import cashew.domain.Payment;
import cashew.domain.BankPayment;
import cashew.domain.CardPayment;
import cashew.domain.MobilePayment;
import cashew.error.InvalidParamsException;
import java.util.Map;

/**
 * Handlers.class
 * 
 * Handles processing of payment based on the selected instrument type
 */
public enum Handlers {
    BANK {
        @Override
        public Fault handle(Processor processor, Map<String,String> params){   
            String hash = params.get("hash");        
            BankPayment payment = new BankPayment(params);
            return process(processor, payment, hash);
        }
    }, CARD {
        @Override
        public Fault handle(Processor processor, Map<String,String> params){       
            String hash = params.get("hash");              
            CardPayment payment = new CardPayment(params);
            return process(processor, payment, hash);
        }
    }, MOBILE {
        @Override
        public Fault handle(Processor processor, Map<String,String> params){   
            String hash = params.get("hash");                  
            MobilePayment payment = new MobilePayment(params);
            return process(processor, payment, hash);
        }
    };

    Fault process(Processor processor, Payment payment, String hash) {        
        String id = IdGenerator.getInstance().generate(Identifier.Type.LONG);
        payment.setId(id);
        Fault fault = processor.process(payment, hash);
        payment.setStatus(fault.getError());
        fault.setData(payment);
        return fault;
    }

    public abstract Fault handle(Processor processor, Map<String,String> params);

}
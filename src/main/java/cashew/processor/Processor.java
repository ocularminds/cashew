package cashew.processor;

import cashew.Fault;
import cashew.domain.Payment;

public interface Processor {
    Fault process(Payment payment, String hash);
}
package cashew.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Helper class for generation identity numbers of specified sizes.
 *
 * @author Jejelowo B. Festus
 * @author mail.festus@gmail.com
 */
public class IdGenerator {

    private static final IdGenerator INSTANCE = new IdGenerator();

    private static AtomicLong SEQ = new AtomicLong(1);

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public String generate(Identifier.Type type) {
        String clockTime = new StringBuilder(
                Long.toString(System.currentTimeMillis())
        ).reverse().toString();
        String threadId = String.format("%04d", Thread.currentThread().getId());
        String atomicId = String.format("%010d", SEQ.getAndIncrement());
        String number;
        if (null == type) {
            return clockTime.substring(0, 2)
            .concat(threadId.substring(2, 4))
            .concat(atomicId.substring(4, 10));
        }
        switch (type) {
            case LONG:
                number = clockTime.substring(0, 4)
                .concat(String.format("%02d", (int) (Math.random() * 99)))
                .concat(threadId).concat(atomicId);
                break;
            default:
                number = clockTime.substring(0, 2)
                .concat(threadId.substring(2, 4))
                .concat(atomicId.substring(4, 10));
                break;
        }
        return number;
    }
}

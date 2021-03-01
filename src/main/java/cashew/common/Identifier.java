package cashew.common;

/**
 * Identifier generates a random Id with varying sizes
 *
 * @author Festus B. Jejelowo
 */
public final class Identifier {

    private transient Type type;

    public enum Type {
        LONG, MIN, MID, SHORT, REF;
    }

    public Identifier() {
        this(Type.SHORT);
    }

    public Identifier(final Type typ) {
        type = typ;
    }

    public String next() throws Exception {
        return IdGenerator.getInstance().generate(type);
    }
}

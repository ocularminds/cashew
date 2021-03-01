package cashew.common;

import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 *
 * @author Babatope Festus
 */
public class Strings {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String trim(String s, int len) {
        return s != null && s.length() > len ? s.substring(0, len) : s;
    }

    /**
     * Encodes a String into bytes and compress the bytes
     *
     * @param input
     * @return
     */
    public static String zip(String input) {
        byte[] output = new byte[100];
        try {
            Deflater compresser = new Deflater();
            compresser.setInput(input.getBytes("UTF-8"));
            compresser.finish();
            int size = compresser.deflate(output);
            compresser.end();
            byte[] actual = new byte[size];
            System.arraycopy(output, 0, actual, 0, size);
            return new String(actual, "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            return input;
        }
    }

    /**
     * Decode string bytes into a String
     *
     * @param input
     * @return
     */
    public static String unzip(String input) {
        byte[] result = new byte[100];
        try {
            byte[] output = input.getBytes();
            Inflater decompresser = new Inflater();
            decompresser.setInput(output, 0, output.length);
            int resultLength = decompresser.inflate(result);
            decompresser.end();
            return new String(result, 0, resultLength, "UTF-8");
        } catch (java.io.UnsupportedEncodingException | java.util.zip.DataFormatException ex) {
            return input;
        }
    }

    public static void main(String[] args) {
        String input = "Install on openSUSE and SUSE Linux Enterprise Server";
        String output = Strings.zip(input);
        String result = Strings.unzip(output);
        System.out.println(input + " zipped-> " + output);
        System.out.println(output + " unzipped-> " + result);
    }
}

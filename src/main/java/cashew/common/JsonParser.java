package cashew.common;

import cashew.error.JsonParserError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 *
 * @author Festus B. Jejelowo
 * @author mail.festus@gmail.com
 */
public class JsonParser {

    public static <T> T parse(final TypeReference<T> type, final String jsonPacket) throws JsonParserError {
        T data;
        try {
            data = new ObjectMapper().readValue(jsonPacket, type);
            return data;
        } catch (IOException e) {
            throw new JsonParserError(jsonPacket + " " + e.getMessage());
        }
    }

    public static String toJson(Object object) throws JsonParserError {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json;
            json = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
            return json;
        } catch (JsonProcessingException ex) {
            throw new JsonParserError(object.toString() + " " + ex.getMessage());
        }
    }
}

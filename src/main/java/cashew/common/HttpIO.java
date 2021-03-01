package cashew.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Dev.io
 */
public final class HttpIO {

    final transient String url;
    transient Map<String, String> headers;
    private final static String USER_AGENT = "Mozilla/5.0";

    public HttpIO(String url) {
        this.url = url;
        headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addBasicAuthorization(String username, String password) {
        String encoded = encodeCredentialsForBasicAuth(username, password);
        headers.put("Authorization", "Basic " + encoded);
    }

    public Map<String, String> post(String json) throws Exception {
        HttpsURLConnection con = getConnection("POST");
        //System.out.println("headers -> " + headers);
        headers.keySet().forEach(h -> con.setRequestProperty(h, headers.get(h)));
        con.setDoOutput(true);
        System.out.println("\nSending 'POST' request to URL : " + url);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(json);
            wr.flush();
        }
        return toMap(con);
    }

    public String encodeCredentialsForBasicAuth(String username, String password) {
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    // HTTP GET request
    public Map<String, String> sendGet() throws Exception {
        HttpsURLConnection con = getConnection("GET");
        headers.keySet().forEach(h -> con.setRequestProperty(h, headers.get(h)));
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        return toMap(con);
    }

    // HTTP POST request
    public Map<String, String> sendPost(String form) throws Exception {
        HttpsURLConnection con = getConnection("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        headers.keySet().forEach(h -> con.setRequestProperty(h, headers.get(h)));
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(form);
            wr.flush();
        }
        return toMap(con);
    }

    private Map<String, String> toMap(HttpURLConnection con) throws Exception {

        Map<String, String> response = new HashMap<>();
        response.put("status", Integer.toString(con.getResponseCode()));
        try {
            String s = readResponse(con);
            response.put("data", s);
        } catch (Exception ex) {
            response.put("fault", ex.getMessage());
        }
        return response;
    }

    private HttpsURLConnection getConnection(String method) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        return con;
    }

    private String readResponse(HttpURLConnection con) throws Exception {
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String line = in.readLine();
            response = new StringBuilder();
            while(line != null){
                response.append(line);
                line = in.readLine();
            }
        }
        System.out.println(response.toString());
        return response.toString();
    }

}

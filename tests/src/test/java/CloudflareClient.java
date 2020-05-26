import cloudflaredtos.CreateZoneRecordResponse;
import cloudflaredtos.GetZoneResponse;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class CloudflareClient {
    private final TestConfiguration _testConfiguration;
    private static final String _baseUri = "https://api.cloudflare.com/client/v4";

    private String _recordId;
    private String _zoneId;

    public CloudflareClient(TestConfiguration testConfiguration) {
        _testConfiguration = testConfiguration;
    }

    public void createRecord() throws IOException {
        _zoneId = getZoneId(_testConfiguration.getZone());

        String body = String.format(
                "{\"type\":\"A\",\"name\":\"%s\",\"content\":\"127.0.0.1\",\"ttl\":1,\"proxied\":false}",
                _testConfiguration.getTargetDomain());

        HttpURLConnection connection = createConnection(String.format("%s/zones/%s/dns_records", _baseUri, _zoneId));
        connection.setRequestMethod("POST");
        setRequestBody(connection, body);

        connection.connect();

        String responseText = readResponseAsString(connection);

        if(connection.getResponseCode() != 200) {
            throw new IOException(String.format("Request returned status code %s: %s", connection.getResponseCode(), responseText));
        }

        CreateZoneRecordResponse response = new ObjectMapper().readValue(responseText, CreateZoneRecordResponse.class);

        _recordId = response.getResult().getId();
    }

    public void deleteRecord() throws IOException {
        HttpURLConnection connection = createConnection(String.format("%s/zones/%s/dns_records/%s", _baseUri, _zoneId, _recordId));
        connection.setRequestMethod("DELETE");

        connection.connect();
    }

    private String getZoneId(String zone) throws IOException {
        HttpURLConnection connection = createConnection(String.format("%s/zones?match=all&name=%s", _baseUri, zone));
        connection.connect();

        String json = readResponseAsString(connection);
        GetZoneResponse response = new ObjectMapper().readValue(json, GetZoneResponse.class);

        return response.getResult()[0].getId();
    }

    private String readResponseAsString(HttpURLConnection connection) throws IOException {
        try (InputStreamReader inputReader = new InputStreamReader(connection.getInputStream())) {
            try (BufferedReader reader = new BufferedReader(inputReader)) {
                return reader.lines().collect(Collectors.joining());
            }
        }
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + _testConfiguration.getCloudflareApiToken());
        connection.setRequestProperty("Content-Type", "application/json");

        return connection;
    }

    private void setRequestBody(HttpURLConnection connection, String body) throws IOException {
        connection.setDoOutput(true);

        try(OutputStream os = connection.getOutputStream()) {
            try (OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
                osw.write(body);
                osw.flush();
            }
        }
    }
}
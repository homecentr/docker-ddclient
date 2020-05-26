import java.io.*;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestConfiguration {
    public static TestConfiguration create() {
        String ipAddress = generateRandomIp();
        String targetDomainPrefix = UUID.randomUUID().toString();

        return new TestConfiguration(ipAddress, targetDomainPrefix);
    }

    private final String _ipAddress;
    private final String _targetDomainPrefix;

    private TestConfiguration(String ipAddress, String targetDomainPrefix) {
        _ipAddress = ipAddress;
        _targetDomainPrefix = targetDomainPrefix;
    }

    public String getTargetDomain() {
        return String.format("%s.ddclient.%s", _targetDomainPrefix, System.getenv("CLOUDFLARE_ZONE"));
    }

    public String getZone() {
        return System.getenv("CLOUDFLARE_ZONE");
    }

    public String getCloudflareApiToken() {
        return System.getenv("CLOUDFLARE_API_TOKEN");
    }

    public String getCloudflareNsServer() {
        return System.getenv("CLOUDFLARE_NS_SERVER");
    }

    public String getExpectedIpAddress() {
        return _ipAddress;
    }

    public String createConfigFile() throws IOException {
        String exampleConfigPath = Paths.get(System.getProperty("user.dir"), "..", "example", "ddclient.conf").normalize().toString();
        String fileContent;

        try(BufferedReader reader = new BufferedReader(new FileReader(exampleConfigPath))) {
            fileContent = reader.lines().collect(Collectors.joining("\n"));
        }

        fileContent = fileContent
                .replace("127.0.0.1", getExpectedIpAddress())
                .replace("zone-domain.tld", getZone())
                .replace("target-domain.tld", getTargetDomain())
                .replace("APIKey", getCloudflareApiToken());

        String tmpConfigPath = exampleConfigPath + ".tmp";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpConfigPath))) {
            writer.write(fileContent);
            writer.flush();
        }

        return tmpConfigPath;
    }

    private static String generateRandomIp() {
        Random random = new Random();

        return String.format("99.%d.%d.%d",
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255));
    }
}

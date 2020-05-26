import helpers.DnsClient;
import helpers.DockerImageTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import io.homecentr.testcontainers.images.PullPolicyEx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;
import java.time.Duration;

import static io.homecentr.testcontainers.WaitLoop.waitFor;

public class DdClientContainerShould {
    private static final Logger logger = LoggerFactory.getLogger(DdClientContainerShould.class);

    private static TestConfiguration _testConfig;
    private static GenericContainerEx _container;
    private static CloudflareClient _cloudflareClient;

    @BeforeClass
    public static void before() throws IOException {
        _testConfig = TestConfiguration.create();

        _cloudflareClient = new CloudflareClient(_testConfig);
        _cloudflareClient.createRecord();

        String configPath = _testConfig.createConfigFile();

        _container = new GenericContainerEx<>(new DockerImageTagResolver())
                .withFileSystemBind(configPath, "/config/ddclient.conf")
                .withEnv("CRON_SCHEDULE", "* * * * *")
                // .withEnv("DDCLIENT_ARGS", "-verbose -debug -noipv6")
                .withImagePullPolicy(PullPolicyEx.never())
                .waitingFor(WaitEx.forS6OverlayStart())
                .withReuse(false);

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void after() throws IOException {
        _container.close();
        _cloudflareClient.deleteRecord();
    }

    @Test
    public void runTick() throws Exception {
        waitFor(Duration.ofSeconds(90), () -> _container.getLogsAnalyzer().contains("Execution finished"));
    }

    @Test
    public void exitWithZero() throws Exception {
        waitFor(Duration.ofSeconds(90), () -> _container.getLogsAnalyzer().contains("Script exit code: 0"));
    }

    @Test
    public void updateDnsRecord() throws Exception {
        waitFor(Duration.ofSeconds(120), () -> DnsClient.resolve(_testConfig.getTargetDomain(), _testConfig.getCloudflareNsServer()) != null);
    }
}
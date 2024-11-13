package dev.bakulin.ticktacktoe;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest {
    @LocalServerPort
    int runAt;

    String buildUrl(String path) {
        return "http://localhost:" + runAt + path;
    }

    String fromFile(String path) {
        try {
            return IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream(path)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("Error reading file {0}", path));
        }
    }

}

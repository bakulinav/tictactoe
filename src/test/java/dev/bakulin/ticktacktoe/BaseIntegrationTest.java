package dev.bakulin.ticktacktoe;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest {
    @LocalServerPort
    int runAt;

    String buildUrl(String path) {
        return "http://localhost:" + runAt + path;
    }
}

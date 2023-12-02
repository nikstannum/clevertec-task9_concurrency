package ru.clevertec.entity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ServerTest {

    private Server server;

    @ParameterizedTest
    @ValueSource(ints = {5, 89, 351, 1000})
    void checkProcessRequestShouldBeEquals(int nThread) throws InterruptedException {
        // given
        server = new Server();
        int taskFactor = 3;
        int iterations = taskFactor * nThread;

        // when
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        for (int i = 0; i < iterations; i++) {
            service.submit(() -> {
                Request request = new Request();
                request.setNumber(1);
                server.processRequest(request);
            });
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);

        // then
        Assertions.assertThat(server.getProcessed().size()).isEqualTo(iterations);
    }
}
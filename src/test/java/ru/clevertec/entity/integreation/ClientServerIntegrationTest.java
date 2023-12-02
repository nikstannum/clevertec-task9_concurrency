package ru.clevertec.entity.integreation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.clevertec.entity.Client;
import ru.clevertec.entity.Server;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientServerIntegrationTest {

    @ParameterizedTest
    @MethodSource("provideCapacityPoolSize")
    void checkClientServerInteraction(TestData data) throws InterruptedException {
        // given
        int initCapacity = data.capacity;
        int clientThreadPoolSize = data.poolSize;
        int clientQuantity = data.clientQuantity;
        int threadFactor = 3;
        int nThread = threadFactor * clientThreadPoolSize;
        Server server = new Server();

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        for (int i = 0; i < clientQuantity; i++) {
            executorService.submit(() -> {
                Client client = new Client(initCapacity, clientThreadPoolSize);
                client.sendRequestsBatch(server);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // then
        assertThat(server.getProcessed()).hasSize(initCapacity * clientQuantity);
    }

    private static Stream<TestData> provideCapacityPoolSize() {
        return Stream.of(
                new TestData(10, 100, 10),
                new TestData(20, 200, 20),
                new TestData(50, 500, 50)
        );
    }

    private record TestData(int clientQuantity, int capacity, int poolSize) {
    }
}

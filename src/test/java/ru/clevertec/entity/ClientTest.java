package ru.clevertec.entity;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class ClientTest {

    private Server server;

    @BeforeEach
    void setUp() {
        server = Mockito.mock(Server.class);
    }

    @ParameterizedTest
    @MethodSource("provideCapacityPoolSize")
    void checkGetAccumulatorValueShouldBeEqualsInitCapacity(CapacityPoolData data) throws InterruptedException {
        // given
        Response response = new Response();
        response.setNumber(1);
        when(server.processRequest(any())).thenReturn(response);

        Client client = new Client(data.capacity, data.poolSize);

        // when
        client.sendRequestsBatch(server);

        // then
        Assertions.assertThat(data.capacity).isEqualTo(client.getAccumulator());
    }

    private static Stream<CapacityPoolData> provideCapacityPoolSize() {
        return Stream.of(
                new CapacityPoolData(100, 10),
                new CapacityPoolData(100500, 15),
                new CapacityPoolData(2000, 5)
        );

    }


    private record CapacityPoolData(int capacity, int poolSize) {
    }
}
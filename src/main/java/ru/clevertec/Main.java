package ru.clevertec;

import ru.clevertec.entity.Client;
import ru.clevertec.entity.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        Client client = new Client(100, 10);
        client.sendRequestsBatch(server);
        System.out.println("accumulator = " + client.getAccumulator());
        System.out.println("elements left in the list  = " + client.getIntList().size());
        System.out.println("server processed requests = " + server.getProcessed().size());
    }
}

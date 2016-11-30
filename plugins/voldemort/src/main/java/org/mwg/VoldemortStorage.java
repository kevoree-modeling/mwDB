package org.mwg;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;

public class VoldemortStorage {

    public static void main(String[] args) {
        String bootstrapUrl = "tcp://localhost:6666";
        StoreClientFactory factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));

        // create a client that executes operations on a single store
        StoreClient<String, String> client = factory.getStoreClient("my_store_name");
    }

}

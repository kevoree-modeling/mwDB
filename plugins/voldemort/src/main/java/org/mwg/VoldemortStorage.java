package org.mwg;

import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;
import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;

import java.util.ArrayList;
import java.util.List;

// TODO check for configuration params
// TODO do we create a client per request or keep one for the storage
// TODO what is the semantic of the "versioned" stuff?

// TODO put and get are very rudimentary

public class VoldemortStorage implements Storage {
    private static final String _connectedError = "PLEASE CONNECT YOUR DATABASE FIRST";

    public static void main(String[] args) {
        String bootstrapUrl = "tcp://localhost:6666";
        StoreClientFactory factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));

        // create a client that executes operations on a single store
        StoreClient<byte[], byte[]> client = factory.getStoreClient("test");

        // do some random pointless operations
        client.put(new byte[0], new byte[0]);


//        Versioned<String> value = client.get("key1");
//        System.out.println(value.getValue());

//        Versioned<String> value = client.get("some_key");
//        System.out.println(value);

//        value.setObject("some_value");
//        client.put("some_key", value);
    }

    private boolean _isConnected;

    private final String _bootstrapUrl;
    private final String _storeName;

    private StoreClientFactory _factory;
    private StoreClient _client;
    private Graph _graph;

    public VoldemortStorage(String bootstrapUrl, String storeName) {
        this._bootstrapUrl = bootstrapUrl;
        this._storeName = storeName;

        this._factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));
    }

    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {
        if (!_isConnected) {
            throw new RuntimeException(_connectedError);
        }

        BufferIterator it = keys.iterator();
        final List<byte[]> allKeys = new ArrayList<>();
        while (it.hasNext()) {
            Buffer keyView = it.next();
            if (keyView != null) {
                allKeys.add(keyView.data());
            }
        }


        if (callback != null) {
            callback.on(null);
        }
    }

    @Override
    public void put(Buffer stream, Callback<Boolean> p_callback) {
        if (!_isConnected) {
            throw new RuntimeException(_connectedError);
        }

        BufferIterator it = stream.iterator();
        while (it.hasNext()) {
            Buffer keyView = it.next();
            Buffer valueView = it.next();
            if (valueView != null) {
                _client.put(keyView.data(), valueView.data());
            }
        }
        if (p_callback != null) {
            p_callback.on(true);
        }
    }

    @Override
    public void remove(Buffer keys, Callback<Boolean> callback) {

    }

    @Override
    public void connect(Graph graph, Callback<Boolean> callback) {
        if (_isConnected) {
            if (callback != null) {
                callback.on(null);
            }
            return;
        }

        this._graph = graph;

        // create a client that executes operations on a single store
        _client = _factory.getStoreClient(this._storeName);

        _isConnected = true;

        callback.on(true);
    }

    @Override
    public void lock(Callback<Buffer> callback) {
        byte[] current = null;//db.get(prefixKey);
        if (current == null) {
            current = new String("0").getBytes();
        }
        Short currentPrefix = Short.parseShort(new String(current));
        // db.put(prefixKey, ((currentPrefix + 1) + "").getBytes());
        if (callback != null) {
            Buffer newBuf = _graph.newBuffer();
            org.mwg.utility.Base64.encodeIntToBuffer(currentPrefix, newBuf);
            callback.on(newBuf);
        }
    }

    @Override
    public void unlock(Buffer previousLock, Callback<Boolean> callback) {
        callback.on(true);

    }

    @Override
    public void disconnect(Callback<Boolean> callback) {
        _client = null;
        if (callback != null) {
            callback.on(true);
        }
    }
}

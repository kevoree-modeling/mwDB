package org.mwg;

import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;
import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.utils.ByteArray;
import voldemort.utils.ByteUtils;

// TODO check for configuration params
// TODO do we create a client per request or keep one for the storage
// TODO what is the semantic of the "versioned" stuff?

// TODO put and get are very rudimentary

public class VoldemortStorage implements Storage {
    private static final String _connectedError = "PLEASE CONNECT YOUR DATABASE FIRST";

    public static void main(String[] args) {
//        String bootstrapUrl = "tcp://localhost:6666";
        String bootstrapUrl = "tcp://192.168.25.82:6666";

        StoreClientFactory factory = new SocketStoreClientFactory(new ClientConfig()
                .setEnableSerializationLayer(false)
                .setEnableLazy(false)
                .setRequestFormatType(RequestFormatType.VOLDEMORT_V3)
                .setBootstrapUrls(bootstrapUrl)

        );

        // create a client that executes operations on a single store
        StoreClient<ByteArray, byte[]> client = factory.getStoreClient("test");

        // do some random pointless operations
        ByteArray key = new ByteArray(ByteUtils.getBytes("key100", "UTF-8"));
        client.put(new ByteArray(ByteUtils.getBytes("key100", "UTF-8")), new byte[0]);
        System.out.println(client.get(key).getValue());

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
    private StoreClient<ByteArray, byte[]> _client;
    private Graph _graph;

    public VoldemortStorage(String bootstrapUrl, String storeName) {
        this._bootstrapUrl = bootstrapUrl;
        this._storeName = storeName;

        this._factory = new SocketStoreClientFactory(new ClientConfig()
                .setEnableSerializationLayer(false)
                .setEnableLazy(false)
                .setRequestFormatType(RequestFormatType.VOLDEMORT_V3)
                .setBootstrapUrls(bootstrapUrl)
        );
    }

    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {
        if (!_isConnected) {
            throw new RuntimeException(_connectedError);
        }
        Buffer result = _graph.newBuffer();
        BufferIterator it = keys.iterator();
        boolean isFirst = true;
        while (it.hasNext()) {
            Buffer view = it.next();
            try {
                if (!isFirst) {
                    result.write(Constants.BUFFER_SEP);
                } else {
                    isFirst = false;
                }
                // TODO directly getValue?
                // TODO getALL?
                ByteArray key = new ByteArray(view.data());
                byte[] res = _client.get(key).getValue();
                if (res != null) {
                    result.writeAll(res);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (callback != null) {
            callback.on(result);
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
                 ByteArray key = new ByteArray(keyView.data());
                _client.put(key, valueView.data());
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

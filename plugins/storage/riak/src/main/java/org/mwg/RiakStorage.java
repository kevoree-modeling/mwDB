package org.mwg;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.MultiFetch;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.RiakFutureListener;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class RiakStorage implements Storage {

    private String[] _urls;
    private RiakClient client;
    private Namespace ns;

    public RiakStorage(String nameSpace, String... urls) {
        this._urls = urls;
        Namespace ns = new Namespace("default", nameSpace);
    }

    @Override
    public void connect(Graph graph, Callback<Boolean> callback) {
        try {
            client = RiakClient.newClient(_urls);
            callback.on(true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            callback.on(false);
        }
    }

    @Override
    public void disconnect(Callback<Boolean> callback) {
        client.shutdown();
    }


    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {

        List<Location> locations = new ArrayList<Location>();
        BufferIterator it = keys.iterator();
        while (it.hasNext()) {
            Buffer keyView = it.next();
            Location location = new Location(ns, new String(keyView.data()));
            locations.add(location);
        }
        MultiFetch multifetch = new MultiFetch.Builder().addLocations(locations).build();
        client.executeAsync(multifetch).addListener(new RiakFutureListener<MultiFetch.Response, List<Location>>() {
            @Override
            public void handle(RiakFuture<MultiFetch.Response, List<Location>> riakFuture) {
                try {
                    MultiFetch.Response resp = riakFuture.get();
                    resp.forEach(new Consumer<RiakFuture<FetchValue.Response, Location>>() {
                        @Override
                        public void accept(RiakFuture<FetchValue.Response, Location> responseLocationRiakFuture) {
                            responseLocationRiakFuture.get().getValues()
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void put(Buffer stream, Callback<Boolean> callback) {
        client.ex
    }

    @Override
    public void remove(Buffer keys, Callback<Boolean> callback) {

    }


    @Override
    public void lock(Callback<Buffer> callback) {

    }

    @Override
    public void unlock(Buffer previousLock, Callback<Boolean> callback) {

    }

}

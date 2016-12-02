package org.mwg;

import io.undertow.connector.ByteBufferPool;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.*;
import org.mwg.chunk.Chunk;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;
import org.mwg.utility.Base64;
import org.xnio.*;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WSClient implements Storage {

    private final String url;

    private WebSocketChannel channel;

    private XnioWorker _worker;

    private Graph graph;

    private Map<Integer, Callback> callbacks;

    public WSClient(String p_url) {
        this.url = p_url;
        this.callbacks = new HashMap<Integer, Callback>();
    }

    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {
        send_rpc_req(WSConstants.REQ_GET, keys, callback);
    }

    @Override
    public void put(Buffer stream, Callback<Boolean> callback) {
        send_rpc_req(WSConstants.REQ_PUT, stream, callback);
    }

    @Override
    public void remove(Buffer keys, Callback<Boolean> callback) {
        send_rpc_req(WSConstants.REQ_REMOVE, keys, callback);
    }

    @Override
    public void lock(Callback<Buffer> callback) {
        send_rpc_req(WSConstants.REQ_LOCK, null, callback);
    }

    @Override
    public void unlock(Buffer previousLock, Callback<Boolean> callback) {
        send_rpc_req(WSConstants.REQ_UNLOCK, previousLock, callback);
    }

    @Override
    public void connect(final Graph p_graph, final Callback<Boolean> callback) {
        if (channel != null) {
            if (callback != null) {
                callback.on(true);//already connected
            }
        }
        this.graph = p_graph;
        try {
            Xnio xnio = Xnio.getInstance(io.undertow.websockets.client.WebSocketClient.class.getClassLoader());
            _worker = xnio.createWorker(OptionMap.builder()
                    .set(Options.WORKER_IO_THREADS, 2)
                    .set(Options.CONNECTION_HIGH_WATER, 1000000)
                    .set(Options.CONNECTION_LOW_WATER, 1000000)
                    .set(Options.WORKER_TASK_CORE_THREADS, 30)
                    .set(Options.WORKER_TASK_MAX_THREADS, 30)
                    .set(Options.TCP_NODELAY, true)
                    .set(Options.CORK, true)
                    .getMap());
            ByteBufferPool _buffer = new DefaultByteBufferPool(true, 1024 * 1024);
            WebSocketClient.ConnectionBuilder builder = io.undertow.websockets.client.WebSocketClient
                    .connectionBuilder(_worker, _buffer, new URI(url));

            /*
            if(_sslContext != null) {
                UndertowXnioSsl ssl = new UndertowXnioSsl(Xnio.getInstance(), OptionMap.EMPTY, _sslContext);
                builder.setSsl(ssl);
            }*/

            IoFuture<WebSocketChannel> futureChannel = builder.connect();
            futureChannel.await(5, TimeUnit.SECONDS); //Todo change this magic number!!!
            if (futureChannel.getStatus() != IoFuture.Status.DONE) {
                System.err.println("Error during connexion with webSocket");
                if (callback != null) {
                    callback.on(null);
                }
            }
            channel = futureChannel.get();
            channel.getReceiveSetter().set(new MessageReceiver());
            channel.resumeReceives();
            if (callback != null) {
                callback.on(true);
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.on(false);
            }
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(Callback<Boolean> callback) {
        try {
            System.out.println("CloseClient");
            channel.sendClose();
            channel.close();
            _worker.shutdown();
            channel = null;
            _worker = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            callback.on(true);
        }
    }

    private class MessageReceiver extends AbstractReceiveListener {
        @Override
        protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message) throws IOException {
            ByteBuffer byteBuffer = WebSockets.mergeBuffers(message.getData().getResource());
            process_rpc_resp(byteBuffer.array());
            super.onFullBinaryMessage(channel, message);
        }

        @Override
        protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
            process_rpc_resp(message.getData().getBytes());
            super.onFullTextMessage(channel, message);
        }
    }

    private void send_rpc_req(byte code, Buffer payload, Callback callback) {
        if (channel == null) {
            throw new RuntimeException(WSConstants.DISCONNECTED_ERROR);
        }
        Buffer buffer = graph.newBuffer();
        buffer.write(code);
        buffer.write(Constants.BUFFER_SEP);
        int hash = callback.hashCode();
        callbacks.put(hash, callback);
        Base64.encodeIntToBuffer(hash, buffer);
        if (payload != null) {
            buffer.write(Constants.BUFFER_SEP);
            buffer.writeAll(payload.data());
        }
        ByteBuffer wrapped = ByteBuffer.wrap(buffer.data());
        buffer.free();
        WebSockets.sendBinary(wrapped, channel, new WebSocketCallback<Void>() {
            @Override
            public void complete(WebSocketChannel webSocketChannel, Void aVoid) {

            }

            @Override
            public void onError(WebSocketChannel webSocketChannel, Void aVoid, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void process_rpc_resp(byte[] payload) {
        Buffer payloadBuf = graph.newBuffer();
        payloadBuf.writeAll(payload);
        BufferIterator it = payloadBuf.iterator();
        Buffer codeView = it.next();
        if (codeView != null && codeView.length() != 0) {
            final byte firstCode = codeView.read(0);
            if (firstCode == WSConstants.REQ_UPDATE) {
                Buffer updateBuf = graph.newBuffer();
                boolean isFirst = true;
                while (it.hasNext()) {
                    Buffer view = it.next();
                    ChunkKey key = ChunkKey.build(view);
                    if (key != null) {
                        Chunk ch = graph.space().getAndMark(key.type, key.world, key.time, key.id);
                        if (ch != null) {
                            graph.space().unmark(ch.index());
                            //ok we keep it, ask for update
                            if (isFirst) {
                                isFirst = false;
                            } else {
                                updateBuf.write(Constants.BUFFER_SEP);
                            }
                            updateBuf.writeAll(view.data());
                        }
                    }
                }
                //now ask for ta get query ... TODO
                //System.out.println("Notification!"); //TODO
            } else {
                Buffer callbackCodeView = it.next();
                if (callbackCodeView != null) {
                    int callbackCode = Base64.decodeToIntWithBounds(callbackCodeView, 0, callbackCodeView.length());
                    Callback resolvedCallback = callbacks.get(callbackCode);
                    if (resolvedCallback != null) {
                        if (firstCode == WSConstants.RESP_LOCK || firstCode == WSConstants.RESP_GET) {
                            Buffer newBuf = graph.newBuffer();//will be free by the core
                            boolean isFirst = true;
                            while (it.hasNext()) {
                                if (isFirst) {
                                    isFirst = false;
                                } else {
                                    newBuf.write(Constants.BUFFER_SEP);
                                }
                                newBuf.writeAll(it.next().data());
                            }
                            resolvedCallback.on(newBuf);
                        } else {
                            resolvedCallback.on(true);
                        }
                    }
                }
            }
        }
        payloadBuf.free();
    }

}

///<reference path='mwg.d.ts'/>
///<reference path='reconnecting-websocket.ts'/>
var org;
(function (org) {
    var mwg;
    (function (mwg) {
        var plugin;
        (function (plugin) {
            var WSClient = (function () {
                function WSClient(p_url) {
                    this.ws = null;
                    this.graph = null;
                    this.generator = 0;
                    this.REQ_GET = 0;
                    this.REQ_PUT = 1;
                    this.REQ_LOCK = 2;
                    this.REQ_UNLOCK = 3;
                    this.REQ_REMOVE = 4;
                    this.REQ_UPDATE = 5;
                    this.RESP_GET = 6;
                    this.RESP_PUT = 7;
                    this.RESP_REMOVE = 8;
                    this.RESP_LOCK = 9;
                    this.RESP_UNLOCK = 10;
                    this.url = p_url;
                    this.callbacks = new java.util.HashMap();
                }
                WSClient.prototype.connect = function (p_graph, callback) {
                    this.graph = p_graph;
                    if (this.ws == null) {
                        var selfPointer_1 = this;
                        this.ws = new WebSocketHelper.ReconnectingWebSocket(this.url);
                        this.ws.onopen = function (event) {
                            callback(true);
                        };
                        this.ws.onmessage = function (msg) {
                            selfPointer_1.process_rpc_resp(new Int8Array(msg.data));
                        };
                        this.ws.connect(false);
                    }
                    else {
                        //do nothing
                        callback(true);
                    }
                };
                WSClient.prototype.disconnect = function (callback) {
                    if (this.ws != null) {
                        this.ws.close();
                        this.ws = null;
                        callback(true);
                    }
                };
                WSClient.prototype.get = function (keys, callback) {
                    this.send_rpc_req(this.REQ_GET, keys, callback);
                };
                WSClient.prototype.put = function (stream, callback) {
                    this.send_rpc_req(this.REQ_PUT, stream, callback);
                };
                WSClient.prototype.remove = function (keys, callback) {
                    this.send_rpc_req(this.REQ_REMOVE, keys, callback);
                };
                WSClient.prototype.lock = function (callback) {
                    this.send_rpc_req(this.REQ_LOCK, null, callback);
                };
                WSClient.prototype.unlock = function (previousLock, callback) {
                    this.send_rpc_req(this.REQ_UNLOCK, previousLock, callback);
                };
                WSClient.prototype.process_rpc_resp = function (payload) {
                    var payloadBuf = this.graph.newBuffer();
                    payloadBuf.writeAll(payload);
                    var it = payloadBuf.iterator();
                    var codeView = it.next();
                    if (codeView != null && codeView.length() != 0) {
                        var firstCode = codeView.read(0);
                        if (firstCode == this.REQ_UPDATE) {
                        }
                        else {
                            var callbackCodeView = it.next();
                            if (callbackCodeView != null) {
                                var callbackCode = org.mwg.utility.Base64.decodeToIntWithBounds(callbackCodeView, 0, callbackCodeView.length());
                                var resolvedCallback = this.callbacks.get(callbackCode);
                                if (resolvedCallback != null) {
                                    if (firstCode == this.RESP_GET || firstCode == this.RESP_LOCK) {
                                        var newBuf = this.graph.newBuffer();
                                        var isFirst = true;
                                        while (it.hasNext()) {
                                            if (isFirst) {
                                                isFirst = false;
                                            }
                                            else {
                                                newBuf.write(org.mwg.Constants.BUFFER_SEP);
                                            }
                                            newBuf.writeAll(it.next().data());
                                        }
                                        resolvedCallback(newBuf);
                                    }
                                    else {
                                        resolvedCallback(true);
                                    }
                                }
                            }
                        }
                    }
                };
                WSClient.prototype.send_rpc_req = function (code, payload, callback) {
                    if (this.ws == null) {
                        throw new Error("Not connected!");
                    }
                    var buffer = this.graph.newBuffer();
                    buffer.write(code);
                    buffer.write(org.mwg.Constants.BUFFER_SEP);
                    var hash = this.generator;
                    this.generator = this.generator + 1 % 1000000;
                    this.callbacks.put(hash, callback);
                    org.mwg.utility.Base64.encodeIntToBuffer(hash, buffer);
                    if (payload != null) {
                        buffer.write(org.mwg.Constants.BUFFER_SEP);
                        buffer.writeAll(payload.data());
                    }
                    var flatData = buffer.data();
                    buffer.free();
                    this.ws.send(flatData);
                };
                return WSClient;
            }());
            plugin.WSClient = WSClient;
        })(plugin = mwg.plugin || (mwg.plugin = {}));
    })(mwg = org.mwg || (org.mwg = {}));
})(org || (org = {}));

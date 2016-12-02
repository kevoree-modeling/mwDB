///<reference path='mwg.d.ts'/>
///<reference path='reconnecting-websocket.ts'/>

module org {
    export module mwg {
        export module plugin {
            export class WSClient implements org.mwg.plugin.Storage {

                private url:string;
                private callbacks:java.util.Map<number,org.mwg.Callback<any>>;
                private ws:WebSocketHelper.ReconnectingWebSocket = null;
                private graph:org.mwg.Graph = null;
                private generator:number = 0;

                private REQ_GET = 0;
                private REQ_PUT = 1;
                private REQ_LOCK = 2;
                private REQ_UNLOCK = 3;
                private REQ_REMOVE = 4;
                private REQ_UPDATE = 5;

                private RESP_GET = 6;
                private RESP_PUT = 7;
                private RESP_REMOVE = 8;
                private RESP_LOCK = 9;
                private RESP_UNLOCK = 10;

                constructor(p_url:string) {
                    this.url = p_url;
                    this.callbacks = new java.util.HashMap<number,org.mwg.Callback<any>>();
                }

                connect(p_graph:org.mwg.Graph, callback:org.mwg.Callback<boolean>):void {
                    this.graph = p_graph;
                    if (this.ws == null) {
                        let selfPointer = this;
                        this.ws = new WebSocketHelper.ReconnectingWebSocket(this.url);
                        this.ws.onopen = function (event:MessageEvent) {
                            callback(true);
                        };
                        this.ws.onmessage = function (msg:MessageEvent) {
                            selfPointer.process_rpc_resp(new Int8Array(msg.data));
                        };
                        this.ws.connect(false);
                    } else {
                        //do nothing
                        callback(true);
                    }
                }

                disconnect(callback:org.mwg.Callback<boolean>):void {
                    if (this.ws != null) {
                        this.ws.close();
                        this.ws = null;
                        callback(true);
                    }
                }

                get(keys:org.mwg.struct.Buffer, callback:org.mwg.Callback<org.mwg.struct.Buffer>):void {
                    this.send_rpc_req(this.REQ_GET, keys, callback);
                }

                put(stream:org.mwg.struct.Buffer, callback:org.mwg.Callback<boolean>):void {
                    this.send_rpc_req(this.REQ_PUT, stream, callback);
                }

                remove(keys:org.mwg.struct.Buffer, callback:org.mwg.Callback<boolean>):void {
                    this.send_rpc_req(this.REQ_REMOVE, keys, callback);
                }

                lock(callback:org.mwg.Callback<org.mwg.struct.Buffer>):void {
                    this.send_rpc_req(this.REQ_LOCK, null, callback);
                }

                unlock(previousLock:org.mwg.struct.Buffer, callback:org.mwg.Callback<boolean>):void {
                    this.send_rpc_req(this.REQ_UNLOCK, previousLock, callback);
                }

                process_rpc_resp(payload:Int8Array) {
                    var payloadBuf = this.graph.newBuffer();
                    payloadBuf.writeAll(payload);
                    var it = payloadBuf.iterator();
                    var codeView = it.next();
                    if (codeView != null && codeView.length() != 0) {
                        var firstCode = codeView.read(0);
                        if(firstCode == this.REQ_UPDATE){
                            //console.log("NOTIFY UPDATE"); //TODO
                        } else {
                            var callbackCodeView = it.next();
                            if(callbackCodeView != null){
                                var callbackCode = org.mwg.utility.Base64.decodeToIntWithBounds(callbackCodeView, 0, callbackCodeView.length());
                                var resolvedCallback = this.callbacks.get(callbackCode);
                                if (resolvedCallback != null) {
                                    if (firstCode == this.RESP_GET || firstCode == this.RESP_LOCK) {
                                        var newBuf = this.graph.newBuffer();
                                        var isFirst = true;
                                        while (it.hasNext()) {
                                            if (isFirst) {
                                                isFirst = false;
                                            } else {
                                                newBuf.write(org.mwg.Constants.BUFFER_SEP);
                                            }
                                            newBuf.writeAll(it.next().data());
                                        }
                                        resolvedCallback(newBuf);
                                    } else {
                                        resolvedCallback(true);
                                    }
                                }
                            }
                        }
                    }
                }

                send_rpc_req(code:number, payload:org.mwg.struct.Buffer, callback:org.mwg.Callback<any>):void {
                    if (this.ws == null) {
                        throw new Error("Not connected!");
                    }
                    var buffer:org.mwg.struct.Buffer = this.graph.newBuffer();
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
                }

            }
        }
    }
}


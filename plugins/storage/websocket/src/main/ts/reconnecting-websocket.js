// MIT License:
//
// Copyright (c) 2010-2012, Joe Walnes
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, load, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
/**
 * This behaves like a WebSocket in every way, except if it fails to connect,
 * or it gets disconnected, it will repeatedly poll until it succesfully connects
 * again.
 *
 * It is API compatible, so when you have:
 *   ws = new WebSocket('ws://....');
 * you can replace with:
 *   ws = new ReconnectingWebSocket('ws://....');
 *
 * The event stream will typically look like:
 *  onconnecting
 *  onopen
 *  onmessage
 *  onmessage
 *  onclose // lost connection
 *  onconnecting
 *  onopen  // sometime later...
 *  onmessage
 *  onmessage
 *  etc...
 *
 * It is API compatible with the standard WebSocket API.
 *
 * Latest version: https://github.com/joewalnes/reconnecting-websocket/
 * - Joe Walnes
 *
 * Latest TypeScript version: https://github.com/daviddoran/typescript-reconnecting-websocket/
 * - David Doran
 */
var WebSocketHelper;
(function (WebSocketHelper) {
    var ReconnectingWebSocket = (function () {
        function ReconnectingWebSocket(url, protocols) {
            if (protocols === void 0) { protocols = []; }
            //These can be altered by calling code
            this.debug = false;
            //Time to wait before attempting reconnect (after close)
            this.reconnectInterval = 1000;
            //Time to wait for WebSocket to open (before aborting and retrying)
            this.timeoutInterval = 2000;
            //Whether WebSocket was forced to close by this client
            this.forcedClose = false;
            //Whether WebSocket opening timed out
            this.timedOut = false;
            //List of WebSocket sub-protocols
            this.protocols = [];
            //Set up the default 'noop' event handlers
            this.onopen = function (event) {
            };
            this.onclose = function (event) {
            };
            this.onconnecting = function () {
            };
            this.onmessage = function (event) {
            };
            this.onerror = function (event) {
            };
            this.url = url;
            this.protocols = protocols;
            this.readyState = WebSocket.CONNECTING;
            //this.connect(false);
        }
        ReconnectingWebSocket.prototype.connect = function (reconnectAttempt) {
            var _this = this;
            this.ws = new WebSocket(this.url, this.protocols);
            this.ws.binaryType = "arraybuffer";
            this.onconnecting();
            this.log('ReconnectingWebSocket', 'attempt-connect', this.url);
            var localWs = this.ws;
            var timeout = setTimeout(function () {
                _this.log('ReconnectingWebSocket', 'connection-timeout', _this.url);
                _this.timedOut = true;
                localWs.close();
                _this.timedOut = false;
            }, this.timeoutInterval);
            this.ws.onopen = function (event) {
                clearTimeout(timeout);
                _this.log('ReconnectingWebSocket', 'onopen', _this.url);
                _this.readyState = WebSocket.OPEN;
                reconnectAttempt = false;
                _this.onopen(event);
            };
            this.ws.onclose = function (event) {
                clearTimeout(timeout);
                _this.ws = null;
                if (_this.forcedClose) {
                    _this.readyState = WebSocket.CLOSED;
                    _this.onclose(event);
                }
                else {
                    _this.readyState = WebSocket.CONNECTING;
                    _this.onconnecting();
                    if (!reconnectAttempt && !_this.timedOut) {
                        _this.log('ReconnectingWebSocket', 'onclose', _this.url);
                        _this.onclose(event);
                    }
                    setTimeout(function () {
                        _this.connect(true);
                    }, _this.reconnectInterval);
                }
            };
            this.ws.onmessage = function (event) {
                _this.log('ReconnectingWebSocket', 'onmessage', _this.url, event.data);
                _this.onmessage(event);
            };
            this.ws.onerror = function (event) {
                _this.log('ReconnectingWebSocket', 'onerror', _this.url, event);
                _this.onerror(event);
            };
        };
        ReconnectingWebSocket.prototype.send = function (data) {
            if (this.ws) {
                this.log('ReconnectingWebSocket', 'send', this.url, data);
                return this.ws.send(data);
            }
            else {
                throw 'INVALID_STATE_ERR : Pausing to reconnect websocket';
            }
        };
        /**
         * Returns boolean, whether websocket was FORCEFULLY closed.
         */
        ReconnectingWebSocket.prototype.close = function () {
            if (this.ws) {
                this.forcedClose = true;
                this.ws.close();
                return true;
            }
            return false;
        };
        /**
         * Additional public API method to refresh the connection if still open (close, re-open).
         * For example, if the app suspects bad data / missed heart beats, it can try to refresh.
         *
         * Returns boolean, whether websocket was closed.
         */
        ReconnectingWebSocket.prototype.refresh = function () {
            if (this.ws) {
                this.ws.close();
                return true;
            }
            return false;
        };
        ReconnectingWebSocket.prototype.log = function () {
            var args = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                args[_i - 0] = arguments[_i];
            }
            if (this.debug || ReconnectingWebSocket.debugAll) {
                console.debug.apply(console, args);
            }
        };
        /**
         * Setting this to true is the equivalent of setting all instances of ReconnectingWebSocket.debug to true.
         */
        ReconnectingWebSocket.debugAll = false;
        return ReconnectingWebSocket;
    }());
    WebSocketHelper.ReconnectingWebSocket = ReconnectingWebSocket;
})(WebSocketHelper || (WebSocketHelper = {}));

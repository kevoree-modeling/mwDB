/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg;

class WSConstants {

    static final String DISCONNECTED_ERROR = "Please connect your WebSocket client first.";

    static final byte REQ_GET = 0;
    static final byte REQ_PUT = 1;
    static final byte REQ_LOCK = 2;
    static final byte REQ_UNLOCK = 3;
    static final byte REQ_REMOVE = 4;
    static final byte REQ_UPDATE = 5;
    static final byte REQ_TASK = 6;

    static final byte RESP_GET = 7;
    static final byte RESP_PUT = 8;
    static final byte RESP_REMOVE = 9;
    static final byte RESP_LOCK = 10;
    static final byte RESP_UNLOCK = 11;
    static final byte RESP_TASK = 12;

}

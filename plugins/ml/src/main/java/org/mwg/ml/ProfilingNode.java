/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.ml;

import org.mwg.Callback;
import org.mwg.Node;

public interface ProfilingNode extends Node {
    /**
     * Main training function to learn from the the expected output,
     * The input features are defined through features extractions.
     *
     * @param callback Called when the learning is completed with the status of learning true/false
     */
    void learn(Callback<Boolean> callback);

    void learnWith(double[] values);

    /**
     * Main infer function to give a cluster ID,
     * The input features are defined through features extractions.
     *
     * @param callback Called when the infer is completed with the result of the predictions
     */
    void predict(Callback<double[]> callback);
}

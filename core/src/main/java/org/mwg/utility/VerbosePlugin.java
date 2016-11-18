package org.mwg.utility;

import org.mwg.base.BasePlugin;

public class VerbosePlugin extends BasePlugin {

    public VerbosePlugin() {
        super();
        declareTaskHookFactory(new VerboseHookFactory());
    }

}

package org.mwg.base;

import org.mwg.plugin.Job;

public abstract class AbstractExternalAttribute {

    public abstract String name();

    public abstract String save();

    public abstract void load(String buffer);

    public abstract AbstractExternalAttribute copy();

    public abstract void notifyDirty(Job dirtyNotifier);

}

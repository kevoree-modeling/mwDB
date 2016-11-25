package org.mwg;

import org.mwg.plugin.Plugin;
import org.mwg.plugin.Scheduler;
import org.mwg.plugin.Storage;
import org.mwg.core.BlackHoleStorage;
import org.mwg.core.scheduler.TrampolineScheduler;
import org.mwg.core.utility.ReadOnlyStorage;

/**
 * Creates an instance of a Graph, with several customizable features.
 */
public class GraphBuilder {

    private Storage _storage = null;
    private Scheduler _scheduler = null;
    private Plugin[] _plugins = null;
    private long _memorySize = -1;
    private boolean _readOnly = false;

    public static GraphBuilder newBuilder() {
        return new GraphBuilder();
    }

    /**
     * Sets the storage system to the given parameter.
     *
     * @param storage the storage system to be used by the graph
     * @return the {@link GraphBuilder}, for a fluent API
     */
    public GraphBuilder withStorage(Storage storage) {
        this._storage = storage;
        return this;
    }

    /**
     * Sets the storage system to the given parameter, in read-only mode.
     *
     * @param storage the storage system to be used by the graph in read-only mode
     * @return the {@link GraphBuilder}, for a fluent API
     */
    public GraphBuilder withReadOnlyStorage(Storage storage) {
        this._storage = storage;
        _readOnly = true;
        return this;
    }

    /**
     * Sets the maximum size of the memory that can be used before automated unload.
     *
     * @param numberOfElements the number of elements in memory before unloading
     * @return the {@link GraphBuilder}, for a fluent API
     */
    public GraphBuilder withMemorySize(long numberOfElements) {
        this._memorySize = numberOfElements;
        return this;
    }

    /**
     * Sets the scheduler to be used by the graph
     *
     * @param scheduler an instance of scheduler
     * @return the {@link GraphBuilder}, for a fluent API
     */
    public GraphBuilder withScheduler(Scheduler scheduler) {
        this._scheduler = scheduler;
        return this;
    }

    /**
     * Declare a plugin to the graph builder.
     *
     * @param plugin that has to be added
     * @return the {@link GraphBuilder}, for a fluent API
     */
    public GraphBuilder withPlugin(Plugin plugin) {
        if (_plugins == null) {
            _plugins = new Plugin[1];
            _plugins[0] = plugin;
        } else {
            Plugin[] _plugins2 = new Plugin[_plugins.length + 1];
            System.arraycopy(_plugins, 0, _plugins2, 0, _plugins.length);
            _plugins2[_plugins.length] = plugin;
            _plugins = _plugins2;
        }
        return this;
    }

    public Graph build() {
        if (_storage == null) {
            _storage = new BlackHoleStorage();
        }
        if (_readOnly) {
            _storage = new ReadOnlyStorage(_storage);
        }
        if (_scheduler == null) {
            _scheduler = new TrampolineScheduler();
        }
        if (_memorySize == -1) {
            _memorySize = 100000;
        }
        return new org.mwg.core.CoreGraph(_storage, _memorySize, _scheduler, _plugins);
    }

}

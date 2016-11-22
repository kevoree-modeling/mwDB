package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.chunk.ChunkSpace;
import org.mwg.core.memory.HeapMemoryFactory;
import org.mwg.plugin.*;
import org.mwg.struct.Buffer;
import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskHookFactory;
import org.mwg.utility.HashHelper;

public class QueryTest implements Resolver, Graph {

    @Test
    public void test() {

        CoreQuery query = new CoreQuery(this, this);
        query.add("name", "Hello");
        //64 bits version/
        Assert.assertEquals(query.hash(), 8429737982204714L);
        //Assert.assertEquals(query.hash(), 3074775135214424L);

        CoreQuery query2 = new CoreQuery(this, this);
        query2.add("id", "Hello");
        Assert.assertEquals(query2.hash(), -4949475811985026L);

        CoreQuery query3 = new CoreQuery(this, this);
        query3.add("id", "Hello2");
        Assert.assertEquals(query3.hash(), 5227124363167243L);

        Assert.assertTrue(query3.hash() != query.hash());
        Assert.assertTrue(query2.hash() != query.hash());
        Assert.assertTrue(query3.hash() != query2.hash());

        CoreQuery query4 = new CoreQuery(this, this);
        query4.add("id", "Hello2");
        Assert.assertEquals(query4.hash(), 5227124363167243L);
        Assert.assertEquals(query3.hash(), query4.hash());

    }


    @Override
    public void init() {

    }

    @Override
    public void initNode(Node node, long typeCode) {

    }

    @Override
    public void initWorld(long parentWorld, long childWorld) {

    }

    @Override
    public void freeNode(Node node) {

    }

    @Override
    public String typeName(Node node) {
        return null;
    }

    @Override
    public long typeCode(Node node) {
        return 0;
    }

    @Override
    public Node newNode(long world, long time) {
        return null;
    }

    @Override
    public Node newTypedNode(long world, long time, String nodeType) {
        return null;
    }

    @Override
    public Node cloneNode(Node origin) {
        return null;
    }

    @Override
    public <A extends Node> void lookup(long world, long time, long id, Callback<A> callback) {

    }

    @Override
    public void lookupTimes(long world, long from, long to, long id, Callback<Node[]> callback) {

    }

    @Override
    public void lookupAll(long world, long time, long[] ids, Callback<Node[]> callback) {

    }

    @Override
    public void lookupAllTimes(long world, long from, long to, long[] ids, Callback<Node[]> callback) {

    }

    @Override
    public long fork(long world) {
        return 0;
    }

    @Override
    public void save(Callback<Boolean> callback) {

    }

    @Override
    public void connect(Callback<Boolean> callback) {

    }

    @Override
    public void disconnect(Callback<Boolean> callback) {

    }

    @Override
    public void index(long world, long time, String name, Callback<NodeIndex> callback) {

    }


    @Override
    public void indexNames(long world, long time, Callback<String[]> callback) {

    }

    @Override
    public DeferCounter newCounter(int expectedEventsCount) {
        return null;
    }

    @Override
    public DeferCounterSync newSyncCounter(int expectedEventsCount) {
        return null;
    }

    @Override
    public Resolver resolver() {
        return null;
    }

    @Override
    public Scheduler scheduler() {
        return null;
    }

    @Override
    public ChunkSpace space() {
        return null;
    }

    @Override
    public Storage storage() {
        return null;
    }

    private HeapMemoryFactory factory = new HeapMemoryFactory();

    @Override
    public Buffer newBuffer() {
        return factory.newBuffer();
    }

    @Override
    public Query newQuery() {
        return null;
    }

    @Override
    public void freeNodes(Node[] nodes) {

    }

    @Override
    public TaskActionFactory taskAction(String name) {
        return null;
    }

    @Override
    public ExternalAttributeFactory externalAttribute(String name) {
        return null;
    }

    @Override
    public TaskHookFactory taskHookFactory() {
        return null;
    }

    @Override
    public NodeState resolveState(Node node) {
        return null;
    }

    @Override
    public NodeState alignState(Node node) {
        return null;
    }

    @Override
    public NodeState newState(Node node, long world, long time) {
        return null;
    }

    @Override
    public void resolveTimepoints(Node node, long beginningOfSearch, long endOfSearch, Callback<long[]> callback) {

    }

    @Override
    public long stringToHash(String name, boolean insertIfNotExists) {
        return HashHelper.hash(name);
    }

    @Override
    public String hashToString(long key) {
        return null;
    }
}

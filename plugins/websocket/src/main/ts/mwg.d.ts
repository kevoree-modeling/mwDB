declare module java {
    module lang {
        class System {
            static gc(): void;
            static arraycopy(src: any[] | Float64Array | Int32Array, srcPos: number, dest: any[] | Float64Array | Int32Array, destPos: number, numElements: number): void;
        }
        class StringBuilder {
            private _buffer;
            length: number;
            append(val: any): StringBuilder;
            insert(position: number, val: any): StringBuilder;
            toString(): string;
        }
        class String {
            static valueOf(data: any, offset?: number, count?: number): string;
            static hashCode(str: string): number;
            static isEmpty(str: string): boolean;
            static join(delimiter: string, elements: string[]): string;
        }
        class Thread {
            static sleep(time: number): void;
        }
        class Double {
            static MAX_VALUE: number;
            static POSITIVE_INFINITY: number;
            static NEGATIVE_INFINITY: number;
            static NaN: number;
        }
        class Long {
            static parseLong(d: any): number;
        }
        class Integer {
            static parseInt(d: any): number;
        }
    }
    namespace util {
        namespace concurrent {
            namespace atomic {
                class AtomicIntegerArray {
                    _internal: Int32Array;
                    constructor(initialCapacity: number);
                    set(index: number, newVal: number): void;
                    get(index: number): number;
                    getAndSet(index: number, newVal: number): number;
                    compareAndSet(index: number, expect: number, update: number): boolean;
                }
                class AtomicLongArray {
                    _internal: Float64Array;
                    constructor(initialCapacity: number);
                    set(index: number, newVal: number): void;
                    get(index: number): number;
                    getAndSet(index: number, newVal: number): number;
                    compareAndSet(index: number, expect: number, update: number): boolean;
                    length(): number;
                }
                class AtomicReferenceArray<A> {
                    _internal: Array<A>;
                    constructor(initialCapacity: number);
                    set(index: number, newVal: A): void;
                    get(index: number): A;
                    getAndSet(index: number, newVal: A): A;
                    compareAndSet(index: number, expect: A, update: A): boolean;
                    length(): number;
                }
                class AtomicReference<A> {
                    _internal: A;
                    compareAndSet(expect: A, update: A): boolean;
                    get(): A;
                    set(newRef: A): void;
                    getAndSet(newVal: A): A;
                }
                class AtomicLong {
                    _internal: number;
                    constructor(init: number);
                    compareAndSet(expect: number, update: number): boolean;
                    get(): number;
                    incrementAndGet(): number;
                    decrementAndGet(): number;
                }
                class AtomicBoolean {
                    _internal: boolean;
                    constructor(init: boolean);
                    compareAndSet(expect: boolean, update: boolean): boolean;
                    get(): boolean;
                    set(newVal: boolean): void;
                }
                class AtomicInteger {
                    _internal: number;
                    constructor(init: number);
                    compareAndSet(expect: number, update: number): boolean;
                    get(): number;
                    set(newVal: number): void;
                    getAndSet(newVal: number): number;
                    incrementAndGet(): number;
                    decrementAndGet(): number;
                    getAndIncrement(): number;
                    getAndDecrement(): number;
                }
            }
            namespace locks {
                class ReentrantLock {
                    lock(): void;
                    unlock(): void;
                }
            }
        }
        class Random {
            private seed;
            nextInt(max?: number): number;
            nextDouble(): number;
            nextBoolean(): boolean;
            setSeed(seed: number): void;
            private nextSeeded(min?, max?);
        }
        interface Iterator<E> {
            hasNext(): boolean;
            next(): E;
        }
        class Arrays {
            static fill(data: any, begin: number, nbElem: number, param: number): void;
            static copyOf<T>(original: any[], newLength: number, ignore?: any): T[];
        }
        class Collections {
            static swap(list: List<any>, i: number, j: number): void;
        }
        interface Collection<E> {
            add(val: E): void;
            addAll(vals: Collection<E>): void;
            get(index: number): E;
            remove(o: any): any;
            clear(): void;
            isEmpty(): boolean;
            size(): number;
            contains(o: E): boolean;
            toArray<E>(a: Array<E>): E[];
            iterator(): Iterator<E>;
            containsAll(c: Collection<any>): boolean;
            addAll(c: Collection<any>): boolean;
            removeAll(c: Collection<any>): boolean;
        }
        interface List<E> extends Collection<E> {
            add(elem: E): void;
            add(index: number, elem: E): void;
            poll(): E;
            addAll(c: Collection<E>): boolean;
            addAll(index: number, c: Collection<E>): boolean;
            get(index: number): E;
            set(index: number, element: E): E;
            indexOf(o: E): number;
            lastIndexOf(o: E): number;
            remove(index: number): E;
        }
        interface Set<E> extends Collection<E> {
            forEach(f: (e: any) => void): void;
        }
        class Itr<E> implements Iterator<E> {
            cursor: number;
            lastRet: number;
            protected list: Collection<E>;
            constructor(list: Collection<E>);
            hasNext(): boolean;
            next(): E;
        }
        class HashSet<E> implements Set<E> {
            private content;
            add(val: E): void;
            clear(): void;
            contains(val: E): boolean;
            containsAll(elems: Collection<E>): boolean;
            addAll(vals: Collection<E>): boolean;
            remove(val: E): boolean;
            removeAll(): boolean;
            size(): number;
            isEmpty(): boolean;
            toArray<E>(a: Array<E>): E[];
            iterator(): Iterator<E>;
            forEach(f: (e: any) => void): void;
            get(index: number): E;
        }
        class AbstractList<E> implements List<E> {
            private content;
            addAll(index: any, vals?: any): boolean;
            clear(): void;
            poll(): E;
            remove(indexOrElem: any): any;
            removeAll(): boolean;
            toArray(a: Array<E>): E[];
            size(): number;
            add(index: any, elem?: E): void;
            get(index: number): E;
            contains(val: E): boolean;
            containsAll(elems: Collection<E>): boolean;
            isEmpty(): boolean;
            set(index: number, element: E): E;
            indexOf(element: E): number;
            lastIndexOf(element: E): number;
            iterator(): Iterator<E>;
        }
        class LinkedList<E> extends AbstractList<E> {
        }
        class ArrayList<E> extends AbstractList<E> {
        }
        class Stack<E> {
            content: any[];
            pop(): E;
            push(t: E): void;
            isEmpty(): boolean;
            peek(): E;
        }
        interface Map<K, V> {
            get(key: K): V;
            put(key: K, value: V): V;
            containsKey(key: K): boolean;
            remove(key: K): V;
            keySet(): Set<K>;
            isEmpty(): boolean;
            values(): Set<V>;
            clear(): void;
            size(): number;
        }
        class HashMap<K, V> implements Map<K, V> {
            private content;
            get(key: K): V;
            put(key: K, value: V): V;
            containsKey(key: K): boolean;
            remove(key: K): V;
            keySet(): Set<K>;
            isEmpty(): boolean;
            values(): Set<V>;
            clear(): void;
            size(): number;
        }
        class ConcurrentHashMap<K, V> extends HashMap<K, V> {
        }
    }
}
declare function arrayInstanceOf(arr: any, arg: Function): boolean;
declare class Long {
    private high;
    private low;
    private unsigned;
    private static INT_CACHE;
    private static UINT_CACHE;
    private static pow_dbl;
    private static TWO_PWR_16_DBL;
    private static TWO_PWR_24_DBL;
    private static TWO_PWR_32_DBL;
    private static TWO_PWR_64_DBL;
    private static TWO_PWR_63_DBL;
    private static TWO_PWR_24;
    static ZERO: Long;
    static UZERO: Long;
    static ONE: Long;
    static UONE: Long;
    static NEG_ONE: Long;
    static MAX_VALUE: Long;
    static MAX_UNSIGNED_VALUE: Long;
    static MIN_VALUE: Long;
    constructor(low?: number, high?: number, unsigned?: boolean);
    static isLong(obj: any): boolean;
    static fromInt(value: number, unsigned?: boolean): Long;
    static fromNumber(value: number, unsigned?: boolean): Long;
    static fromBits(lowBits?: number, highBits?: number, unsigned?: boolean): Long;
    static fromString(str: string, radix?: number, unsigned?: boolean): Long;
    static fromValue(val: any): Long;
    toInt(): number;
    toNumber(): number;
    toString(radix: number): string;
    getHighBits(): number;
    getHighBitsUnsigned(): number;
    getLowBits(): number;
    getLowBitsUnsigned(): number;
    getNumBitsAbs(): number;
    isZero(): boolean;
    isNegative(): boolean;
    isPositive(): boolean;
    isOdd(): boolean;
    isEven(): boolean;
    equals(other: any): boolean;
    eq: (other: any) => boolean;
    notEquals(other: any): boolean;
    neq: (other: any) => boolean;
    lessThan(other: any): boolean;
    lt: (other: any) => boolean;
    lessThanOrEqual(other: any): boolean;
    lte: (other: any) => boolean;
    greaterThan(other: any): boolean;
    gt: (other: any) => boolean;
    greaterThanOrEqual(other: any): boolean;
    gte: (other: any) => boolean;
    compare(other: any): number;
    comp: (other: any) => number;
    negate(): Long;
    neg: () => Long;
    add(addend: any): Long;
    subtract(subtrahend: any): Long;
    sub: (subtrahend: any) => Long;
    multiply(multiplier: any): Long;
    mul: (multiplier: any) => Long;
    divide(divisor: any): Long;
    div: (divisor: any) => Long;
    modulo(divisor: any): Long;
    mod: (divisor: any) => Long;
    not(): Long;
    and(other: any): Long;
    or(other: any): Long;
    xor(other: any): Long;
    shiftLeft(numBits: any): Long;
    shl: (numBits: any) => Long;
    shiftRight(numBits: any): Long;
    shr: (numBits: any) => Long;
    shiftRightUnsigned(numBits: any): Long;
    shru: (numBits: any) => Long;
    toSigned(): Long;
    toUnsigned(): Long;
}
declare module org {
    module mwg {
        interface Callback<A> {
            (result: A): void;
        }
        class Constants {
            static KEY_SIZE: number;
            static LONG_SIZE: number;
            static PREFIX_SIZE: number;
            static BEGINNING_OF_TIME: number;
            static END_OF_TIME: number;
            static NULL_LONG: number;
            static KEY_PREFIX_MASK: number;
            static CACHE_MISS_ERROR: string;
            static QUERY_SEP: string;
            static QUERY_KV_SEP: string;
            static TASK_SEP: string;
            static TASK_PARAM_OPEN: string;
            static TASK_PARAM_CLOSE: string;
            static CHUNK_SEP: number;
            static CHUNK_SUB_SEP: number;
            static CHUNK_SUB_SUB_SEP: number;
            static CHUNK_SUB_SUB_SUB_SEP: number;
            static BUFFER_SEP: number;
            static KEY_SEP: number;
            static MAP_INITIAL_CAPACITY: number;
            static MAP_LOAD_FACTOR: number;
            static BOOL_TRUE: number;
            static BOOL_FALSE: number;
            static isDefined(param: any): boolean;
            static equals(src: string, other: string): boolean;
            static longArrayEquals(src: Float64Array, other: Float64Array): boolean;
        }
        interface DeferCounter {
            count(): void;
            getCount(): number;
            then(job: org.mwg.plugin.Job): void;
            wrap(): org.mwg.Callback<any>;
        }
        interface DeferCounterSync extends org.mwg.DeferCounter {
            waitResult(): any;
        }
        interface Graph {
            newNode(world: number, time: number): org.mwg.Node;
            newTypedNode(world: number, time: number, nodeType: string): org.mwg.Node;
            cloneNode(origin: org.mwg.Node): org.mwg.Node;
            lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
            lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            fork(world: number): number;
            save(callback: org.mwg.Callback<boolean>): void;
            connect(callback: org.mwg.Callback<boolean>): void;
            disconnect(callback: org.mwg.Callback<boolean>): void;
            index(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
            indexAt(world: number, time: number, indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
            unindex(indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
            unindexAt(world: number, time: number, indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
            indexes(world: number, time: number, callback: org.mwg.Callback<string[]>): void;
            find(world: number, time: number, indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            findAll(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            getIndexNode(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node>): void;
            newCounter(expectedEventsCount: number): org.mwg.DeferCounter;
            newSyncCounter(expectedEventsCount: number): org.mwg.DeferCounterSync;
            resolver(): org.mwg.plugin.Resolver;
            scheduler(): org.mwg.plugin.Scheduler;
            space(): org.mwg.chunk.ChunkSpace;
            storage(): org.mwg.plugin.Storage;
            newBuffer(): org.mwg.struct.Buffer;
            newQuery(): org.mwg.Query;
            freeNodes(nodes: org.mwg.Node[]): void;
            taskAction(name: string): org.mwg.task.TaskActionFactory;
            taskHookFactory(): org.mwg.task.TaskHookFactory;
        }
        class GraphBuilder {
            private _storage;
            private _scheduler;
            private _plugins;
            private _memorySize;
            private _readOnly;
            private static _internalBuilder;
            withStorage(storage: org.mwg.plugin.Storage): org.mwg.GraphBuilder;
            withReadOnlyStorage(storage: org.mwg.plugin.Storage): org.mwg.GraphBuilder;
            withMemorySize(numberOfElements: number): org.mwg.GraphBuilder;
            withScheduler(scheduler: org.mwg.plugin.Scheduler): org.mwg.GraphBuilder;
            withPlugin(plugin: org.mwg.plugin.Plugin): org.mwg.GraphBuilder;
            build(): org.mwg.Graph;
        }
        module GraphBuilder {
            interface InternalBuilder {
                newGraph(storage: org.mwg.plugin.Storage, readOnly: boolean, scheduler: org.mwg.plugin.Scheduler, plugins: org.mwg.plugin.Plugin[], memorySize: number): org.mwg.Graph;
                newTask(): org.mwg.task.Task;
            }
        }
        interface Node {
            world(): number;
            time(): number;
            id(): number;
            get(propertyName: string): any;
            getByIndex(propIndex: number): any;
            type(propertyName: string): number;
            nodeTypeName(): string;
            set(propertyName: string, propertyValue: any): void;
            setProperty(propertyName: string, propertyType: number, propertyValue: any): void;
            forceProperty(propertyName: string, propertyType: number, propertyValue: any): void;
            setPropertyByIndex(propIndex: number, propertyType: number, propertyValue: any): void;
            getOrCreate(propertyName: string, propertyType: number): any;
            getOrCreateRel(propertyName: string): org.mwg.struct.Relationship;
            removeProperty(propertyName: string): void;
            rel(relationName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            relByIndex(relationIndex: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            add(relationName: string, relatedNode: org.mwg.Node): void;
            remove(relationName: string, relatedNode: org.mwg.Node): void;
            index(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
            unindex(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
            find(indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            findAll(indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            timeDephasing(): number;
            lastModification(): number;
            rephase(): void;
            timepoints(beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
            free(): void;
            graph(): org.mwg.Graph;
            jump<A extends org.mwg.Node>(targetTime: number, callback: org.mwg.Callback<A>): void;
        }
        interface Query {
            parse(flatQuery: string): org.mwg.Query;
            add(attributeName: string, value: string): org.mwg.Query;
            setWorld(initialWorld: number): org.mwg.Query;
            world(): number;
            setTime(initialTime: number): org.mwg.Query;
            time(): number;
            setIndexName(indexName: string): org.mwg.Query;
            indexName(): string;
            hash(): number;
            attributes(): Float64Array;
            values(): any[];
        }
        class Type {
            static BOOL: number;
            static STRING: number;
            static LONG: number;
            static INT: number;
            static DOUBLE: number;
            static DOUBLE_ARRAY: number;
            static LONG_ARRAY: number;
            static INT_ARRAY: number;
            static LONG_TO_LONG_MAP: number;
            static LONG_TO_LONG_ARRAY_MAP: number;
            static STRING_TO_LONG_MAP: number;
            static RELATION: number;
            static typeName(p_type: number): string;
        }
        module chunk {
            interface Chunk {
                world(): number;
                time(): number;
                id(): number;
                chunkType(): number;
                index(): number;
                save(buffer: org.mwg.struct.Buffer): void;
                load(buffer: org.mwg.struct.Buffer): void;
            }
            interface ChunkSpace {
                createAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                getAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                getOrLoadAndMark(type: number, world: number, time: number, id: number, callback: org.mwg.Callback<org.mwg.chunk.Chunk>): void;
                getOrLoadAndMarkAll(keys: Float64Array, callback: org.mwg.Callback<org.mwg.chunk.Chunk[]>): void;
                get(index: number): org.mwg.chunk.Chunk;
                unmark(index: number): void;
                mark(index: number): number;
                free(chunk: org.mwg.chunk.Chunk): void;
                notifyUpdate(index: number): void;
                graph(): org.mwg.Graph;
                save(callback: org.mwg.Callback<boolean>): void;
                clear(): void;
                freeAll(): void;
                available(): number;
            }
            class ChunkType {
                static STATE_CHUNK: number;
                static TIME_TREE_CHUNK: number;
                static WORLD_ORDER_CHUNK: number;
                static GEN_CHUNK: number;
            }
            interface GenChunk extends org.mwg.chunk.Chunk {
                newKey(): number;
            }
            interface Stack {
                enqueue(index: number): boolean;
                dequeueTail(): number;
                dequeue(index: number): boolean;
                free(): void;
                size(): number;
            }
            interface StateChunk extends org.mwg.chunk.Chunk, org.mwg.plugin.NodeState {
                loadFrom(origin: org.mwg.chunk.StateChunk): void;
            }
            interface TimeTreeChunk extends org.mwg.chunk.Chunk {
                insert(key: number): void;
                unsafe_insert(key: number): void;
                previousOrEqual(key: number): number;
                clearAt(max: number): void;
                range(startKey: number, endKey: number, maxElements: number, walker: org.mwg.chunk.TreeWalker): void;
                magic(): number;
                size(): number;
            }
            interface TreeWalker {
                (t: number): void;
            }
            interface WorldOrderChunk extends org.mwg.chunk.Chunk, org.mwg.struct.LongLongMap {
                magic(): number;
                lock(): void;
                unlock(): void;
                extra(): number;
                setExtra(extraValue: number): void;
            }
        }
        module plugin {
            abstract class AbstractNode implements org.mwg.Node {
                private _world;
                private _time;
                private _id;
                private _graph;
                _resolver: org.mwg.plugin.Resolver;
                _index_worldOrder: number;
                _index_superTimeTree: number;
                _index_timeTree: number;
                _index_stateChunk: number;
                _world_magic: number;
                _super_time_magic: number;
                _time_magic: number;
                _dead: boolean;
                private _lock;
                constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph);
                cacheLock(): void;
                cacheUnlock(): void;
                init(): void;
                nodeTypeName(): string;
                unphasedState(): org.mwg.plugin.NodeState;
                phasedState(): org.mwg.plugin.NodeState;
                newState(time: number): org.mwg.plugin.NodeState;
                graph(): org.mwg.Graph;
                world(): number;
                time(): number;
                id(): number;
                get(propertyName: string): any;
                set(propertyName: string, propertyValue: any): void;
                forceProperty(propertyName: string, propertyType: number, propertyValue: any): void;
                setProperty(propertyName: string, propertyType: number, propertyValue: any): void;
                private isEquals(obj1, obj2, type);
                getOrCreate(propertyName: string, propertyType: number): any;
                type(propertyName: string): number;
                removeProperty(attributeName: string): void;
                rel(relationName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                relByIndex(relationIndex: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                add(relationName: string, relatedNode: org.mwg.Node): void;
                remove(relationName: string, relatedNode: org.mwg.Node): void;
                free(): void;
                timeDephasing(): number;
                lastModification(): number;
                rephase(): void;
                timepoints(beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
                jump<A extends org.mwg.Node>(targetTime: number, callback: org.mwg.Callback<A>): void;
                findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                find(indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                findAll(indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                index(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
                unindex(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
                private isNaN(toTest);
                toString(): string;
                getOrCreateRel(propertyName: string): org.mwg.struct.Relationship;
                getByIndex(propIndex: number): any;
                setPropertyByIndex(propIndex: number, propertyType: number, propertyValue: any): void;
            }
            class AbstractPlugin implements org.mwg.plugin.Plugin {
                private _nodeTypes;
                private _taskActions;
                private _memoryFactory;
                private _resolverFactory;
                private _hookFactory;
                declareNodeType(name: string, factory: org.mwg.plugin.NodeFactory): org.mwg.plugin.Plugin;
                declareTaskAction(name: string, factory: org.mwg.task.TaskActionFactory): org.mwg.plugin.Plugin;
                declareMemoryFactory(factory: org.mwg.plugin.MemoryFactory): org.mwg.plugin.Plugin;
                declareResolverFactory(factory: org.mwg.plugin.ResolverFactory): org.mwg.plugin.Plugin;
                hookFactory(): org.mwg.task.TaskHookFactory;
                declareTaskHookFactory(factory: org.mwg.task.TaskHookFactory): org.mwg.plugin.Plugin;
                nodeTypes(): string[];
                nodeType(nodeTypeName: string): org.mwg.plugin.NodeFactory;
                taskActionTypes(): string[];
                taskActionType(taskTypeName: string): org.mwg.task.TaskActionFactory;
                memoryFactory(): org.mwg.plugin.MemoryFactory;
                resolverFactory(): org.mwg.plugin.ResolverFactory;
            }
            abstract class AbstractTaskAction implements org.mwg.task.TaskAction {
                private _next;
                setNext(p_next: org.mwg.plugin.AbstractTaskAction): void;
                next(): org.mwg.plugin.AbstractTaskAction;
                abstract eval(context: org.mwg.task.TaskContext): void;
            }
            interface Job {
                (): void;
            }
            interface MemoryFactory {
                newSpace(memorySize: number, graph: org.mwg.Graph): org.mwg.chunk.ChunkSpace;
                newBuffer(): org.mwg.struct.Buffer;
            }
            interface NodeFactory {
                (world: number, time: number, id: number, graph: org.mwg.Graph): org.mwg.Node;
            }
            interface NodeState {
                world(): number;
                time(): number;
                set(index: number, elemType: number, elem: any): void;
                setFromKey(key: string, elemType: number, elem: any): void;
                get(index: number): any;
                getFromKey(key: string): any;
                getFromKeyWithDefault<A>(key: string, defaultValue: A): A;
                getWithDefault<A>(key: number, defaultValue: A): A;
                getOrCreate(index: number, elemType: number): any;
                getOrCreateFromKey(key: string, elemType: number): any;
                getType(index: number): number;
                getTypeFromKey(key: string): number;
                each(callBack: org.mwg.plugin.NodeStateCallback): void;
            }
            interface NodeStateCallback {
                (attributeKey: number, elemType: number, elem: any): void;
            }
            interface Plugin {
                declareNodeType(name: string, factory: org.mwg.plugin.NodeFactory): org.mwg.plugin.Plugin;
                declareTaskAction(name: string, factory: org.mwg.task.TaskActionFactory): org.mwg.plugin.Plugin;
                declareMemoryFactory(factory: org.mwg.plugin.MemoryFactory): org.mwg.plugin.Plugin;
                declareTaskHookFactory(factory: org.mwg.task.TaskHookFactory): org.mwg.plugin.Plugin;
                declareResolverFactory(factory: org.mwg.plugin.ResolverFactory): org.mwg.plugin.Plugin;
                hookFactory(): org.mwg.task.TaskHookFactory;
                nodeTypes(): string[];
                nodeType(nodeTypeName: string): org.mwg.plugin.NodeFactory;
                taskActionTypes(): string[];
                taskActionType(taskTypeName: string): org.mwg.task.TaskActionFactory;
                memoryFactory(): org.mwg.plugin.MemoryFactory;
                resolverFactory(): org.mwg.plugin.ResolverFactory;
            }
            interface Resolver {
                init(): void;
                initNode(node: org.mwg.Node, typeCode: number): void;
                initWorld(parentWorld: number, childWorld: number): void;
                freeNode(node: org.mwg.Node): void;
                typeName(node: org.mwg.Node): string;
                typeCode(node: org.mwg.Node): number;
                lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
                lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                resolveState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                alignState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                newState(node: org.mwg.Node, world: number, time: number): org.mwg.plugin.NodeState;
                resolveTimepoints(node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
                stringToHash(name: string, insertIfNotExists: boolean): number;
                hashToString(key: number): string;
            }
            interface ResolverFactory {
                newResolver(storage: org.mwg.plugin.Storage, space: org.mwg.chunk.ChunkSpace): org.mwg.plugin.Resolver;
            }
            interface Scheduler {
                dispatch(affinity: number, job: org.mwg.plugin.Job): void;
                start(): void;
                stop(): void;
                workers(): number;
            }
            class SchedulerAffinity {
                static SAME_THREAD: number;
                static ANY_LOCAL_THREAD: number;
                static OTHER_LOCAL_THREAD: number;
                static ANY_REMOTE_THREAD: number;
            }
            interface Storage {
                get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
                lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                disconnect(callback: org.mwg.Callback<boolean>): void;
            }
        }
        module struct {
            interface Buffer {
                write(b: number): void;
                writeAll(bytes: Int8Array): void;
                read(position: number): number;
                data(): Int8Array;
                length(): number;
                free(): void;
                iterator(): org.mwg.struct.BufferIterator;
                removeLast(): void;
                slice(initPos: number, endPos: number): Int8Array;
            }
            interface BufferIterator {
                hasNext(): boolean;
                next(): org.mwg.struct.Buffer;
            }
            interface LongLongArrayMap extends org.mwg.struct.Map {
                get(key: number): Float64Array;
                put(key: number, value: number): void;
                remove(key: number, value: number): void;
                each(callback: org.mwg.struct.LongLongArrayMapCallBack): void;
                contains(key: number, value: number): boolean;
            }
            interface LongLongArrayMapCallBack {
                (key: number, value: number): void;
            }
            interface LongLongMap extends org.mwg.struct.Map {
                get(key: number): number;
                put(key: number, value: number): void;
                remove(key: number): void;
                each(callback: org.mwg.struct.LongLongMapCallBack): void;
            }
            interface LongLongMapCallBack {
                (key: number, value: number): void;
            }
            interface Map {
                size(): number;
            }
            interface Relationship {
                size(): number;
                get(index: number): number;
                set(index: number, value: number): void;
                add(newValue: number): org.mwg.struct.Relationship;
                remove(oldValue: number): org.mwg.struct.Relationship;
                clear(): org.mwg.struct.Relationship;
            }
            interface StringLongMap extends org.mwg.struct.Map {
                getValue(key: string): number;
                getByHash(index: number): string;
                containsHash(index: number): boolean;
                put(key: string, value: number): void;
                remove(key: string): void;
                each(callback: org.mwg.struct.StringLongMapCallBack): void;
            }
            interface StringLongMapCallBack {
                (key: string, value: number): void;
            }
        }
        module task {
            interface Action {
                (context: org.mwg.task.TaskContext): void;
            }
            class Actions {
                private static _internalBuilder;
                static newTask(): org.mwg.task.Task;
                static setWorld(variableName: string): org.mwg.task.Task;
                static setTime(variableName: string): org.mwg.task.Task;
                static then(action: org.mwg.task.Action): org.mwg.task.Task;
                static inject(input: any): org.mwg.task.Task;
                static fromVar(variableName: string): org.mwg.task.Task;
                static fromVarAt(variableName: string, index: number): org.mwg.task.Task;
                static fromIndexAll(indexName: string): org.mwg.task.Task;
                static fromIndex(indexName: string, query: string): org.mwg.task.Task;
                static indexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                static unindexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                static localIndex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
                static localUnindex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
                static indexesNames(): org.mwg.task.Task;
                static parse(flatTask: string): org.mwg.task.Task;
                static asGlobalVar(variableName: string): org.mwg.task.Task;
                static addToGlobalVar(variableName: string): org.mwg.task.Task;
                static asVar(variableName: string): org.mwg.task.Task;
                static defineVar(variableName: string): org.mwg.task.Task;
                static addToVar(variableName: string): org.mwg.task.Task;
                static map(mapFunction: org.mwg.task.TaskFunctionMap<any, any>): org.mwg.task.Task;
                static selectWith(name: string, pattern: string): org.mwg.task.Task;
                static selectWithout(name: string, pattern: string): org.mwg.task.Task;
                static select(filterFunction: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task;
                static selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task;
                static traverse(relationName: string): org.mwg.task.Task;
                static get(name: string): org.mwg.task.Task;
                static traverseIndex(indexName: string, ...queryParams: string[]): org.mwg.task.Task;
                static traverseOrKeep(relationName: string): org.mwg.task.Task;
                static traverseIndexAll(indexName: string): org.mwg.task.Task;
                static loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                static loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                static print(name: string): org.mwg.task.Task;
                static setProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task;
                static selectWhere(subTask: org.mwg.task.Task): org.mwg.task.Task;
                static foreach(subTask: org.mwg.task.Task): org.mwg.task.Task;
                static foreachPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                static flatmap(subTask: org.mwg.task.Task): org.mwg.task.Task;
                static flatmapPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                static math(expression: string): org.mwg.task.Task;
                static action(name: string, params: string): org.mwg.task.Task;
                static remove(relationName: string, variableNameToRemove: string): org.mwg.task.Task;
                static add(relationName: string, variableNameToAdd: string): org.mwg.task.Task;
                static properties(): org.mwg.task.Task;
                static propertiesWithTypes(filter: number): org.mwg.task.Task;
                static jump(time: string): org.mwg.task.Task;
                static removeProperty(propertyName: string): org.mwg.task.Task;
                static newNode(): org.mwg.task.Task;
                static newTypedNode(nodeType: string): org.mwg.task.Task;
                static save(): org.mwg.task.Task;
                static ifThen(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
                static ifThenElse(cond: org.mwg.task.TaskFunctionConditional, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                static whileDo(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
                static doWhile(then: org.mwg.task.Task, cond: org.mwg.task.TaskFunctionConditional): org.mwg.task.Task;
                static split(splitPattern: string): org.mwg.task.Task;
                static lookup(nodeId: string): org.mwg.task.Task;
                static hook(fact: org.mwg.task.TaskHookFactory): org.mwg.task.Task;
                static clear(): org.mwg.task.Task;
                static subTask(subTask: org.mwg.task.Task): org.mwg.task.Task;
                static isolate(subTask: org.mwg.task.Task): org.mwg.task.Task;
                static subTasks(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                static subTasksPar(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                static cond(mathExpression: string): org.mwg.task.TaskFunctionConditional;
            }
            interface Task {
                setWorld(template: string): org.mwg.task.Task;
                setTime(template: string): org.mwg.task.Task;
                defineVar(variableName: string): org.mwg.task.Task;
                asGlobalVar(variableName: string): org.mwg.task.Task;
                addToGlobalVar(variableName: string): org.mwg.task.Task;
                asVar(variableName: string): org.mwg.task.Task;
                addToVar(variableName: string): org.mwg.task.Task;
                fromVar(variableName: string): org.mwg.task.Task;
                fromVarAt(variableName: string, index: number): org.mwg.task.Task;
                inject(inputValue: any): org.mwg.task.Task;
                fromIndex(indexName: string, query: string): org.mwg.task.Task;
                fromIndexAll(indexName: string): org.mwg.task.Task;
                indexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                indexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                localIndex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
                unindexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                unindexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                localUnindex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
                indexesNames(): org.mwg.task.Task;
                selectWith(name: string, pattern: string): org.mwg.task.Task;
                selectWithout(name: string, pattern: string): org.mwg.task.Task;
                select(filterFunction: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task;
                newSelect(filter: org.mwg.task.TaskFunctionSelectNew): org.mwg.task.Task;
                selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task;
                selectWhere(subTask: org.mwg.task.Task): org.mwg.task.Task;
                traverse(relationName: string): org.mwg.task.Task;
                get(name: string): org.mwg.task.Task;
                traverseOrKeep(relationName: string): org.mwg.task.Task;
                traverseIndex(indexName: string, ...queryArgs: string[]): org.mwg.task.Task;
                traverseIndexAll(indexName: string): org.mwg.task.Task;
                map(mapFunction: org.mwg.task.TaskFunctionMap<any, any>): org.mwg.task.Task;
                group(groupFunction: org.mwg.task.TaskFunctionGroup): org.mwg.task.Task;
                groupWhere(groupSubTask: org.mwg.task.Task): org.mwg.task.Task;
                foreach(subTask: org.mwg.task.Task): org.mwg.task.Task;
                flatmap(subTask: org.mwg.task.Task): org.mwg.task.Task;
                foreachPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                flatmapPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                subTask(subTask: org.mwg.task.Task): org.mwg.task.Task;
                isolate(subTask: org.mwg.task.Task): org.mwg.task.Task;
                subTasks(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                subTasksPar(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                ifThen(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
                ifThenElse(cond: org.mwg.task.TaskFunctionConditional, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                whileDo(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
                doWhile(then: org.mwg.task.Task, conditional: org.mwg.task.TaskFunctionConditional): org.mwg.task.Task;
                then(action: org.mwg.task.Action): org.mwg.task.Task;
                save(): org.mwg.task.Task;
                clear(): org.mwg.task.Task;
                newNode(): org.mwg.task.Task;
                newTypedNode(typeNode: string): org.mwg.task.Task;
                setProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task;
                forceProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task;
                removeProperty(propertyName: string): org.mwg.task.Task;
                add(relationName: string, variableToAdd: string): org.mwg.task.Task;
                addTo(relationName: string, variableTarget: string): org.mwg.task.Task;
                properties(): org.mwg.task.Task;
                propertiesWithTypes(filterType: number): org.mwg.task.Task;
                remove(relationName: string, variableNameToRemove: string): org.mwg.task.Task;
                jump(time: string): org.mwg.task.Task;
                parse(flat: string): org.mwg.task.Task;
                action(name: string, params: string): org.mwg.task.Task;
                split(splitPattern: string): org.mwg.task.Task;
                lookup(nodeId: string): org.mwg.task.Task;
                lookupAll(nodeId: string): org.mwg.task.Task;
                math(expression: string): org.mwg.task.Task;
                loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                print(name: string): org.mwg.task.Task;
                println(name: string): org.mwg.task.Task;
                hook(hookFactory: org.mwg.task.TaskHookFactory): org.mwg.task.Task;
                execute(graph: org.mwg.Graph, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                executeSync(graph: org.mwg.Graph): org.mwg.task.TaskResult<any>;
                executeWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                prepareWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): org.mwg.task.TaskContext;
                executeUsing(preparedContext: org.mwg.task.TaskContext): void;
                executeFrom(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                executeFromUsing(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, contextInitializer: org.mwg.Callback<org.mwg.task.TaskContext>, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                emptyResult(): org.mwg.task.TaskResult<any>;
                mathConditional(mathExpression: string): org.mwg.task.TaskFunctionConditional;
            }
            interface TaskAction {
                eval(context: org.mwg.task.TaskContext): void;
            }
            interface TaskActionFactory {
                (params: string[]): org.mwg.task.TaskAction;
            }
            interface TaskContext {
                graph(): org.mwg.Graph;
                world(): number;
                setWorld(world: number): void;
                time(): number;
                setTime(time: number): void;
                variable(name: string): org.mwg.task.TaskResult<any>;
                wrap(input: any): org.mwg.task.TaskResult<any>;
                wrapClone(input: any): org.mwg.task.TaskResult<any>;
                newResult(): org.mwg.task.TaskResult<any>;
                declareVariable(name: string): void;
                defineVariable(name: string, initialResult: any): void;
                defineVariableForSubTask(name: string, initialResult: any): void;
                setGlobalVariable(name: string, value: any): void;
                setVariable(name: string, value: any): void;
                addToGlobalVariable(name: string, value: any): void;
                addToVariable(name: string, value: any): void;
                result(): org.mwg.task.TaskResult<any>;
                resultAsNodes(): org.mwg.task.TaskResult<org.mwg.Node>;
                resultAsStrings(): org.mwg.task.TaskResult<string>;
                continueTask(): void;
                continueWith(nextResult: org.mwg.task.TaskResult<any>): void;
                template(input: string): string;
                hook(): org.mwg.task.TaskHook;
            }
            interface TaskFunctionConditional {
                (context: org.mwg.task.TaskContext): boolean;
            }
            interface TaskFunctionGroup {
                (nodes: org.mwg.Node): number;
            }
            interface TaskFunctionMap<A, B> {
                (node: A): B;
            }
            interface TaskFunctionSelect {
                (node: org.mwg.Node): boolean;
            }
            interface TaskFunctionSelectNew {
                (node: org.mwg.Node, context: org.mwg.task.TaskContext): boolean;
            }
            interface TaskFunctionSelectObject {
                (object: any, context: org.mwg.task.TaskContext): boolean;
            }
            interface TaskHook {
                start(initialContext: org.mwg.task.TaskContext): void;
                beforeAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void;
                afterAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void;
                beforeTask(parentContext: org.mwg.task.TaskContext, context: org.mwg.task.TaskContext): void;
                afterTask(context: org.mwg.task.TaskContext): void;
                end(finalContext: org.mwg.task.TaskContext): void;
            }
            interface TaskHookFactory {
                newHook(): org.mwg.task.TaskHook;
            }
            interface TaskResult<A> {
                iterator(): org.mwg.task.TaskResultIterator<any>;
                get(index: number): A;
                set(index: number, input: A): void;
                allocate(index: number): void;
                add(input: A): void;
                clear(): void;
                clone(): org.mwg.task.TaskResult<A>;
                free(): void;
                size(): number;
                asArray(): any[];
            }
            interface TaskResultIterator<A> {
                next(): A;
                nextWithIndex(): org.mwg.utility.Tuple<number, A>;
            }
        }
        module utility {
            class Base64 {
                private static dictionary;
                private static powTwo;
                private static longIndexes;
                static encodeLongToBuffer(l: number, buffer: org.mwg.struct.Buffer): void;
                static encodeIntToBuffer(l: number, buffer: org.mwg.struct.Buffer): void;
                static decodeToLong(s: org.mwg.struct.Buffer): number;
                static decodeToLongWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
                static decodeToInt(s: org.mwg.struct.Buffer): number;
                static decodeToIntWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
                static encodeDoubleToBuffer(d: number, buffer: org.mwg.struct.Buffer): void;
                static decodeToDouble(s: org.mwg.struct.Buffer): number;
                static decodeToDoubleWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
                static encodeBoolArrayToBuffer(boolArr: Array<boolean>, buffer: org.mwg.struct.Buffer): void;
                static decodeBoolArray(s: org.mwg.struct.Buffer, arraySize: number): any[];
                static decodeToBoolArrayWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number, arraySize: number): any[];
                static encodeStringToBuffer(s: string, buffer: org.mwg.struct.Buffer): void;
                static decodeString(s: org.mwg.struct.Buffer): string;
                static decodeToStringWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): string;
            }
            class BufferView implements org.mwg.struct.Buffer {
                private _origin;
                private _initPos;
                private _endPos;
                constructor(p_origin: org.mwg.struct.Buffer, p_initPos: number, p_endPos: number);
                write(b: number): void;
                writeAll(bytes: Int8Array): void;
                read(position: number): number;
                data(): Int8Array;
                length(): number;
                free(): void;
                iterator(): org.mwg.struct.BufferIterator;
                removeLast(): void;
                slice(initPos: number, endPos: number): Int8Array;
            }
            class DefaultBufferIterator implements org.mwg.struct.BufferIterator {
                private _origin;
                private _originSize;
                private _cursor;
                constructor(p_origin: org.mwg.struct.Buffer);
                hasNext(): boolean;
                next(): org.mwg.struct.Buffer;
            }
            class Enforcer {
                private checkers;
                asBool(propertyName: string): org.mwg.utility.Enforcer;
                asString(propertyName: string): org.mwg.utility.Enforcer;
                asLong(propertyName: string): org.mwg.utility.Enforcer;
                asLongWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer;
                asDouble(propertyName: string): org.mwg.utility.Enforcer;
                asDoubleWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer;
                asInt(propertyName: string): org.mwg.utility.Enforcer;
                asIntWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer;
                asIntGreaterOrEquals(propertyName: string, min: number): org.mwg.utility.Enforcer;
                asDoubleArray(propertyName: string): org.mwg.utility.Enforcer;
                asPositiveInt(propertyName: string): org.mwg.utility.Enforcer;
                asNonNegativeDouble(propertyName: string): org.mwg.utility.Enforcer;
                asPositiveDouble(propertyName: string): org.mwg.utility.Enforcer;
                asNonNegativeOrNanDouble(propertyName: string): org.mwg.utility.Enforcer;
                asPositiveLong(propertyName: string): org.mwg.utility.Enforcer;
                declare(propertyName: string, checker: org.mwg.utility.EnforcerChecker): org.mwg.utility.Enforcer;
                check(propertyName: string, propertyType: number, propertyValue: any): void;
            }
            interface EnforcerChecker {
                check(inputType: number, input: any): void;
            }
            class HashHelper {
                private static PRIME1;
                private static PRIME2;
                private static PRIME3;
                private static PRIME4;
                private static PRIME5;
                private static len;
                private static byteTable;
                private static HSTART;
                private static HMULT;
                static longHash(number: number, max: number): number;
                static tripleHash(p0: number, p1: number, p2: number, p3: number, max: number): number;
                static rand(): number;
                static equals(src: string, other: string): boolean;
                static DOUBLE_MIN_VALUE(): number;
                static DOUBLE_MAX_VALUE(): number;
                static isDefined(param: any): boolean;
                static hash(data: string): number;
                static hashBytes(data: Int8Array): number;
            }
            class KeyHelper {
                static keyToBuffer(buffer: org.mwg.struct.Buffer, chunkType: number, world: number, time: number, id: number): void;
            }
            class Tuple<A, B> {
                private _left;
                private _right;
                constructor(p_left: A, p_right: B);
                left(): A;
                right(): B;
            }
            class VerboseHook implements org.mwg.task.TaskHook {
                private ctxIdents;
                start(initialContext: org.mwg.task.TaskContext): void;
                beforeAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void;
                afterAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void;
                beforeTask(parentContext: org.mwg.task.TaskContext, context: org.mwg.task.TaskContext): void;
                afterTask(context: org.mwg.task.TaskContext): void;
                end(finalContext: org.mwg.task.TaskContext): void;
            }
            class VerboseHookFactory implements org.mwg.task.TaskHookFactory {
                newHook(): org.mwg.task.TaskHook;
            }
            class VerbosePlugin extends org.mwg.plugin.AbstractPlugin {
                constructor();
            }
        }
    }
}
declare module org {
    module mwg {
        module core {
            class BlackHoleStorage implements org.mwg.plugin.Storage {
                private _graph;
                private prefix;
                get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
                lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                disconnect(callback: org.mwg.Callback<boolean>): void;
            }
            class Builder implements org.mwg.GraphBuilder.InternalBuilder {
                newGraph(p_storage: org.mwg.plugin.Storage, p_readOnly: boolean, p_scheduler: org.mwg.plugin.Scheduler, p_plugins: org.mwg.plugin.Plugin[], p_memorySize: number): org.mwg.Graph;
                newTask(): org.mwg.task.Task;
            }
            class CoreConstants extends org.mwg.Constants {
                static PREFIX_TO_SAVE_SIZE: number;
                static NULL_KEY: Float64Array;
                static GLOBAL_UNIVERSE_KEY: Float64Array;
                static GLOBAL_DICTIONARY_KEY: Float64Array;
                static GLOBAL_INDEX_KEY: Float64Array;
                static INDEX_ATTRIBUTE: string;
                static DISCONNECTED_ERROR: string;
                static SCALE_1: number;
                static SCALE_2: number;
                static SCALE_3: number;
                static SCALE_4: number;
                static DEAD_NODE_ERROR: string;
            }
            class CoreGraph implements org.mwg.Graph {
                private _storage;
                private _space;
                private _scheduler;
                private _resolver;
                private _nodeTypes;
                private _taskActions;
                private _isConnected;
                private _lock;
                private _plugins;
                private _memoryFactory;
                private _hookFactory;
                private _prefix;
                private _nodeKeyCalculator;
                private _worldKeyCalculator;
                constructor(p_storage: org.mwg.plugin.Storage, memorySize: number, p_scheduler: org.mwg.plugin.Scheduler, p_plugins: org.mwg.plugin.Plugin[]);
                fork(world: number): number;
                newNode(world: number, time: number): org.mwg.Node;
                newTypedNode(world: number, time: number, nodeType: string): org.mwg.Node;
                cloneNode(origin: org.mwg.Node): org.mwg.Node;
                factoryByCode(code: number): org.mwg.plugin.NodeFactory;
                taskAction(taskActionName: string): org.mwg.task.TaskActionFactory;
                taskHookFactory(): org.mwg.task.TaskHookFactory;
                lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
                lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                save(callback: org.mwg.Callback<boolean>): void;
                connect(callback: org.mwg.Callback<boolean>): void;
                disconnect(callback: org.mwg.Callback<any>): void;
                newBuffer(): org.mwg.struct.Buffer;
                newQuery(): org.mwg.Query;
                index(indexName: string, toIndexNode: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
                indexAt(world: number, time: number, indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
                unindex(indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
                unindexAt(world: number, time: number, indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
                indexes(world: number, time: number, callback: org.mwg.Callback<string[]>): void;
                find(world: number, time: number, indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                findAll(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                getIndexNode(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node>): void;
                private getIndexOrCreate(world, time, indexName, createIfNull, callback);
                newCounter(expectedCountCalls: number): org.mwg.DeferCounter;
                newSyncCounter(expectedCountCalls: number): org.mwg.DeferCounterSync;
                resolver(): org.mwg.plugin.Resolver;
                scheduler(): org.mwg.plugin.Scheduler;
                space(): org.mwg.chunk.ChunkSpace;
                storage(): org.mwg.plugin.Storage;
                freeNodes(nodes: org.mwg.Node[]): void;
            }
            class CoreNode extends org.mwg.plugin.AbstractNode {
                constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph);
            }
            class CoreQuery implements org.mwg.Query {
                private _resolver;
                private _graph;
                private capacity;
                private _attributes;
                private _values;
                private size;
                private _hash;
                private _world;
                private _time;
                private _indexName;
                constructor(graph: org.mwg.Graph, p_resolver: org.mwg.plugin.Resolver);
                parse(flatQuery: string): org.mwg.Query;
                add(attributeName: string, value: string): org.mwg.Query;
                setWorld(initialWorld: number): org.mwg.Query;
                world(): number;
                setTime(initialTime: number): org.mwg.Query;
                time(): number;
                setIndexName(indexName: string): org.mwg.Query;
                indexName(): string;
                hash(): number;
                attributes(): Float64Array;
                values(): any[];
                private internal_add(att, val);
                private compute();
            }
            class MWGResolver implements org.mwg.plugin.Resolver {
                private _storage;
                private _space;
                private _graph;
                private dictionary;
                private globalWorldOrderChunk;
                private static KEY_SIZE;
                constructor(p_storage: org.mwg.plugin.Storage, p_space: org.mwg.chunk.ChunkSpace, p_graph: org.mwg.Graph);
                init(): void;
                typeName(node: org.mwg.Node): string;
                typeCode(node: org.mwg.Node): number;
                initNode(node: org.mwg.Node, codeType: number): void;
                initWorld(parentWorld: number, childWorld: number): void;
                freeNode(node: org.mwg.Node): void;
                lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
                private lookupAll_end(finalResult, callback, sizeIds, worldOrders, superTimes, times, chunks);
                lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                private resolve_world(globalWorldOrder, nodeWorldOrder, timeToResolve, originWorld);
                private getOrLoadAndMarkAll(types, keys, callback);
                resolveState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                private internal_resolveState(node, safe);
                alignState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                newState(node: org.mwg.Node, world: number, time: number): org.mwg.plugin.NodeState;
                resolveTimepoints(node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
                private resolveTimepointsFromWorlds(globalWorldOrder, objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds, collectedWorldsSize, callback);
                private resolveTimepointsFromSuperTimes(objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds, collectedSuperTimes, collectedSize, callback);
                stringToHash(name: string, insertIfNotExists: boolean): number;
                hashToString(key: number): string;
            }
            module chunk {
                module heap {
                    class HeapAtomicByteArray {
                        private _back;
                        constructor(initialSize: number);
                        get(index: number): number;
                        set(index: number, value: number): void;
                    }
                    class HeapChunkSpace implements org.mwg.chunk.ChunkSpace {
                        private static HASH_LOAD_FACTOR;
                        private _maxEntries;
                        private _hashEntries;
                        private _lru;
                        private _dirtiesStack;
                        private _hashNext;
                        private _hash;
                        private _chunkWorlds;
                        private _chunkTimes;
                        private _chunkIds;
                        private _chunkTypes;
                        private _chunkValues;
                        private _chunkMarks;
                        private _graph;
                        graph(): org.mwg.Graph;
                        worldByIndex(index: number): number;
                        timeByIndex(index: number): number;
                        idByIndex(index: number): number;
                        constructor(initialCapacity: number, p_graph: org.mwg.Graph);
                        getAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                        get(index: number): org.mwg.chunk.Chunk;
                        getOrLoadAndMark(type: number, world: number, time: number, id: number, callback: org.mwg.Callback<org.mwg.chunk.Chunk>): void;
                        getOrLoadAndMarkAll(keys: Float64Array, callback: org.mwg.Callback<org.mwg.chunk.Chunk[]>): void;
                        mark(index: number): number;
                        unmark(index: number): void;
                        free(chunk: org.mwg.chunk.Chunk): void;
                        createAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                        notifyUpdate(index: number): void;
                        save(callback: org.mwg.Callback<boolean>): void;
                        clear(): void;
                        freeAll(): void;
                        available(): number;
                        printMarked(): void;
                    }
                    class HeapFixedStack implements org.mwg.chunk.Stack {
                        private _next;
                        private _prev;
                        private _capacity;
                        private _first;
                        private _last;
                        private _count;
                        constructor(capacity: number, fill: boolean);
                        enqueue(index: number): boolean;
                        dequeueTail(): number;
                        dequeue(index: number): boolean;
                        free(): void;
                        size(): number;
                    }
                    class HeapGenChunk implements org.mwg.chunk.GenChunk {
                        private _space;
                        private _index;
                        private _prefix;
                        private _seed;
                        private _dirty;
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_id: number, p_index: number);
                        save(buffer: org.mwg.struct.Buffer): void;
                        load(buffer: org.mwg.struct.Buffer): void;
                        newKey(): number;
                        index(): number;
                        world(): number;
                        time(): number;
                        id(): number;
                        chunkType(): number;
                    }
                    class HeapLongLongArrayMap implements org.mwg.struct.LongLongArrayMap {
                        private parent;
                        private mapSize;
                        private capacity;
                        private keys;
                        private values;
                        private nexts;
                        private hashs;
                        constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk);
                        private key(i);
                        private setKey(i, newValue);
                        private value(i);
                        private setValue(i, newValue);
                        private next(i);
                        private setNext(i, newValue);
                        private hash(i);
                        private setHash(i, newValue);
                        reallocate(newCapacity: number): void;
                        cloneFor(newParent: org.mwg.core.chunk.heap.HeapStateChunk): org.mwg.core.chunk.heap.HeapLongLongArrayMap;
                        get(requestKey: number): Float64Array;
                        contains(requestKey: number, requestValue: number): boolean;
                        each(callback: org.mwg.struct.LongLongArrayMapCallBack): void;
                        unsafe_each(callback: org.mwg.struct.LongLongArrayMapCallBack): void;
                        size(): number;
                        remove(requestKey: number, requestValue: number): void;
                        put(insertKey: number, insertValue: number): void;
                    }
                    class HeapLongLongMap implements org.mwg.struct.LongLongMap {
                        private parent;
                        private mapSize;
                        private capacity;
                        private keys;
                        private values;
                        private nexts;
                        private hashs;
                        constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk);
                        private key(i);
                        private setKey(i, newValue);
                        private value(i);
                        private setValue(i, newValue);
                        private next(i);
                        private setNext(i, newValue);
                        private hash(i);
                        private setHash(i, newValue);
                        reallocate(newCapacity: number): void;
                        cloneFor(newParent: org.mwg.core.chunk.heap.HeapStateChunk): org.mwg.core.chunk.heap.HeapLongLongMap;
                        get(requestKey: number): number;
                        each(callback: org.mwg.struct.LongLongMapCallBack): void;
                        unsafe_each(callback: org.mwg.struct.LongLongMapCallBack): void;
                        size(): number;
                        remove(requestKey: number): void;
                        put(insertKey: number, insertValue: number): void;
                    }
                    class HeapRelationship implements org.mwg.struct.Relationship {
                        private _back;
                        private _size;
                        private parent;
                        private aligned;
                        constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk, origin: org.mwg.core.chunk.heap.HeapRelationship);
                        allocate(_capacity: number): void;
                        size(): number;
                        get(index: number): number;
                        set(index: number, value: number): void;
                        unsafe_get(index: number): number;
                        add(newValue: number): org.mwg.struct.Relationship;
                        remove(oldValue: number): org.mwg.struct.Relationship;
                        clear(): org.mwg.struct.Relationship;
                        toString(): string;
                    }
                    class HeapStateChunk implements org.mwg.chunk.StateChunk {
                        private _index;
                        private _space;
                        private _capacity;
                        private _size;
                        private _k;
                        private _v;
                        private _next;
                        private _hash;
                        private _type;
                        private _dirty;
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number);
                        world(): number;
                        time(): number;
                        id(): number;
                        chunkType(): number;
                        index(): number;
                        get(p_key: number): any;
                        private internal_find(p_key);
                        private internal_get(p_key);
                        set(p_elementIndex: number, p_elemType: number, p_unsafe_elem: any): void;
                        setFromKey(key: string, p_elemType: number, p_unsafe_elem: any): void;
                        getFromKey(key: string): any;
                        getFromKeyWithDefault<A>(key: string, defaultValue: A): A;
                        getWithDefault<A>(key: number, defaultValue: A): A;
                        getType(p_key: number): number;
                        getTypeFromKey(key: string): number;
                        getOrCreate(p_key: number, p_type: number): any;
                        getOrCreateFromKey(key: string, elemType: number): any;
                        declareDirty(): void;
                        save(buffer: org.mwg.struct.Buffer): void;
                        each(callBack: org.mwg.plugin.NodeStateCallback): void;
                        loadFrom(origin: org.mwg.chunk.StateChunk): void;
                        private internal_set(p_key, p_type, p_unsafe_elem, replaceIfPresent, initial);
                        private allocate(newCapacity);
                        load(buffer: org.mwg.struct.Buffer): void;
                    }
                    class HeapStringLongMap implements org.mwg.struct.StringLongMap {
                        private parent;
                        private mapSize;
                        private capacity;
                        private keys;
                        private keysH;
                        private values;
                        private nexts;
                        private hashs;
                        constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk);
                        private key(i);
                        private setKey(i, newValue);
                        private keyH(i);
                        private setKeyH(i, newValue);
                        private value(i);
                        private setValue(i, newValue);
                        private next(i);
                        private setNext(i, newValue);
                        private hash(i);
                        private setHash(i, newValue);
                        reallocate(newCapacity: number): void;
                        cloneFor(newParent: org.mwg.core.chunk.heap.HeapStateChunk): org.mwg.core.chunk.heap.HeapStringLongMap;
                        getValue(requestString: string): number;
                        getByHash(keyHash: number): string;
                        containsHash(keyHash: number): boolean;
                        each(callback: org.mwg.struct.StringLongMapCallBack): void;
                        unsafe_each(callback: org.mwg.struct.StringLongMapCallBack): void;
                        size(): number;
                        remove(requestKey: string): void;
                        put(insertKey: string, insertValue: number): void;
                    }
                    class HeapTimeTreeChunk implements org.mwg.chunk.TimeTreeChunk {
                        private static META_SIZE;
                        private _index;
                        private _space;
                        private _root;
                        private _back_meta;
                        private _k;
                        private _colors;
                        private _magic;
                        private _size;
                        private _dirty;
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number);
                        world(): number;
                        time(): number;
                        id(): number;
                        size(): number;
                        range(startKey: number, endKey: number, maxElements: number, walker: org.mwg.chunk.TreeWalker): void;
                        save(buffer: org.mwg.struct.Buffer): void;
                        load(buffer: org.mwg.struct.Buffer): void;
                        index(): number;
                        previousOrEqual(key: number): number;
                        magic(): number;
                        insert(p_key: number): void;
                        unsafe_insert(p_key: number): void;
                        chunkType(): number;
                        clearAt(max: number): void;
                        private reallocate(newCapacity);
                        private key(p_currentIndex);
                        private setKey(p_currentIndex, p_paramIndex);
                        private left(p_currentIndex);
                        private setLeft(p_currentIndex, p_paramIndex);
                        private right(p_currentIndex);
                        private setRight(p_currentIndex, p_paramIndex);
                        private parent(p_currentIndex);
                        private setParent(p_currentIndex, p_paramIndex);
                        private color(p_currentIndex);
                        private setColor(p_currentIndex, p_paramIndex);
                        private grandParent(p_currentIndex);
                        private sibling(p_currentIndex);
                        private uncle(p_currentIndex);
                        private previous(p_index);
                        private internal_previousOrEqual_index(p_key);
                        private rotateLeft(n);
                        private rotateRight(n);
                        private replaceNode(oldn, newn);
                        private insertCase1(n);
                        private insertCase2(n);
                        private insertCase3(n);
                        private insertCase4(n_n);
                        private insertCase5(n);
                        private internal_insert(p_key);
                        private internal_set_dirty();
                    }
                    class HeapWorldOrderChunk implements org.mwg.chunk.WorldOrderChunk {
                        private _space;
                        private _index;
                        private _lock;
                        private _magic;
                        private _extra;
                        private _size;
                        private _capacity;
                        private _kv;
                        private _next;
                        private _hash;
                        private _dirty;
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number);
                        world(): number;
                        time(): number;
                        id(): number;
                        extra(): number;
                        setExtra(extraValue: number): void;
                        lock(): void;
                        unlock(): void;
                        magic(): number;
                        each(callback: org.mwg.struct.LongLongMapCallBack): void;
                        get(key: number): number;
                        put(key: number, value: number): void;
                        private internal_put(key, value, notifyUpdate);
                        private resize(newCapacity);
                        load(buffer: org.mwg.struct.Buffer): void;
                        index(): number;
                        remove(key: number): void;
                        size(): number;
                        chunkType(): number;
                        save(buffer: org.mwg.struct.Buffer): void;
                    }
                }
            }
            module memory {
                class HeapBuffer implements org.mwg.struct.Buffer {
                    private buffer;
                    private writeCursor;
                    slice(initPos: number, endPos: number): Int8Array;
                    write(b: number): void;
                    private getNewSize(old, target);
                    writeAll(bytes: Int8Array): void;
                    read(position: number): number;
                    data(): Int8Array;
                    length(): number;
                    free(): void;
                    iterator(): org.mwg.struct.BufferIterator;
                    removeLast(): void;
                    toString(): string;
                }
                class HeapMemoryFactory implements org.mwg.plugin.MemoryFactory {
                    newSpace(memorySize: number, graph: org.mwg.Graph): org.mwg.chunk.ChunkSpace;
                    newBuffer(): org.mwg.struct.Buffer;
                }
            }
            module scheduler {
                class JobQueue {
                    private first;
                    private last;
                    add(item: org.mwg.plugin.Job): void;
                    poll(): org.mwg.plugin.Job;
                }
                module JobQueue {
                    class JobQueueElem {
                        _ptr: org.mwg.plugin.Job;
                        _next: org.mwg.core.scheduler.JobQueue.JobQueueElem;
                        constructor(ptr: org.mwg.plugin.Job, next: org.mwg.core.scheduler.JobQueue.JobQueueElem);
                    }
                }
                class NoopScheduler implements org.mwg.plugin.Scheduler {
                    dispatch(affinity: number, job: org.mwg.plugin.Job): void;
                    start(): void;
                    stop(): void;
                    workers(): number;
                }
                class TrampolineScheduler implements org.mwg.plugin.Scheduler {
                    private queue;
                    private wip;
                    dispatch(affinity: number, job: org.mwg.plugin.Job): void;
                    start(): void;
                    stop(): void;
                    workers(): number;
                }
            }
            module task {
                class ActionAdd extends org.mwg.plugin.AbstractTaskAction {
                    private _relationName;
                    private _variableNameToAdd;
                    constructor(relationName: string, variableNameToAdd: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionAddTo extends org.mwg.plugin.AbstractTaskAction {
                    private _relationName;
                    private _variableNameTarget;
                    constructor(relationName: string, variableNameTarget: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionAddToVar extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    private _global;
                    constructor(p_name: string, p_global: boolean);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionAsVar extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    private _global;
                    constructor(p_name: string, p_global: boolean);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionClear extends org.mwg.plugin.AbstractTaskAction {
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionDefineVar extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    constructor(p_name: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionDoWhile extends org.mwg.plugin.AbstractTaskAction {
                    private _cond;
                    private _then;
                    constructor(p_then: org.mwg.task.Task, p_cond: org.mwg.task.TaskFunctionConditional);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionFlatmap extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionFlatmapPar extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionForeach extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionForeachPar extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionFromIndex extends org.mwg.plugin.AbstractTaskAction {
                    private _indexName;
                    private _query;
                    constructor(p_indexName: string, p_query: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionFromIndexAll extends org.mwg.plugin.AbstractTaskAction {
                    private _indexName;
                    constructor(p_indexName: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionFromVar extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    private _index;
                    constructor(p_name: string, p_index: number);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionGet extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    constructor(p_name: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionIfThen extends org.mwg.plugin.AbstractTaskAction {
                    private _condition;
                    private _action;
                    constructor(cond: org.mwg.task.TaskFunctionConditional, action: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionIfThenElse extends org.mwg.plugin.AbstractTaskAction {
                    private _condition;
                    private _thenSub;
                    private _elseSub;
                    constructor(cond: org.mwg.task.TaskFunctionConditional, p_thenSub: org.mwg.task.Task, p_elseSub: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionIndexOrUnindexNode extends org.mwg.plugin.AbstractTaskAction {
                    private _indexName;
                    private _flatKeyAttributes;
                    private _isIndexation;
                    constructor(indexName: string, flatKeyAttributes: string, isIndexation: boolean);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionIndexOrUnindexNodeAt extends org.mwg.plugin.AbstractTaskAction {
                    private _indexName;
                    private _flatKeyAttributes;
                    private _isIndexation;
                    private _world;
                    private _time;
                    constructor(world: string, time: string, indexName: string, flatKeyAttributes: string, isIndexation: boolean);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionIndexesNames extends org.mwg.plugin.AbstractTaskAction {
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionInject extends org.mwg.plugin.AbstractTaskAction {
                    private _value;
                    constructor(value: any);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionIsolate extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionJump extends org.mwg.plugin.AbstractTaskAction {
                    private _time;
                    constructor(time: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionLocalIndexOrUnindex extends org.mwg.plugin.AbstractTaskAction {
                    private _indexedRelation;
                    private _flatKeyAttributes;
                    private _isIndexation;
                    private _varNodeToAdd;
                    constructor(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string, _isIndexation: boolean);
                    eval(context: org.mwg.task.TaskContext): void;
                }
                class ActionLookup extends org.mwg.plugin.AbstractTaskAction {
                    private _id;
                    constructor(p_id: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionLookupAll extends org.mwg.plugin.AbstractTaskAction {
                    private _ids;
                    constructor(p_ids: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionLoop extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    private _lower;
                    private _upper;
                    constructor(p_lower: string, p_upper: string, p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionLoopPar extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    private _lower;
                    private _upper;
                    constructor(p_lower: string, p_upper: string, p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionMap extends org.mwg.plugin.AbstractTaskAction {
                    private _map;
                    constructor(p_map: org.mwg.task.TaskFunctionMap<any, any>);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionMath extends org.mwg.plugin.AbstractTaskAction {
                    private _engine;
                    private _expression;
                    constructor(mathExpression: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionNewNode extends org.mwg.plugin.AbstractTaskAction {
                    private _typeNode;
                    constructor(typeNode: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionPlugin extends org.mwg.plugin.AbstractTaskAction {
                    private _actionName;
                    private _flatParams;
                    private subAction;
                    constructor(actionName: string, flatParams: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionPrint extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    private _withLineBreak;
                    constructor(p_name: string, withLineBreak: boolean);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionProperties extends org.mwg.plugin.AbstractTaskAction {
                    private _filter;
                    constructor(filterType: number);
                    eval(context: org.mwg.task.TaskContext): void;
                }
                class ActionRemove extends org.mwg.plugin.AbstractTaskAction {
                    private _relationName;
                    private _variableNameToRemove;
                    constructor(relationName: string, variableNameToRemove: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionRemoveProperty extends org.mwg.plugin.AbstractTaskAction {
                    private _propertyName;
                    constructor(propertyName: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionSave extends org.mwg.plugin.AbstractTaskAction {
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionSelect extends org.mwg.plugin.AbstractTaskAction {
                    private _filter;
                    constructor(p_filter: org.mwg.task.TaskFunctionSelect);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionSelectNew extends org.mwg.plugin.AbstractTaskAction {
                    private _filter;
                    constructor(filter: org.mwg.task.TaskFunctionSelectNew);
                    eval(context: org.mwg.task.TaskContext): void;
                }
                class ActionSelectObject extends org.mwg.plugin.AbstractTaskAction {
                    private _filter;
                    constructor(filterFunction: org.mwg.task.TaskFunctionSelectObject);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionSetProperty extends org.mwg.plugin.AbstractTaskAction {
                    private _relationName;
                    private _variableNameToSet;
                    private _propertyType;
                    private _force;
                    constructor(relationName: string, propertyType: number, variableNameToSet: string, force: boolean);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                    private parseBoolean(booleanValue);
                }
                class ActionSplit extends org.mwg.plugin.AbstractTaskAction {
                    private _splitPattern;
                    constructor(p_splitPattern: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionSubTask extends org.mwg.plugin.AbstractTaskAction {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionSubTasks extends org.mwg.plugin.AbstractTaskAction {
                    private _subTasks;
                    constructor(p_subTasks: org.mwg.task.Task[]);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionSubTasksPar extends org.mwg.plugin.AbstractTaskAction {
                    private _subTasks;
                    constructor(p_subTasks: org.mwg.task.Task[]);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionTime extends org.mwg.plugin.AbstractTaskAction {
                    private _varName;
                    constructor(p_varName: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionTraverse extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    constructor(p_name: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionTraverseIndex extends org.mwg.plugin.AbstractTaskAction {
                    private _indexName;
                    private _queryParams;
                    private _resolvedQueryParams;
                    constructor(indexName: string, ...queryParams: string[]);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionTraverseIndexAll extends org.mwg.plugin.AbstractTaskAction {
                    private _indexName;
                    constructor(indexName: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionTraverseOrKeep extends org.mwg.plugin.AbstractTaskAction {
                    private _name;
                    constructor(p_name: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionWhileDo extends org.mwg.plugin.AbstractTaskAction {
                    private _cond;
                    private _then;
                    constructor(p_cond: org.mwg.task.TaskFunctionConditional, p_then: org.mwg.task.Task);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionWith extends org.mwg.plugin.AbstractTaskAction {
                    private _patternTemplate;
                    private _name;
                    constructor(name: string, stringPattern: string);
                    toString(): string;
                    eval(context: org.mwg.task.TaskContext): void;
                }
                class ActionWithout extends org.mwg.plugin.AbstractTaskAction {
                    private _patternTemplate;
                    private _name;
                    constructor(name: string, stringPattern: string);
                    toString(): string;
                    eval(context: org.mwg.task.TaskContext): void;
                }
                class ActionWorld extends org.mwg.plugin.AbstractTaskAction {
                    private _varName;
                    constructor(p_varName: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class ActionWrapper extends org.mwg.plugin.AbstractTaskAction {
                    private _wrapped;
                    constructor(p_wrapped: org.mwg.task.Action);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class CoreTask implements org.mwg.task.Task {
                    private _first;
                    private _last;
                    private _hookFactory;
                    private addAction(nextAction);
                    setWorld(template: string): org.mwg.task.Task;
                    setTime(template: string): org.mwg.task.Task;
                    fromIndex(indexName: string, query: string): org.mwg.task.Task;
                    fromIndexAll(indexName: string): org.mwg.task.Task;
                    indexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                    indexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                    localIndex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
                    unindexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                    unindexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
                    localUnindex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
                    indexesNames(): org.mwg.task.Task;
                    selectWith(name: string, pattern: string): org.mwg.task.Task;
                    selectWithout(name: string, pattern: string): org.mwg.task.Task;
                    asGlobalVar(variableName: string): org.mwg.task.Task;
                    asVar(variableName: string): org.mwg.task.Task;
                    defineVar(variableName: string): org.mwg.task.Task;
                    addToGlobalVar(variableName: string): org.mwg.task.Task;
                    addToVar(variableName: string): org.mwg.task.Task;
                    fromVar(variableName: string): org.mwg.task.Task;
                    fromVarAt(variableName: string, index: number): org.mwg.task.Task;
                    select(filter: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task;
                    newSelect(filter: org.mwg.task.TaskFunctionSelectNew): org.mwg.task.Task;
                    selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task;
                    selectWhere(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    get(name: string): org.mwg.task.Task;
                    traverse(relationName: string): org.mwg.task.Task;
                    traverseOrKeep(relationName: string): org.mwg.task.Task;
                    traverseIndex(indexName: string, ...queryParams: string[]): org.mwg.task.Task;
                    traverseIndexAll(indexName: string): org.mwg.task.Task;
                    map(mapFunction: org.mwg.task.TaskFunctionMap<any, any>): org.mwg.task.Task;
                    group(groupFunction: org.mwg.task.TaskFunctionGroup): org.mwg.task.Task;
                    groupWhere(groupSubTask: org.mwg.task.Task): org.mwg.task.Task;
                    inject(inputValue: any): org.mwg.task.Task;
                    subTask(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    isolate(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    subTasks(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                    subTasksPar(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                    ifThen(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
                    ifThenElse(cond: org.mwg.task.TaskFunctionConditional, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                    whileDo(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
                    doWhile(then: org.mwg.task.Task, cond: org.mwg.task.TaskFunctionConditional): org.mwg.task.Task;
                    then(p_action: org.mwg.task.Action): org.mwg.task.Task;
                    foreach(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    flatmap(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    foreachPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    flatmapPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    save(): org.mwg.task.Task;
                    clear(): org.mwg.task.Task;
                    lookup(nodeId: string): org.mwg.task.Task;
                    lookupAll(nodeId: string): org.mwg.task.Task;
                    execute(graph: org.mwg.Graph, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    executeSync(graph: org.mwg.Graph): org.mwg.task.TaskResult<any>;
                    executeWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    prepareWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): org.mwg.task.TaskContext;
                    executeUsing(preparedContext: org.mwg.task.TaskContext): void;
                    executeFrom(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    executeFromUsing(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, contextInitializer: org.mwg.Callback<org.mwg.task.TaskContext>, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    action(name: string, flatParams: string): org.mwg.task.Task;
                    parse(flat: string): org.mwg.task.Task;
                    newNode(): org.mwg.task.Task;
                    newTypedNode(typeNode: string): org.mwg.task.Task;
                    setProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task;
                    forceProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task;
                    removeProperty(propertyName: string): org.mwg.task.Task;
                    add(relationName: string, variableNameToAdd: string): org.mwg.task.Task;
                    addTo(relationName: string, variableNameTarget: string): org.mwg.task.Task;
                    propertiesWithTypes(filter: number): org.mwg.task.Task;
                    properties(): org.mwg.task.Task;
                    remove(relationName: string, variableNameToRemove: string): org.mwg.task.Task;
                    jump(time: string): org.mwg.task.Task;
                    math(expression: string): org.mwg.task.Task;
                    split(splitPattern: string): org.mwg.task.Task;
                    loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                    loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                    print(name: string): org.mwg.task.Task;
                    println(name: string): org.mwg.task.Task;
                    hook(p_hookFactory: org.mwg.task.TaskHookFactory): org.mwg.task.Task;
                    emptyResult(): org.mwg.task.TaskResult<any>;
                    mathConditional(mathExpression: string): org.mwg.task.TaskFunctionConditional;
                    static fillDefault(registry: java.util.Map<string, org.mwg.task.TaskActionFactory>): void;
                }
                class CoreTaskContext implements org.mwg.task.TaskContext {
                    private _globalVariables;
                    private _parent;
                    private _graph;
                    _callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>;
                    private _localVariables;
                    private _nextVariables;
                    private _current;
                    _result: org.mwg.task.TaskResult<any>;
                    private _world;
                    private _time;
                    private _hook;
                    constructor(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, p_graph: org.mwg.Graph, p_hook: org.mwg.task.TaskHook, p_callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>);
                    graph(): org.mwg.Graph;
                    world(): number;
                    setWorld(p_world: number): void;
                    time(): number;
                    setTime(p_time: number): void;
                    variable(name: string): org.mwg.task.TaskResult<any>;
                    private internal_deep_resolve(name);
                    wrap(input: any): org.mwg.task.TaskResult<any>;
                    wrapClone(input: any): org.mwg.task.TaskResult<any>;
                    newResult(): org.mwg.task.TaskResult<any>;
                    declareVariable(name: string): void;
                    private lazyWrap(input);
                    defineVariable(name: string, initialResult: any): void;
                    defineVariableForSubTask(name: string, initialResult: any): void;
                    setGlobalVariable(name: string, value: any): void;
                    setVariable(name: string, value: any): void;
                    private internal_deep_resolve_map(name);
                    addToGlobalVariable(name: string, value: any): void;
                    addToVariable(name: string, value: any): void;
                    globalVariables(): java.util.Map<string, org.mwg.task.TaskResult<any>>;
                    nextVariables(): java.util.Map<string, org.mwg.task.TaskResult<any>>;
                    variables(): java.util.Map<string, org.mwg.task.TaskResult<any>>;
                    result(): org.mwg.task.TaskResult<any>;
                    resultAsNodes(): org.mwg.task.TaskResult<org.mwg.Node>;
                    resultAsStrings(): org.mwg.task.TaskResult<string>;
                    continueWith(nextResult: org.mwg.task.TaskResult<any>): void;
                    continueTask(): void;
                    execute(initialTaskAction: org.mwg.plugin.AbstractTaskAction): void;
                    template(input: string): string;
                    private extractMethodElements(methodCall);
                    hook(): org.mwg.task.TaskHook;
                    toString(): string;
                }
                class CoreTaskResult<A> implements org.mwg.task.TaskResult<A> {
                    private _backend;
                    private _capacity;
                    private _size;
                    asArray(): any[];
                    constructor(toWrap: any, protect: boolean);
                    iterator(): org.mwg.task.TaskResultIterator<any>;
                    get(index: number): A;
                    set(index: number, input: A): void;
                    allocate(index: number): void;
                    add(input: A): void;
                    clear(): void;
                    clone(): org.mwg.task.TaskResult<A>;
                    free(): void;
                    size(): number;
                    private extendTil(index);
                    toString(): string;
                    private toJson(withContent);
                }
                class CoreTaskResultIterator<A> implements org.mwg.task.TaskResultIterator<A> {
                    private _backend;
                    private _size;
                    private _current;
                    constructor(p_backend: any[]);
                    next(): A;
                    nextWithIndex(): org.mwg.utility.Tuple<number, A>;
                }
                class TaskHelper {
                    static flatNodes(toFLat: any, strict: boolean): org.mwg.Node[];
                    static parseInt(s: string): number;
                }
                module math {
                    class CoreMathExpressionEngine implements org.mwg.core.task.math.MathExpressionEngine {
                        static decimalSeparator: string;
                        static minusSign: string;
                        private _cacheAST;
                        constructor(expression: string);
                        static parse(p_expression: string): org.mwg.core.task.math.MathExpressionEngine;
                        static isNumber(st: string): boolean;
                        static isDigit(c: string): boolean;
                        static isLetter(c: string): boolean;
                        static isWhitespace(c: string): boolean;
                        private shuntingYard(expression);
                        eval(context: org.mwg.Node, taskContext: org.mwg.task.TaskContext, variables: java.util.Map<string, number>): number;
                        private buildAST(rpn);
                        private parseDouble(val);
                        private parseInt(val);
                    }
                    class MathConditional {
                        private _engine;
                        private _expression;
                        constructor(mathExpression: string);
                        conditional(): org.mwg.task.TaskFunctionConditional;
                        toString(): string;
                    }
                    class MathDoubleToken implements org.mwg.core.task.math.MathToken {
                        private _content;
                        constructor(_content: number);
                        type(): number;
                        content(): number;
                    }
                    class MathEntities {
                        private static INSTANCE;
                        operators: java.util.HashMap<string, org.mwg.core.task.math.MathOperation>;
                        functions: java.util.HashMap<string, org.mwg.core.task.math.MathFunction>;
                        static getINSTANCE(): org.mwg.core.task.math.MathEntities;
                        constructor();
                    }
                    interface MathExpressionEngine {
                        eval(context: org.mwg.Node, taskContext: org.mwg.task.TaskContext, variables: java.util.Map<string, number>): number;
                    }
                    class MathExpressionTokenizer {
                        private pos;
                        private input;
                        private previousToken;
                        constructor(input: string);
                        hasNext(): boolean;
                        private peekNextChar();
                        next(): string;
                        getPos(): number;
                    }
                    class MathFreeToken implements org.mwg.core.task.math.MathToken {
                        private _content;
                        constructor(content: string);
                        content(): string;
                        type(): number;
                    }
                    class MathFunction implements org.mwg.core.task.math.MathToken {
                        private name;
                        private numParams;
                        constructor(name: string, numParams: number);
                        getName(): string;
                        getNumParams(): number;
                        eval(p: Float64Array): number;
                        private date_to_seconds(value);
                        private date_to_minutes(value);
                        private date_to_hours(value);
                        private date_to_days(value);
                        private date_to_months(value);
                        private date_to_year(value);
                        private date_to_dayofweek(value);
                        type(): number;
                    }
                    class MathOperation implements org.mwg.core.task.math.MathToken {
                        private oper;
                        private precedence;
                        private leftAssoc;
                        constructor(oper: string, precedence: number, leftAssoc: boolean);
                        getOper(): string;
                        getPrecedence(): number;
                        isLeftAssoc(): boolean;
                        eval(v1: number, v2: number): number;
                        type(): number;
                    }
                    interface MathToken {
                        type(): number;
                    }
                }
            }
            module utility {
                class CoreDeferCounter implements org.mwg.DeferCounter {
                    private _nb_down;
                    private _counter;
                    private _end;
                    constructor(nb: number);
                    count(): void;
                    getCount(): number;
                    then(p_callback: org.mwg.plugin.Job): void;
                    wrap(): org.mwg.Callback<any>;
                }
                class CoreDeferCounterSync implements org.mwg.DeferCounterSync {
                    private _nb_down;
                    private _counter;
                    private _end;
                    private _result;
                    constructor(nb: number);
                    count(): void;
                    getCount(): number;
                    then(p_callback: org.mwg.plugin.Job): void;
                    wrap(): org.mwg.Callback<any>;
                    waitResult(): any;
                }
                class ReadOnlyStorage implements org.mwg.plugin.Storage {
                    private wrapped;
                    constructor(toWrap: org.mwg.plugin.Storage);
                    get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                    put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                    remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                    connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
                    disconnect(callback: org.mwg.Callback<boolean>): void;
                    lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                    unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                }
            }
        }
    }
}

# ManyWorldGraph (aka mwg) Storage Plugin: LevelDB backend

This **mwg plugin** aims at offering an efficient file based storage, embeddable directly in your JVM-based application.
It can be used on x86 and ARM architectures using native implementation of LevelDB or on any JVM-ready platforms using the Java port of LevelDB ([dain/leveldb](https://github.com/dain/leveldb)).

LevelDB offers a very efficient storage using LSM-Tree techniques that can handle billions of nodes on a single machine based storage.
This plugin does not rely on an external server and therefore is very easy to set-up.
This feature both boost the performance by avoiding network or Inter-Process communications and in addition simplify a lot the configuration because only require a target directory to persist model on disk.

## Last versions:

- 1.0 compatible with mwg API 1.x

## Changelog

- 1.0 use LevelDB JNI in version 1.8

## Dependency

Simply add the following dependency to your maven project:

```java
<dependency>
    <groupId>org.kevoree.mwg.plugins</groupId>
    <artifactId>leveldb</artifactId>
    <version>REPLACE_BY_LAST_VERSION</version>
</dependency>
```

## Usage

As any mwg plugin, the **LevelDBStorage** should be inserted during the build step of the graph.
Simply reference the full qualified name of the storage (should be an existing directory).
Relative storage work as well and will be based on the current execution path.

```java
import org.mwg.LevelDBStorage;

GraphBuilder
    .builder()
    .withStorage(new LevelDBStorage("data"))
    .build();
```

## Configuration

In addition to the storage path, this plugin can be configure to use or not a native implementation.
By default the native x86 or ARM LevelDB will be use.
To configure a pure Java solution, please use the following code snippet.

```
new LevelDBStorage("/path/data").useNative(false)
```
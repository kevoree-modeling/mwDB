# ManyWorldGraph (aka mwg) Storage Plugin: RocksDB backend

This plugin aims at offering an efficient file based storage, embeddable directly in your JVM-based application.
This backend is also available for Android usages.
RocksDB offers a very efficient storage that can handle billions of mwg nodes on a single machine based storage.
This plugin does not rely on an external server.
This feature both boost the performance by avoiding network or Inter-Processus communications and in addition simplify a lot the configuration because only require a target directory to persist graph on disk.

## Last versions:

- 4.27.0 compatible with KMF framework 4.27.x

## Changelog

- 4.27.0 use RocksDB JNI in version 4.0

## Dependency

Simply add the following dependency to your maven project:

``` xml
<dependency>
    <groupId>org.kevoree.mwg.plugins</groupId>
    <artifactId>rocksdb</artifactId>
    <version>REPLACE_BY_LAST_VERSION</version>
</dependency>
```

## Usage

As any mwg plugin, the **LevelDBStorage** should be inserted during the build step of the graph.
Simply reference the full qualified name of the storage (should be an existing directory).
Relative storage work as well and will be based on the current execution path.

```java
import org.mwg.RocksDBStorage;

GraphBuilder
    .builder()
    .withStorage(new RocksDBStorage("data"))
    .build();
```

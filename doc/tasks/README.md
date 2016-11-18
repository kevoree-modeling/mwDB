# MWG Task API

> Why a (task/traversal/promise) API ?

Manipulating the Many World Graph through an asynchronous API allows to write blazing-fast code without compromising on high-level abstractions. Asynchronous code often leads to what is known as [CallBack Hell](http://callbackhell.com/), that damage both the readability of code and its reusability, due to nested callbacks. To overcome this, modern libraries and APIs provide Promises and/or Futures. The common goal of these approaches is to offer a way to chain reusable elements and to synchronize the program flow in order to avoid repeated and error-prone code. MWG comes with a powerful API to manipulate and traverse graphs, which hides low-level, asynchronous task primitives behind an expressive API. Graph processing can be resource-hungry. To make the most out of the available main memory, MWG provides an API to explicitly free the memory of a node. A `.free()` method can be called once a node is not used anymore.

# Running example

Let's consider the following simple graph as a running example:

> A graph composed by 2 rooms, themselves composed by 3 sensors associated through the relation: __sensors__ . All rooms should be indexed by their name, through the global index: __rooms__.

This graph can be built by the following task, build using the fluent Java DSL.

``` java
loop("1","2",
	newNode()
	.setProperty("name", Type.STRING, "room_{{i}}")
	.indexNode("rooms", "name")
	.asVar("parentRoom")
	.loop("1","3",
		newNode()
		.setProperty("sensor", Type.STRING, "sensor_{{i}}")
		.addTo("sensors", "parentRoom")
	)
).execute(g, null);
```

Without black magic, actions are just chained by `.` as any fluent DSL an initial action can be transparently built using a global static import.

```java
import static org.mwg.core.task.Actions.*;
```

The result graph can be represented as follow:

![Running Example View](running_example.png)

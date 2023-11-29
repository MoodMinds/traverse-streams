# Synchronous Traversal specification

## Motivation

The objective is to establish a traversal abstraction capable of executing potentially parallel or sequential streaming
operations, akin to the Java Streams API. This initiative seeks to overcome the limitations and inconveniences inherent
in the Streams API, such as the need for manual Stream closure, the inability to reuse Stream definitions, the integration
of parallel flattening, and the implementation of "if-then-elseif-...else"-style conditional streaming and exception handling.
Unlike the Streams API, **Traverse Streams** permits definition of the elements traversal with functions that may throw
checked exceptions. Numerous other restrictions present in the Streams API will also be addressed.

Additionally, there is a possibility to propagate a key-value **Context** associated with a particular traversal.

It is important to note that **Traverse Streams** is not positioned as a rival to the Java Streams API, especially in terms of its
performance in streaming of primitives. While **Traverse Streams** refrains from introducing its own stream building and transformation
methods, it offers a well-defined external API interfaces which facilitate the attainment of equivalent results through the combination
of API instances.

One of the key motivations behind the introduction of **Traverse Streams** is its capability to envelop stream instances with interceptors,
such as a transactional demarcating interceptor.

## The Abstraction

The structure of the **Traverse Streams** specification is as follows:

* `TraverseSupport`: this component acts as a Source of items that can be traversed either sequentially or in parallel.
  It provides methods like `sequence`, `traverse` and `parallel`, enabling the utilization of a `Traverser` to process
  each segment of the Source within the specified traverse function. Additionally, it allows for the transmission of a
  key-value Context alongside a specific traverse function. The traversal completion is marked by a boolean flag returning,
  indicating whether the Source was traversed entirely. The implementation can throw a `TraverseSupportException`
  if traversal is unsupported.
* `TraverseSupport.Traverser`: this component offers methods such as `next`, `some` and `each` to facilitate the traversal
  of elements. It allows traversing elements individually, in portions, or in bulk sequentially using the specified consumer
  functions that may throw checked exceptions.
* `TraverseSupportException`: an exception indicating that synchronous traversal inherently is not supported.

## Getting Started

Include **Traverse Streams** in your project by adding the dependency.

## Maven configuration

Artifacts can be found on [Maven Central](https://search.maven.org/) after publication.

```xml
<dependency>
    <groupId>org.moodminds.traverse</groupId>
    <artifactId>traverse-streams</artifactId>
    <version>${version}</version>
</dependency>
```

## Building from Source

You may need to build from source to use **Traverse Streams** (until it is in Maven Central) with Maven and JDK 1.8 at least.

## License
This project is going to be released under version 2.0 of the [Apache License][l].

[l]: https://www.apache.org/licenses/LICENSE-2.0
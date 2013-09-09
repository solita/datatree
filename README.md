
DataTree
========

Simple way to generate XML documents and XML Schemas using Java.

Requires Java 7 or greater.

This project is available in Maven Central using the following dependency:

```
<dependency>
    <groupId>fi.solita.datatree</groupId>
    <artifactId>datatree</artifactId>
    <version>0.1.0</version>
</dependency>
```


Why yet another XML library?
----------------------------

We had to create lots of REST APIs for external consumption, but our
customer did not allow us to use Clojure and JSON, but instead required us
to use Java and XML, even producing XML Schemas. We tried to use JAXB for
about two days. To protect our sanity and to save time, we created this
library to have a succint syntax for creating XML documents and generating
the XML Schemas without duplicating the Bean Validation annotations that
our model objects already had.


Version History
---------------

### Upcoming Changes

- A tree node may now contain both text and meta data
- `tree()` accepts arrays and collections of its arguments and will flatten
them. This is useful when splitting tree construction into helper methods
- Renamed `Tree.content()` to `Tree.text()`

### DataTree 0.1.0 (2013-09-08)

- Can create a tree using succinct Java syntax
- Can convert the tree into an XML document

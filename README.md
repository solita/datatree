
DataTree
========

Simple way to generate XML documents and XML Schemas using Java, but *feeling
like Clojure.*

Requires Java 7 or greater.


User Guide
----------

### Getting Started

Get this library from Maven Central using the following dependency:

```
<dependency>
    <groupId>fi.solita.datatree</groupId>
    <artifactId>datatree</artifactId>
    <version>0.5.0</version>
</dependency>
```

Add static imports for the factory methods in the [`Tree`][Tree] class:

```
import static fi.solita.datatree.Tree.*;
```

Now you can describe an XML document as a series of nested calls to `tree` and
`meta`. Here are some examples:

`tree("element", "some text")` becomes `<element>some text</element>`

`tree("element", meta("attribute", "value"))` becomes `<element attribute="value"/>`

`tree("outer", tree("inner-1"), tree("inner-2"))` becomes `<outer><inner-1/><inner-2/></outer>`

To convert a tree into XML, you can use one of the various methods in
[`XmlDocumentGenerator`][XmlDocumentGenerator]:

```
Tree tree = tree("some-tree");
InputStream in = XmlDocumentGenerator.toInputStream(tree);
```

[Tree]: https://github.com/solita/datatree/blob/master/src/main/java/fi/solita/datatree/Tree.java
[XmlDocumentGenerator]: https://github.com/solita/datatree/blob/master/src/main/java/fi/solita/datatree/xml/XmlDocumentGenerator.java


### Creating XML Schemas

There are helper methods in [`XmlSchema`][XmlSchema] for generating XML
Schemas. First do a static import for them:

```
import static fi.solita.datatree.xml.XmlSchema.*;
```

And then use those to generate a tree that represents the schema:

```
Tree schema = schema(element("foo"));
Tree document = tree("foo");
XmlSchemaValidator.validate(schema, document);
```

There are not yet helper methods for every XML Schema element and attribute.
Maybe later. Create a pull request if you want to add something there.

[XmlSchema]: https://github.com/solita/datatree/blob/master/src/main/java/fi/solita/datatree/xml/XmlSchema.java


Why yet another XML library?
----------------------------

We had to create lots of REST APIs for external consumption, but our customer
did not allow us to use Clojure and JSON, but instead required us to use Java
and XML, even producing XML Schemas. We tried to use JAXB for about two days.
To protect our sanity and to save time, we created this library to have a
succinct syntax for creating XML documents and XML Schemas.


Version History
---------------

### Upcoming Changes

- Removed `XmlDocumentGenerator.toXml()`
- Added `XmlDocumentGenerator.toInputStream()`
- Added `XmlDocumentGenerator.toString()` and `toPrettyString()`
- Added `XmlDocumentGenerator.toNamespaceAwareDocument()`
- `XmlDocumentGenerator.toDocument()` won't anymore produce empty text nodes

### DataTree 0.5.0 (2013-09-10)

- `tree()` will flatten also `Iterable` arguments
- In `Tree.toString()`, show meta before text

### DataTree 0.4.0 (2013-09-10)

- `null` in a tree's content does not anymore cause `NullPointerException`,
but will be ignored silently. We consider `null` to be equivalent to an empty
list, the same way as Clojure's `nil`
- Will throw an `IllegalArgumentException` if trying to put something other
than String, Tree or Meta instances into a tree

### DataTree 0.3.0 (2013-09-10)

- `XmlSchema` class with helper methods for generating XSD files

### DataTree 0.2.0 (2013-09-09)

- A tree node may now contain both text and meta data
- `tree()` accepts arrays and collections of its arguments and will flatten
them. This is useful when splitting tree construction into helper methods
- Renamed `Tree.content()` to `Tree.text()`
- Made the `Meta` class' constructor package-private to encourage using the
`Tree.meta()` factory method

### DataTree 0.1.0 (2013-09-08)

- Can create a tree using succinct Java syntax
- Can convert the tree into an XML document

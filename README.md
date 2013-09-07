
DataTree
========

Simple way to generate XML documents and XML Schemas using Java.


Why yet another XML library?
----------------------------

We had to create lots of REST APIs for external consumption, but our
customer did not allow us to use Clojure and JSON, but instead required us
to use Java and XML, even producing XML Schemas. We tried to use JAXB for
about two days. To protect our sanity and to save time, we created this
library to have a succint syntax for creating XML documents and generating
the XML Schemas without duplicating the Bean Validation annotations that
our model objects already had.
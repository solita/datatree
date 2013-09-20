#!/bin/bash
set -eux

cd src/main/resources
cd fi/solita/datatree/xml
rm -f *.xsd *.dtd

wget http://www.w3.org/2001/xml.xsd
wget http://www.w3.org/2001/XMLSchema.xsd
wget http://www.w3.org/2001/XMLSchema.dtd
wget http://www.w3.org/2001/datatypes.dtd

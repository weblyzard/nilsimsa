## Nilsimsa for Java

Nilsimsa is a hashing function which belongs to the family of locality sensitive hashes (LSH). This library provides functions to compute and compare the Nilsimsa string similarity hash for a given string.
 
The code is a port of the Python Nilsimsa implementation by Michael Itz to the Java language:
  http://code.google.com/p/py-nilsimsa.
 
Original C nilsimsa-0.2.4 implementation by cmeclax:
 http://ixazon.dynip.com/~cmeclax/nilsimsa.html


## Requirements

* Java 8 or higher

## Changelog

* 0.0.2: 
  - added support for hashing byte arrays and static constructors
  - cache hash digest
  - implemented `equals` and `hashCode`
  - `compare` now uses the much faster Integer.bitcount method
  - added `bitwiseDifference` which yields the number of bits that differ between hashes
  - improved test coverage
  - code cleanup

* 0.0.3:
  - reduced the number of necessary dependencies (commons-io and commons-codec)
  - improved javadoc
  - publish library on maven central

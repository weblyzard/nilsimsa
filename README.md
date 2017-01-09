## Nilsimsa for Java

Nilsimsa is a hashing function which belongs to the family of locality sensitive hashes (LSH). This library provides functions to compute and compare the Nilsimsa string similarity hash for a given string.
 
The code is a port of the Python Nilsimsa implementation by Michael Itz to the Java language:
  http://code.google.com/p/py-nilsimsa.
 
Original C nilsimsa-0.2.4 implementation by cmeclax:
 http://ixazon.dynip.com/~cmeclax/nilsimsa.html


## Requirements

* Java 7 or higher
* Apache Commons libraries from http://commons.apache.org/

## Changelog

* 0.0.2: 
  - added support for hashing byte arrays and static constructors
  - `compare` now uses the quicker Integer.bitcount method
  - `compare` yields the number of bits that differ rather than the difference to 128 equal bits.
  - improved test coverage
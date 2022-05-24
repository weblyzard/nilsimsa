## Nilsimsa for Java
[![Build Status](https://github.com/weblyzard/nilsimsa/actions/workflows/build.yml/badge.svg)](https://github.com/weblyzard/nilsimsa/actions/workflows/build.yml)


Nilsimsa is a hashing function which belongs to the family of locality sensitive hashes (LSH). This library provides functions to compute and compare the Nilsimsa string similarity hash for a given string.
 
The code is a port of the Python Nilsimsa implementation by Michael Itz to the Java language:
  http://code.google.com/p/py-nilsimsa.
 
Original C nilsimsa-0.2.4 implementation by cmeclax:
  http://ixazon.dynip.com/~cmeclax/nilsimsa.html

## Requirements

* Java 8+

## Maven Dependency

```xml
 <dependency>
     <groupId>com.weblyzard.lib.string</groupId>
     <artifactId>nilsimsa</artifactId>
     <version>0.0.5</version>
 </dependency>
```

## Examples

### Compute and output the Nilsimsa hash

```java
String text = "A short test message"; 
Nilsimsa n = Nilsimsa.getHash(text);
System.out.println("Nilsimsa hash for message '" + text + "': " + n.hexdigest());
```
### Text similarity

```java
Nilsimsa first = Nilsimsa.getHash("A short test message");
Nilsimsa second = Nilsimsa.getHash("A short test message!");
Nilsimsa third = Nilsimsa.getHash("Something completely different");

System.out.println(first.bitwiseDifference(first));    // 0
System.out.println(first.bitwiseDifference(second));   // 3
System.out.println(first.bitwiseDifference(third));    // 133
```

### Determine whether two strings are significantly different

```java
List<String> testList = Arrays.asList("A short test message", 
                                      "A short test message!", 
                                      "Something completely different");

for (String firstString: testList) {
    for (String secondString: testList) {
        Nilsimsa firstHash = Nilsimsa.getHash(firstString);
        Nilsimsa secondHash = Nilsimsa.getHash(secondString);

        System.out.println("The hash value of text '" + firstString + "' and '" 
            + secondString + "' differ in " + firstHash.bitwiseDifference(secondHash) + " bits.");
    }
}
```

## Changelog
Please refer to the [releases](https://github.com/weblyzard/nilsimsa/releases) page.


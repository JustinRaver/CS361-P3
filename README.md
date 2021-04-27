# Project 3: Regular Expressions

* Author: Justin Raver, Nick Stolarow
* Class: CS361 Section 1
* Semester: Spring 2021

## Overview

This Java applications builds an NFA from a REGEX passed by the user. It
then converts the NFA to an equivalent DFA and tests strings outputting "yes"
if the string is accepted and "no" if the string is rejected by the DFA.

## Compiling and Using

```
 
 Build: javac -cp ".:./CS361FA.jar" re/REDriver.java
 
 Run: java -cp ".:./CS361FA.jar" re.REDriver ./tests/<test_name>
 
```

## Discussion

Implementing the recursive descent parser in this program was challenging, but
with the help of the provided article we were able to quickly get that portion working. 
The article gave us a great template to implement this design and ideas of how to implement 
other portions of the project.

The intermediate methods of this project were definitely the hardest part. We struggled
in combination, repetition, and sequence to understand how to properly connect the 
NFAs to build a single complete NFA. The realization that we could connect multiple NFAS
using empty transitions helped a lot.

We utilized the NFA toString methods along with others to debug the program and over
time we were able to work out the bugs in the program. Once we had the initial tests
passing we developed several more and were able to discover edge cases that broke the 
implementation. Using the same debugging tactics were found and fixed those issues.

## Sources used

Starter files and Lib provided on piazza
http://matt.might.net/articles/parsing-regex-with-recursive-descent/
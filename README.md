# ALFA-D
A Programming Language built with ANTLR4

![](https://img.shields.io/badge/Academical%20Project-Yes-green)
![](https://img.shields.io/badge/Made%20with-ANTLR4-blue)
![](https://img.shields.io/badge/License-Free%20To%20Use-green)
![](https://img.shields.io/badge/Maintained-No-red)

## Description

ALFA-D aims at providing an easy way of dinamically manipulating information tables and of accessing/storing data from/to external .csv files.
Its implementation is done in a way that allows the programmer to develop table interaction scripts with primitive variables, while also allowing data prints on the command line or onto files.

As a very simplified language, it is table oriented and follows some characteristics related to SQL programming (table operations wise) and Java (sintax wise).
Practically speaking, this work envolves 2 languages:
- One for the compiler, that validates table manipulation programs - this is the main language, where compilation to Java code occurs.
- One to read and validate structured information stored in .csv files - this envolves an interpreter.

## Repository Structure

/docs - contains the final report on the work developed

/example-code - contains a set of programs that demonstrate the use of ALFA-D

/src - contains the source code for the compiler and interpreter

## Data Types 

Primitives:
- bool - boolean bit
- int - 32 bit integer
- real - 64 bit float

Non-Primitives:
- String - character sequence between " " 
- Table - data structure of a given primitive type for each column; contains a header line 
- Column - particular case of a Table variable, with limited operations, 1 column and N lines
- Line - particular case of a Table variable, with limited operations, N columns and 1 line

## Sintax

ALFA-D supports single and multi-line comments, variable declaration, definition and instantiation, arithmetic operations, conditional statements, for and while loops and function definition.

Table operations include: create, load, create column, create line, union, intersect, difference, join and join on, remove where, increase, decrease, add, clear and save.

## Authors

The authors of this repository are André Pedrosa, Duarte Castanho, Filipe Pires, João Alegria and Lucas Silva, and the project was developed for the Compilers Course of the licenciate's degree in Informatics Engineering of the University of Aveiro.

For further information, please read our [report](https://github.com/FilipePires98/ALFA-D/blob/main/docs/Relat%C3%B3rio.pdf) or contact us at filipesnetopires@ua.pt.




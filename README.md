#  Constraint Satisfaction Problem Solver
## CS452

## Author(s):

Lucas Rappette

## Date:

3/16/18


## Description:

A program that implements a backtracking algorithm over a crossword puzzle.
Given a dictionary and a set of constraints, the program will fill the crossword 
with words that satisfy the crossword puzzle. 
This program was assigned in the AI course. 
A more in depth description is attached, it is the original assignment description. 

This file is called _README2.pdf_.


## How to build the software

Add this project to any Java IDE, it will automatically compile.
If this does not work execute the command below on the command line to build the project.

```
javac -d bin -sourcepath src src/*
```


## How to use the software

Execute the command below on a command line in the directory, or run from the 
IDE with runtime arguments.

```
java -cp bin; Solve x y
```

_Valid Arguments:_

- The first arg is required, __x__ is the crossword puzzle file with extension included.
- The second arg is required, __y__ is the dictionary file with extension included.
- File formats are described in _README2.pdf_.


## How the software was tested

Testing was completed by using outlier-like input arguments in order to stress
test constraint-satisfaction/backtracking capabilities.


## Known bugs and problem areas

This Crossword Solver using a CSP algorithm is not flawless, but I thought I'd 
share it anyways for others to try build a model from if they so desire. 
The actual CSP algorithm implemented is sound but I still have errors in 
regards to enforcing a constraint without shutting it out of the entire domain
 of values when there are many constraints on one space.
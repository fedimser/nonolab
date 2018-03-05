# Nonolab is set of tools for processing nonograms.

## Features:

* Solving nonograms (hints->pixels).
* Creating nonogram (pixels->hints).
* Generating pictures for nonograms (solved and not solved).
* Checking whether nonogram is correct (solvable, uniquily solvable), countresamples for case when solution is not unique.
* Support for .non format ([description](https://github.com/mikix/nonogram-db/blob/master/FORMAT.md)).

## How to solve nonograms

Create file abc.non

	width 5
	height 10

	rows
	2
	2,1
	1,1
	3
	1,1
	1,1
	2
	1,1
	1,2
	2

	columns
	2,1
	2,1,3
	7
	1,3
	2,1
	
Run `nonolab` and enter command

	solve abc.non
	
## How to create nonograms

Create file abc.txt

		 XX  
	 XX X
	  X X
	 XXX 
	X X  
	X X  
	  XX 
	 X X 
	 X XX
	XX 
	
Run `nonolab` and enter command

	create abc.txt


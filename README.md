# External Sequential Sorter
This is a simple utility class that aims to sort big Java object files that would otherwise not fit in memory.

## Usage Guide
The public static method "sort" should be used. It has 4 parameters:
1. The file / path to the file to be sorted
2. The name of the output sorted file
3. The maximum number of objects we allow to be read in memory
4. A comparator

A good maxObjReadInMemory would be equal to ceil(sqrt(number_of_objects_in_input_file)) + 1

## How it works
The program will try to split the original file into n small files that can fit in memory. Then, it will sort the contents of the files (according to a given comparator) using Shell Sort. Afterwards, it will merge the n sorted files into a single one using a Priority Queue.

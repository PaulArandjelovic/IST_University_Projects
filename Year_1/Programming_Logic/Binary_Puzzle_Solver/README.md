# Binary_Puzzle_Solver

This project was part of a Programming Logic course at Instituto Superior Técnico (2018-2019) programmed in Prolog. This program when given a binary puzzle, solves and returns it.

You can use SWI-Prolog as an environment to run this code. See public_tests/puzzles_publicos.pl to see sample puzzles that can be introduced into the program to be solved.

## Input

Puzzle = </br>
\[\[\_, \_, 0, \_, \_, 1],</br>
\[\_, \_, \_, \_, \_, 1],</br>
\[\_, \_, \_, \_, \_, \_],</br>
\[\_, \_, \_, \_, 0, 0],</br>
\[\_, 1, \_, \_, \_, \_],</br>
\[0, \_, 0, \_, \_, \_]],</br>
resolve(Puzzle, Solucao), escreve_Puzzle(Solucao).</br>

## Output

1 0 0 1 0 1 </br>
0 0 1 0 1 1 </br>
1 1 0 0 1 0 </br>
1 0 1 1 0 0 </br>
0 1 1 0 0 1 </br>
0 1 0 1 1 0 </br>
Puzzle = [[_G20, _G23, 0, _G29, _G32, 1], ..., </br>
Solucao = [[1, 0, 0, 1, 0, 1], [0, 0, 1, 0, 1, 1], ..., </br>
[0, 1, 1, 0|...], [0, 1, 0|...]] . </br>

# Assignment

Need to develop a tic tac toe application
that can play a 3*3 crosses and zeros game with another application
running on a different port or node.

The instances of the application choose who will play crosses
and who will play zeros, then take turns making moves,
reporting their moves to the other instance.
Eventually a winner is determined or a tie is declared
and the game is stopped.

## Requirements

1) The application must be developed in the JVM language.
2) Any technology of the developer's choice can be used.
3) The application should be developed with best practices for writing code: formatting, testing, comments as needed.
4) The connection between instances can be broken and re-established at any time.
5) The application must have an interface (REST or HTML) that allows the user to retrieve the state of the playing field at any time.
6) At any time, both instances must show the same state of the playing field or explicitly indicate that the state is inconsistent, regardless of the state of the connection between the instances.
7) A delay must be provided so that instances' moves can be tracked.
8) The application must make moves according to the rules.
9) The application must not allow another instance to make moves not according to the rules.
10) You can choose any other turn-based game with 2 or more players: naval combat, chess, sticks (each player takes 1,2,3 sticks from the pile, whoever takes the last one loses).
11) The algorithm of the game is not important, you can use random strategy. What is important is how synchronization between instances takes place.

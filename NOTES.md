# Protocol

1. establish connection with a peer
2. check rules version they have. destroy connection if different
3. init new game session. exchange with session ID
4. peek up a role: "crosses", "zero". "crosses" made move first
5. write down the field state
6. send round state to peer: the field state, move position
7. first move always by cross
8. wait acceptance from peer. if declined by peer, log and interrupt the session 



## Nice to have
4. peek up who starts first by: 3 rounds of "stone scissors paper"

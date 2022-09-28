## Winner

When someone loose the `GameOver` view shows a “game over” which is weird because one of them won and also the counting of the points is somehow wrong because it does not take into account any crash or winning or loosing.

The original goal of the **Tron** game is to trap the other snake to crash on yourself or a wall.

To reflect that behaviour as a success we could, for example :

- On the game over view write “Snake/Ekans is the winner” instead of “game over”
- Put the looser points to 0 - so the only way to keep your accumulated points would be to win.

Try implement that !

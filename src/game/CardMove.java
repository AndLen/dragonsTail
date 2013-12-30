package game;

/**
 * Created by Andrew on 29/12/13.
 */
public class CardMove {

    private final int boardIndexFrom;
    private final int toMoveTop;
    private int boardIndexTo = -1;
    private boolean topRow;

    public CardMove(int toMoveTop, int boardIndexFrom) {
        this.toMoveTop = toMoveTop;
        this.boardIndexFrom = boardIndexFrom;
    }

    public void cardReleased(int boardIndexTo, boolean topRow) {
        this.boardIndexTo = boardIndexTo;
        this.topRow = topRow;
    }

    public String makeMove(CardGame game) {
        if (boardIndexTo != -1) {
            if (topRow) {
                return game.moveCardOntoTopRow(toMoveTop,boardIndexFrom,boardIndexTo);

            } else {
                return game.moveCardOntoCard(toMoveTop, boardIndexFrom, boardIndexTo);
            }
        }
        return "";
    }

    public int getBoardIndexFrom() {
        return boardIndexFrom;
    }

    public int getToMoveTop() {
        return toMoveTop;
    }

    public String toString() {
        return "FROM:" + boardIndexFrom + " TOP CARD INDEX: " + toMoveTop + (boardIndexTo == -1 ? "" : " TO " + boardIndexTo);
    }
}

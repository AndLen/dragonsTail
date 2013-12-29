package game;

/**
 * Created by Andrew on 29/12/13.
 */
public class CardMove {

    private final int boardIndexFrom;
    private final int toMoveTop;
    private int boardIndexTo = -1;

    public CardMove(int toMoveTop, int boardIndexFrom) {
        this.toMoveTop = toMoveTop;
        this.boardIndexFrom = boardIndexFrom;
    }

    public void cardReleased(int boardIndexTo) {
        this.boardIndexTo = boardIndexTo;
    }

    public String makeMove(CardGame game) {
        if (boardIndexTo != -1) {
            return game.moveCardOntoCard(toMoveTop, boardIndexFrom, boardIndexTo);
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

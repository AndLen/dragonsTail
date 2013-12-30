package game;

/**
 * Created by Andrew on 29/12/13.
 */
public class CardMove {

    private final int boardIndexFrom;
    private final int toMoveTop;
    private final MOVE_TYPE moveType;
    private int boardIndexTo = -1;
    private boolean topRow;

    public CardMove(int toMoveTop, int boardIndexFrom) {
        this.toMoveTop = toMoveTop;
        this.boardIndexFrom = boardIndexFrom;
        moveType = MOVE_TYPE.FROM_BOARD;
    }

    public CardMove() {
        //deck move
        toMoveTop = -1;
        boardIndexFrom = -1;
        moveType = MOVE_TYPE.FROM_DECK;
    }

    public void cardReleased(int boardIndexTo, boolean topRow) {
        this.boardIndexTo = boardIndexTo;
        this.topRow = topRow;
    }

    public String makeMove(CardGame game) {
        switch (moveType) {
            case FROM_DECK:
                return makeDeckMove(game);
            case FROM_BOARD:
                return makeBoardMove(game);
            case FROM_PILE:
                return makePileMove(game);
            default:
                return "";
        }

    }

    private String makePileMove(CardGame game) {
        return "";
    }

    private String makeDeckMove(CardGame game) {
        return "";
    }

    private String makeBoardMove(CardGame game) {
        if (boardIndexTo != -1) {
            if (topRow) {
                return game.moveCardOntoTopRow(toMoveTop, boardIndexFrom, boardIndexTo);

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

    public MOVE_TYPE getMoveType() {
        return moveType;
    }

    public static enum MOVE_TYPE {FROM_DECK, FROM_BOARD, FROM_PILE}
}

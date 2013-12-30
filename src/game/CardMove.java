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

    public CardMove(int boardIndexFrom) {
        toMoveTop = 1;
        this.boardIndexFrom = boardIndexFrom;
        moveType = MOVE_TYPE.FROM_PILE;
    }

    public void cardReleased(int boardIndexTo, boolean topRow) {
        this.boardIndexTo = boardIndexTo;
        this.topRow = topRow;
    }

    public String makeMove(CardGame game) {
        if (boardIndexTo != -1) {
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
        return "";
    }

    private String makePileMove(CardGame game) {
        if (topRow) {
            return game.moveCardOntoTopRowFromRow(boardIndexFrom, boardIndexTo);
        } else {
            return game.moveCardOntoCardFromTopRow(boardIndexFrom, boardIndexTo);
        }
    }

    private String makeDeckMove(CardGame game) {
        if (topRow) {
            return game.moveCardOntoTopRowFromDeck(boardIndexTo);
        } else {
            return game.moveCardOntoCardFromDeck(boardIndexTo);
        }
    }

    private String makeBoardMove(CardGame game) {
        if (topRow) {
            return game.moveCardOntoTopRowFromBoard(toMoveTop, boardIndexFrom, boardIndexTo);
        } else {
            return game.moveCardOntoCardFromBoard(toMoveTop, boardIndexFrom, boardIndexTo);
        }
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

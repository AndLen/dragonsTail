package game;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrew on 29/12/13.
 */
public class CardMove {

    private final int boardIndexFrom;
    private final int toMoveTop;
    private final MOVE_TYPE moveType;
    private int boardIndexTo = -1;
    private boolean topRow;

    public CardMove(int toMoveTop, int boardIndexFrom, CardGame game) {
        this.toMoveTop = toMoveTop;
        this.boardIndexFrom = boardIndexFrom;
        moveType = MOVE_TYPE.FROM_BOARD;
        List<Card> toHide = game.getBoard().get(boardIndexFrom);
        for (int i = toMoveTop; i < toHide.size(); i++) {
            toHide.get(i).setHidden(true);
        }
    }

    public CardMove(CardGame game) {
        //deck move
        toMoveTop = -1;
        boardIndexFrom = -1;
        moveType = MOVE_TYPE.FROM_DECK;
        game.getDeck().peek().setHidden(true);
    }

    public CardMove(int boardIndexFrom, CardGame game) {
        toMoveTop = 1;
        this.boardIndexFrom = boardIndexFrom;
        moveType = MOVE_TYPE.FROM_PILE;
        List<Card> pile = game.getTopRow().get(boardIndexFrom);
        pile.get(pile.size() - 1).setHidden(true);
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
            }
        }
        return "ERROR";
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

    public void unhideCards(CardGame game) {
        //Not efficient, but easy
        if (!game.getDeck().isEmpty()) {
            game.getDeck().peek().setHidden(false);
        }
        for (List<Card> cardList : game.getBoard()) {
            for (Card card : cardList) {
                card.setHidden(false);
            }
        }
        for (List<Card> cardList : game.getTopRow()) {
            for (Card card : cardList) {
                card.setHidden(false);
            }
        }


    }

    public void undo(CardGame game) {

        switch (moveType) {
            case FROM_DECK:
                //This sucks a bit
                LinkedList<Card> queueList = (LinkedList<Card>) game.getDeck();
                if (topRow) {
                    List<Card> pile = game.getTopRow().get(boardIndexTo);
                    queueList.add(0, pile.remove(pile.size() - 1));
                } else {
                    List<Card> col = game.getBoard().get(boardIndexTo);
                    queueList.add(0, col.remove(col.size() - 1));
                }
                break;
            case FROM_BOARD:
                List<Card> boardCol = game.getBoard().get(boardIndexFrom);
                if (topRow) {
                    List<Card> pile = game.getTopRow().get(boardIndexTo);
                    boardCol.add(pile.remove(pile.size() - 1));
                } else {
                    //The fun one
                    List<Card> otherCol = game.getBoard().get(boardIndexTo);
                    int from = toMoveTop+1;
                    while (from < otherCol.size()) {
                        boardCol.add(otherCol.remove(from));
                    }
                }
                break;
            case FROM_PILE:
                List<Card> pile = game.getTopRow().get(boardIndexFrom);
                if (topRow) {
                    List<Card> otherPile = game.getTopRow().get(boardIndexTo);
                    pile.add(otherPile.remove(otherPile.size() - 1));
                } else {
                    List<Card> col = game.getBoard().get(boardIndexTo);
                    pile.add(col.remove(col.size() - 1));
                }
                break;
        }
    }

    public static enum MOVE_TYPE {FROM_DECK, FROM_BOARD, FROM_PILE}
}

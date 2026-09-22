package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    //HELPER FUNCTIONS

    private boolean isOnBoard(ChessPosition position) {
        if ((position.getRow() > 8) | (position.getRow() < 1)) {
            return false;
        }
        if ((position.getColumn() > 8) | (position.getColumn() < 1)) {
            return false;
        }
        return true;
    }

    private boolean isEmpty(ChessBoard board, ChessPosition position) {
        if (!isOnBoard(position)) {
            return false;
        }
        return (board.getPiece(position) == null);
    }

    private boolean isOpponent(ChessBoard board, ChessPosition position) {
        if (!isOnBoard(position) | isEmpty(board, position)) {
            return false;
        }
        ChessPiece other = board.getPiece(position);
        return color != other.getTeamColor();
    }

    private boolean isAlly(ChessBoard board, ChessPosition position) {
        if (!isOnBoard(position) | isEmpty(board, position)) {
            return false;
        }
        ChessPiece other = board.getPiece(position);
        return color == other.getTeamColor();
    }

    private void attemptMakeMove(ChessBoard board,
                                 ChessPosition start,
                                 ChessPosition end,
                                 Collection<ChessMove> output) {
        if (!isOnBoard(end)) {return;}
        if (isEmpty(board, end) | isOpponent(board, end)) {
            output.add(new ChessMove(start, end, null));
        }
    }

    private void recursiveMovement(ChessBoard board,
                                   ChessPosition start,
                                   ChessPosition position,
                                   int row,
                                   int col,
                                   Collection<ChessMove> output) {
        ChessPosition newPosition = position.getAdjacentPosition(row, col);
        if (recursiveMoveHelper(board, start, newPosition, output)) {
            recursiveMovement(board, start, newPosition, row, col, output);
        }
    }

    private boolean recursiveMoveHelper(ChessBoard board,
                                     ChessPosition start,
                                     ChessPosition newPosition,
                                     Collection<ChessMove> output) {
        //case STOP
        if (!isOnBoard(newPosition) | isAlly(board, newPosition)) {
            return false;
        }
        //case make move but go no further
        if (isOpponent(board, newPosition)) {
            output.add(new ChessMove(start, newPosition, null));
            return false;
        }
        //case add and continue onwards
        output.add(new ChessMove(start, newPosition, null));
        return true;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KING -> {return getKingMoves(board, myPosition);}
            case KNIGHT -> {return getKnightMoves(board, myPosition);}
            case ROOK -> {return getRookMoves(board, myPosition);}
            case BISHOP -> {return getBishopMoves(board, myPosition);}
            case QUEEN -> {return getQueenMoves(board, myPosition);}
            case PAWN -> {return getPawnMoves(board, myPosition);}
            case null, default -> {return null;}
        }
    }

    public Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> output = new HashSet<ChessMove>();
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(1,0), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(1,1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(0,1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(-1,1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(-1,0), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(-1,-1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(0,-1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(1,-1), output);
        return output;
    }

    public Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> output = new HashSet<ChessMove>();
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(2,1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(1,2), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(-1,2), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(-2,1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(-2,-1), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(-1,-2), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(1,-2), output);
        attemptMakeMove(board, myPosition, myPosition.getAdjacentPosition(2,-1), output);
        return output;
    }

    public Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> output = new HashSet<ChessMove>();
        recursiveMovement(board, myPosition, myPosition, 1, 0, output);
        recursiveMovement(board, myPosition, myPosition, -1, 0, output);
        recursiveMovement(board, myPosition, myPosition, 0, 1, output);
        recursiveMovement(board, myPosition, myPosition, 0, -1, output);
        return output;
    }

    public Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> output = new HashSet<ChessMove>();
        recursiveMovement(board, myPosition, myPosition, 1, 1, output);
        recursiveMovement(board, myPosition, myPosition, -1, 1, output);
        recursiveMovement(board, myPosition, myPosition, -1, -1, output);
        recursiveMovement(board, myPosition, myPosition, 1, -1, output);
        return output;
    }

    public Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> output = new HashSet<ChessMove>();
        recursiveMovement(board, myPosition, myPosition, 1, 0, output);
        recursiveMovement(board, myPosition, myPosition, -1, 0, output);
        recursiveMovement(board, myPosition, myPosition, 0, 1, output);
        recursiveMovement(board, myPosition, myPosition, 0, -1, output);
        recursiveMovement(board, myPosition, myPosition, 1, 1, output);
        recursiveMovement(board, myPosition, myPosition, -1, 1, output);
        recursiveMovement(board, myPosition, myPosition, -1, -1, output);
        recursiveMovement(board, myPosition, myPosition, 1, -1, output);
        return output;
    }

    public Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> output = new HashSet<ChessMove>();
        ChessPosition defaultMove;
        ChessPosition diagonal1;
        ChessPosition diagonal2;
        ChessPosition doubleMove;
        int promotionRow;
        int doubleRow;
        if (color == ChessGame.TeamColor.WHITE) {
            defaultMove = myPosition.getAdjacentPosition(1,0);
            diagonal1 = myPosition.getAdjacentPosition(1,1);
            diagonal2 = myPosition.getAdjacentPosition(1,-1);
            doubleMove = myPosition.getAdjacentPosition(2, 0);
            promotionRow = 6;
            doubleMove = 1;
        }
        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        String output;
        switch (type) {
            case KING -> output = "k";
            case QUEEN -> output = "q";
            case BISHOP -> output = "b";
            case ROOK -> output = "r";
            case KNIGHT -> output = "h";
            case PAWN -> output = "p";
            case null, default -> output = " ";
        }
        return output;
    }
}

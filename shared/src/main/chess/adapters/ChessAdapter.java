package chess.adapters;

import chess.Board;
import chess.Game;
import chess.Move;
import chess.Position;
import chess.interfaces.*;
import chess.pieces.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ChessAdapter {
    private static GsonBuilder newBuilder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPosition.class, (JsonDeserializer<ChessPosition>) (element, type, context) -> context.deserialize(element, Position.class));
        builder.registerTypeAdapter(ChessMove.class, (JsonDeserializer<ChessMove>) (element, type, context) -> context.deserialize(element, Move.class));
        builder.registerTypeAdapter(ChessPiece.class, (JsonDeserializer<ChessPiece>) (element, type, context) -> context.deserialize(element, Piece.class));
        builder.registerTypeAdapter(ChessBoard.class, (JsonDeserializer<ChessBoard>) (element, type, context) -> context.deserialize(element, Board.class));
        builder.registerTypeAdapter(ChessGame.class, (JsonDeserializer<ChessGame>) (element, type, context) -> context.deserialize(element, Game.class));
        return builder;
    }

    public static Gson getGson() {
        GsonBuilder builder = newBuilder();
        builder.registerTypeAdapter(Piece.class, getChessPieceTypeAdapter());
        return builder.create();
    }

    private static Gson getGsonWithoutPiece() {
        return newBuilder().create();
    }

    private static final TypeAdapter<Piece> chessPieceTypeAdapter = new TypeAdapter<>() {
        @Override
        public void write(JsonWriter writer, Piece chessPiece) throws IOException {
            if (chessPiece != null) {
                writer.beginObject();
                writer.name("color");
                writer.value(chessPiece.getTeamColor().toString());
                writer.name("type");
                writer.value(chessPiece.getPieceType().toString());
                writer.endObject();
            } else {
                writer.nullValue();
            }
        }

        @Override
        public Piece read(JsonReader jsonReader) {
            Gson gson = getGsonWithoutPiece();
            Piece chessPiece = gson.fromJson(jsonReader, Piece.class);
            if (chessPiece != null) {
                ChessGame.TeamColor color = chessPiece.getTeamColor();
                chessPiece = switch (chessPiece.getPieceType()) {
                    case PAWN -> new Pawn(color);
                    case ROOK -> new Rook(color);
                    case KNIGHT -> new Knight(color);
                    case BISHOP -> new Bishop(color);
                    case QUEEN -> new Queen(color);
                    case KING -> new King(color);
                };
            }
            return chessPiece;
        }
    };

    public static TypeAdapter<Piece> getChessPieceTypeAdapter() {
        return chessPieceTypeAdapter;
    }
}

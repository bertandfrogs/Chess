package ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";

    public static final String RESET_ALL_FORMATTING = UNICODE_ESCAPE + "[0m";

    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[39m";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String RESET_BG_COLOR = UNICODE_ESCAPE + "49m";

    public static final String BLACK_PIECE_COLOR = UNICODE_ESCAPE + "[31m"; // Red
    public static final String WHITE_PIECE_COLOR = UNICODE_ESCAPE + "[39m";

    // My console color theme
    public static final String THEME_PRIMARY_LIGHT = SET_TEXT_COLOR + "194m";
    public static final String THEME_PRIMARY = SET_TEXT_COLOR + "121m";
    public static final String THEME_ACCENT_1 = SET_TEXT_COLOR + "43m";
    public static final String THEME_ACCENT_2 = SET_TEXT_COLOR + "73m";
    public static final String THEME_DARK = SET_TEXT_COLOR + "23m"; //""97m";
    public static final String THEME_WARNING = SET_TEXT_COLOR + "208m";
    public static final String THEME_ERROR = SET_TEXT_COLOR + "196m";

    public static final String KING_ICON = " ♚ ";
    public static final String QUEEN_ICON = " ♛ ";
    public static final String ROOK_ICON = " ♜ ";
    public static final String BISHOP_ICON = " ♝ ";
    public static final String KNIGHT_ICON = " ♞ ";
    public static final String PAWN_ICON = " ♙ "; // using the white pawn, the black pawn needs to be text instead of emoji
    public static final String SPACER = "\u2003";
    public static final String EMPTY_SQUARE = " " + SPACER + " ";

    public static String moveCursorToLocation(int x, int y) { return UNICODE_ESCAPE + "[" + y + ";" + x + "H"; }

    public static String getCustomTextColorString(int colorIndex) {
        return SET_TEXT_COLOR + colorIndex + "m";
    }
}
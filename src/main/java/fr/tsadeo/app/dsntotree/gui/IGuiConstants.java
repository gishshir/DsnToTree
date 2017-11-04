package fr.tsadeo.app.dsntotree.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

public interface IGuiConstants {

    public static final Cursor WaitingCursor = new Cursor(Cursor.WAIT_CURSOR);

    public static final Dimension DIM_HOR_RIGID_AREA_10 = new Dimension(10, 0);
    public static final Dimension DIM_VER_RIGID_AREA_5 = new Dimension(0, 5);
    public static final Dimension DIM_VER_RIGID_AREA_15 = new Dimension(0, 15);

    public static final String ALL = "all";
    public static final String RC = "\n";
    public static final String TAB = "\t";
    public static final String SEP = ":";

    public static final String PATH_IMAGES = "images/";
    public static final String PATH_OPEN_ICO = PATH_IMAGES + "open.png";
    public static final String PATH_SAVE_ICO = PATH_IMAGES + "save.png";
    public static final String PATH_BLOC_ICO = PATH_IMAGES + "bloc.gif";
    public static final String PATH_RUB_ICO = PATH_IMAGES + "rubrique.gif";
    public static final String PATH_SHOW_ICO = PATH_IMAGES + "show.png";
    public static final String PATH_HIDE_ICO = PATH_IMAGES + "hide.png";
    public static final String PATH_ADD_RUB_ICO = PATH_IMAGES + "addrub.gif";
    public static final String PATH_ADD_BLOC_ICO = PATH_IMAGES + "addbloc.gif";
    public static final String PATH_SHOW_BLOC_ICO = PATH_IMAGES + "showbloc.png";
    public static final String PATH_SHOW_NORME_ICO = PATH_IMAGES + "norme.png";
    public static final String PATH_DEL_ICO = PATH_IMAGES + "delrub.gif";
    public static final String PATH_ERROR_ICO = PATH_IMAGES + "errors.png";
    public static final String PATH_FIND_ICO = PATH_IMAGES + "find.gif";
    public static final String PATH_DUPLICATE_ICO = PATH_IMAGES + "duplicate.gif";
    public static final String PATH_BDD_ICO = PATH_IMAGES + "bdd.png";
    public static final String PATH_EDIT_ICO = PATH_IMAGES + "edit.png";

    public static final String PATH_TEST_CONNEXION_ICO = PATH_IMAGES + "connexion.png";
    public static final String PATH_CONNEXION_OK_ICO = PATH_IMAGES + "ok.gif";
    public static final String PATH_CONNEXION_NOK_ICO = PATH_IMAGES + "nok.gif";
    public static final String PATH_CONNEXION_UNKNONW_ICO = PATH_IMAGES + "greymark.gif";
    public static final String PATH_SQL_FILE_ICO = PATH_IMAGES + "sqlrequest.png";
    public static final String PATH_SQL_REQUEST_ICO = PATH_IMAGES + "sqlrequest.png";
    public static final String PATH_SALARIES_ICO = PATH_IMAGES + "salaries.png";
    public static final String PATH_EDIT_SALARIE_ICO = PATH_IMAGES + "smalledit.png";

    public static final Font FONT = new Font("SansSerif", Font.PLAIN, 14);

    public final static Color HILIT_COLOR = Color.LIGHT_GRAY;
    public final static Color ERROR_COLOR = Color.PINK;
    public final static Color SEARCH_SUCCESS_COLOR = Color.cyan;
    public final static Color EDIT_COLOR = Color.BLUE;
    public final static String FOCUS_SEARCH_ACTION = "focus-search";
    public final static String CANCEL_SEARCH_ACTION = "cancel-search";
    public final static String NEXT_SEARCH_ACTION = "next-search";

    public final static Color TREE_BACKGROUND_DROPPABLE_COLOR = Color.YELLOW;
    public final static Color TREE_BACKGROUND_COLOR = Color.BLACK;
    public final static Color TREE_ERROR_COLOR = Color.RED;
    public final static Color TREE_CREATED_COLOR = Color.PINK;
    public final static Color TREE_MODIFIED_COLOR = Color.cyan;
    public final static Color TREE_NORMAL_COLOR = Color.white;
    public final static Color TREE_DROPPABLE_COLOR = Color.black;

    public final static Color DRAG_START_COLOR = Color.cyan;

    public final static String SHOW_OPEN_DIALOG_ACTION = "show-opendialog";
    public final static String SHOW_ERROR_DIALOG_ACTION = "show-errordialog";
    public final static String SAVE_DSN_ACTION = "save-dsn";
    public final static String SHOW_JDBC_ACTION = "show-jdbc";
    public final static String SHOW_NORME_ACTION = "show-norme";
    public final static String SHOW_SALARIES_ACTION = "show-salaries";
    public final static String SHOW_SALARIE_ACTION = "show-salarie";
    public final static String EDIT_SALARIE_ACTION = "edit-salarie";

    public final static String LIRE_BDD_MSG_ACTION = "lire-bdd-msg";
    public final static String EDIT_BDD_MSG_ACTION = "edit-bdd-msg";
    public final static String LOAD_SQL_FILE_MSG_ACTION = "load-sql-file-msg";
    public final static String LOAD_SQL_REQUEST_MSG_ACTION = "load-sql-request-msg";
    public final static String TESTER_BDD_ACTION = "test-bdd";

    public final static String VALIDER_SAISIE_ACTION = "saisie-valider";
    public final static String ANNULER_SAISIE_ACTION = "saisie-annuler";
    public final static String ADD_RUBRIQUE_ACTION = "add-rubrique";
    public final static String ADD_BLOC_ACTION = "add-bloc";

    public final static String NEXT_RUBRIQUE_ACTION = "next-rubrique";
}

/**
 * This program is a Java version of the ancient Norse game, hnefatafl ("table of the fist", in Old Norse). The game is
 * played by Copenhagen rules (11 x 11 square board), and the king must reach a corner to win.
 *
 * @author $team
 * Jennifer Fang, JFang1
 * Peter McCloskey, pcm17
 * Jason Tucker, tuckerj0
 * Guiseppe Zielinski, gcz3
 *
 * CS 1530 - Software Engineering
 * Spring Semester 2017
 */
package copenhagen;

import java.awt.*;
import javax.swing.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import javax.swing.filechooser.*;
import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 * This is the main file that runs the program.
 */
public class Hnefatafl {

    private static int choice;
    private static JFrame frame;
    private static JPanel board;
    private static JPanel side;
    private static JPanel bottom;
    private static JMenuBar menuBar;
    private static char[][] pieceLayout;
    private static int boardSize = 11;
    private static int turnCount = 0;
    private static char turn = 'b';
    private static GameBoard hBoard;
    private static int[] primaryColor = {244, 164, 96};
    private static int[] secondaryColor = {139, 69, 19};
    private static int[] letteringColor = {0, 0, 0};
    private static int[] specialColor = {0, 0, 88};
    private static int[] selectedLoc = {-1, -1};
    private static JButton selected;
    private static boolean pieceIsSelected = false;

    /**
     * This is the main method which starts the program.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        MainMenu start = new MainMenu();
        while (choice == 0) {
            try {
                choice = start.getChoice();
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong. Please try running the program again.");
                System.exit(1);
            }
        }
        if (choice == 1) {
            setUpGameBoard();
            displayGameBoard();
            playGame();
        }
    }

    /**
     * This function sets up and retrieves the different pieces (3 JPanels) that
     * comprise the game board.
     */
    public static void setUpGameBoard() {
        hBoard = new GameBoard(boardSize, primaryColor, secondaryColor, specialColor);
        board = hBoard.getBoard();
        pieceLayout = hBoard.getPieceLocations();
        SideBar sBar = new SideBar(primaryColor, secondaryColor, letteringColor);
        side = sBar.getSideBar();
        BottomBar bBar = new BottomBar(primaryColor, letteringColor, turn, turnCount);
        bottom = bBar.getBottomBar();
        MenuBar menu = new MenuBar();
        menuBar = menu.getMenuBar();
    }

    /**
     * This function adds the different pieces (3 JPanels) to the main JFrame of
     * the program and displays the game to the user.
     */
    public static void displayGameBoard() {
        /*Initialize JFrame. This will hold 3 JPanels*/
        frame = new JFrame("Hnefatafl");
        /*Add Board to lefthand side of JFrame*/
        frame.add(board, BorderLayout.LINE_START);
        frame.add(side, BorderLayout.EAST);
        frame.add(bottom, BorderLayout.SOUTH);
        /* Add Menu bar at top of JFrame*/
        frame.setJMenuBar(menuBar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * This function officially starts up the game.
     */
    public static void playGame() {
    }

    /**
     * This function changes whose turn it is at the end of each move. b = black
     * = attackers w = white = king and his defenders
     */
    public static void endTurn() {
        if (turn == 'b') {
            turn = 'w';
        } else if (turn == 'w') {
            turn = 'b';
        }
    }
    
    /**
     * This function returns whose turn it is. b = black = attackers, w = white = 
     * king and his defenders
     * 
     * @return turn This parameter holds whose turn it is. 
     */
    public static char getTurn() {
        return turn;
    }

    /**
     * This function is called whenever a square is clicked on the game board.
     *
     * @param c This is the column of the square clicked.
     * @param r This is the row of the square clicked.
     * @param clickedOn This is the specific piece (JButton) that was clicked
     * on.
     */
    public static void squareClicked(int c, int r, JButton clickedOn) {
        unselectLast();
        char chosenSquaresPiece = pieceLayout[c][r];
        if ((chosenSquaresPiece == 'w' && turn == 'w') || (chosenSquaresPiece == 'b' && turn == 'b') || (chosenSquaresPiece == 'k' && turn == 'w')) {
            boolean[][] highlight = getValidMoves(c, r);
            for (int i = 0; i < highlight.length; i++) {
                for (int j = 0; j < highlight[0].length; j++) {
                    if (highlight[i][j] == true) {
                        hBoard.highlightButton(i, j);
                    }
                }
            }
        }
        if ((chosenSquaresPiece == '0' || chosenSquaresPiece == 'c') && pieceIsSelected) {
            movePiece(c, r);
        }
        selectNew(clickedOn, c, r);
    }

    /**
     * This function is called to try and move the selected piece (selectedLoc)
     * if it is a valid move.
     *
     * @param c This parameter is the column they are trying to move to.
     * @param r This parameter is the row they are trying to move to.
     */
    public static void movePiece(int c, int r) {
        boolean[][] validMoves = getValidMoves(selectedLoc[0], selectedLoc[1]);
        if (validMoves[c][r] == true) {
            movePieceOnBoard(selectedLoc[0], selectedLoc[1], c, r);
            endTurn();
            turnCount++;
            BottomBar.endTurn(turn, turnCount);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Move");
        }
    }

    /**
     * This is the function that does the actual work from moving a game piece
     * from its original starting row and column to its new row and column.
     *
     * @param startCol This parameter is the starting column of the game piece.
     * @param startRow This parameter is the starting row of the game piece.
     * @param destCol This parameter is the destination column of the game
     * piece.
     * @param destRow This parameter is the destination column of the game
     * piece.
     */
    public static void movePieceOnBoard(int startCol, int startRow, int destCol, int destRow) {
        //update gameboard array
        char pieceType = pieceLayout[startCol][startRow];
        if ((startCol == 0 && startRow == 0)
                || (startCol == boardSize - 1 && startRow == boardSize - 1)
                || (startCol == 0 && startRow == boardSize - 1)
                || (startCol == boardSize - 1 && startRow == 0)) {
            pieceLayout[startCol][startRow] = 'c';
        } else {
            pieceLayout[startCol][startRow] = '0';
        }
        pieceLayout[destCol][destRow] = pieceType;

        //update gameboard gui
        JButton sButton = hBoard.getButtonByLocation(startCol, startRow);
        sButton.setIcon(null);

        JButton dButton = hBoard.getButtonByLocation(destCol, destRow);
        try {
            Image img;
            ImageIcon icon;
            if (pieceType == 'b') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/blackpiece.png"));
                icon = new ImageIcon(img);
                dButton.setIcon(icon);
            } else if (pieceType == 'w') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/whitePiece.png"));
                icon = new ImageIcon(img);
                dButton.setIcon(icon);
            } else if (pieceType == 'k') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/king.png"));
                icon = new ImageIcon(img);
                dButton.setIcon(icon);
            }
        } catch (IOException e) {
            System.out.println("Image Didn't Load");
            System.exit(1);
        }
    }

    /**
     * This function finds where a piece is allowed to move based on the rules
     * of the game.
     *
     * @param col This parameter is the current column that the game piece is
     * located at.
     * @param row This parameter is the current row that the game piece is
     * located at.
     * @return This function returns a boolean array matching the gameboard with
     * true values on all of the spaces a piece can move to.
     */
    public static boolean[][] getValidMoves(int col, int row) {
        boolean[][] validSpaces = new boolean[boardSize][boardSize];
        for (int i = col + 1; i < boardSize; i++) {//check move right
            if ((pieceLayout[i][row] == '0') || (pieceLayout[i][row] == 'c')) {
                validSpaces[i][row] = true;
            } else {
                break;
            }
        }
        for (int i = col - 1; i >= 0; i--) {//check move left
            if ((pieceLayout[i][row] == '0') || (pieceLayout[i][row] == 'c')) {
                validSpaces[i][row] = true;
            } else {
                break;
            }
        }
        for (int i = row + 1; i < boardSize; i++) {//check move down
            if ((pieceLayout[col][i] == '0') || (pieceLayout[col][i] == 'c')) {
                validSpaces[col][i] = true;
            } else {
                break;
            }
        }
        for (int i = row - 1; i >= 0; i--) {//check move up
            if ((pieceLayout[col][i] == '0') || (pieceLayout[col][i] == 'c')) {
                validSpaces[col][i] = true;
            } else {
                break;
            }
        }
        return validSpaces;
    }

    /**
     * This function unselects the previous piece when a new piece is selected.
     */
    public static void unselectLast() {
        if (!pieceIsSelected) {
            return;
        }
        boolean[][] unhighlight = getValidMoves(selectedLoc[0], selectedLoc[1]);
        for (int i = 0; i < unhighlight.length; i++) {
            for (int j = 0; j < unhighlight[0].length; j++) {
                if (unhighlight[i][j] == true) {
                    hBoard.unhighlightButton(i, j);
                }
            }
        }
        char pieceType = pieceLayout[selectedLoc[0]][selectedLoc[1]];
        try {
            Image img;
            ImageIcon icon;
            if (pieceType == 'b') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/blackpiece.png"));
                icon = new ImageIcon(img);
                selected.setIcon(icon);
            } else if (pieceType == 'w') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/whitePiece.png"));
                icon = new ImageIcon(img);
                selected.setIcon(icon);
            } else if (pieceType == 'k') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/king.png"));
                icon = new ImageIcon(img);
                selected.setIcon(icon);
            }
        } catch (IOException e) {
            System.out.println("Image Didn't Load");
            System.exit(1);
        }
    }

    /**
     * This function sets a new game piece to the selected game piece.
     *
     * @param clickedOn This parameter is the game piece (JButton) that is
     * clicked on.
     * @param c This parameter is the column of the game piece that is clicked
     * on.
     * @param r This parameter is the row of the game piece that is clicked on.
     */
    public static void selectNew(JButton clickedOn, int c, int r) {
        char piece = pieceLayout[c][r];
        try {
            selectedLoc[0] = c;
            selectedLoc[1] = r;
            selected = clickedOn;
            Image img;
            ImageIcon icon;
            pieceIsSelected = true;
            if (piece == 'b' && turn == 'b') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/blackpieceSelected.png"));
                icon = new ImageIcon(img);
                clickedOn.setIcon(icon);
            } else if (piece == 'w' && turn == 'w') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/whitepieceSelected.png"));
                icon = new ImageIcon(img);
                clickedOn.setIcon(icon);
            } else if (piece == 'k' && turn == 'w') {
                img = ImageIO.read(Hnefatafl.class.getResource("images/kingSelected.png"));
                icon = new ImageIcon(img);
                clickedOn.setIcon(icon);
            } else {
                pieceIsSelected = false;
            }
        } catch (IOException e) {
            System.out.println("Image Didn't Load");
            System.exit(1);
        }
    }

    /**
     * This function saves the present game state to a save file.
     *
     * @return This function will return true if successful or false in the case
     * of an IOException.
     */
    public static boolean saveGame() {
        PrintWriter writer = null;
        File game = null;
        try {

            JFrame parentFrame = new JFrame();

            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("hnef", "hnef");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Save file");
            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                game = fileChooser.getSelectedFile();
                System.out.println("Save as file: " + game.getAbsolutePath());
                writer = new PrintWriter(game, "UTF-8");
                writer.println(turnCount);
                writer.println(turn);
                char[][] layout = hBoard.getPieceLocations();
                int size = hBoard.getGridSize();
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        writer.print(layout[i][j]);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    /**
     * This function loads a game state from a file and validates it. It must
     * have a .hnef extension to be accepted.
     *
     * @return The return value represents the status code. 0 is success. 1 is
     * failure. 2 is failure due to incorrect extension.
     */
    public static int loadGame() {
        BufferedReader br = null;
        FileReader fr = null;
        File fileName = null;
        int savedTurnCount;
        char savedCurrentTurn;
        String savedLayout;
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("hnef", "hnef");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileName = fileChooser.getSelectedFile();
            }
            String extension = ""; 									// checking file extension. Must be .hnef
            String name = fileName.toString();
            int i = name.lastIndexOf('.');
            if (i > 0) {
                extension = name.substring(i + 1);
            }
            if (!extension.equals("hnef")) {						// checking file extension. Must be .hnef
                return 2;
            }
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String currentLine;
            br = new BufferedReader(new FileReader(fileName));
            i = 0;
            while ((currentLine = br.readLine()) != null) {
                if (i == 0) {
                    savedTurnCount = Integer.parseInt(currentLine);
                } else if (i == 1) {
                    savedCurrentTurn = currentLine.charAt(0);
                } else if (i == 2) {
                    savedLayout = currentLine;
                }
                i++;
            }
        } catch (IOException e) {
            return 1;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ex) {
                return 1;
            }
        }
        return 0;
    }
}

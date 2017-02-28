/**
 * This program is a Java version of the ancient Norse game hnefatafl ("table of the fist", in Old Norse). The game is
 * played by Copenhagen rules (11 x 11 square board).
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
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.File;
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
	public static char[][] pieceLayout;
	private static int boardSize = 11;
	private static int turnCount = 1;
	public static char turn = 'b';
	private static GameBoard hBoard;
	private static int[] primaryColor = {244,164,96};
	private static int[] secondaryColor = {139,69,19};
	private static int[] letteringColor = {0,0,0};
	private static int[] specialColor = {0,0,88};
	private static BoardLocation selectedLoc;
	private static JButton selected;
	private static boolean pieceIsSelected = false;
	private static char winner;
	private static FinalMenu finalMenu;

    /**
     * This is the main method which starts the program.
     * @param args Unused.
     */
	public static void main(String[] args) {
		MainMenu start = new MainMenu();
		while (choice == 0) {
			try {
				choice = start.getChoice();
				TimeUnit.MILLISECONDS.sleep(1);
			}
			catch(InterruptedException e) {
				System.out.println("Something went wrong. Please try running the program again.");
				System.exit(1);
			}
		}
		if (choice == 1) {
			setUpGameBoard();
			displayGameBoard();
		}
	}

    /**
     * This function sets up and retrieves the different pieces (3 JPanels) that comprise the game board.
     */
	public static void setUpGameBoard() {
		hBoard = new GameBoard(boardSize, primaryColor, secondaryColor, specialColor);
		board = hBoard.getBoard();
		pieceLayout = GameLogic.getGameBoardArray();
		SideBar sBar = new SideBar(primaryColor, secondaryColor, letteringColor);
		side = sBar.getSideBar();
		BottomBar bBar = new BottomBar(primaryColor, letteringColor, turn, turnCount);
		bottom = bBar.getBottomBar();
        MenuBar menu = new MenuBar();
        menuBar = menu.getMenuBar();
		turnCount = 0;
		turn = 'b';
		selectedLoc = new BoardLocation();
	}

    /**
     * This function adds the different pieces (3 JPanels) to the main JFrame of the program and displays the game to
     * the user.
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
		frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}

    /**
     * This function changes whose turn it is at the end of each move and updates turn info appropriately.
     * b = black = attackers
     * w = white = king and his defenders
     */
	public static int endTurn() {
	    if (turn == 'b') {
	        turn = 'w';
        }
        else if (turn == 'w') {
	        turn = 'b';
        }
		turnCount++;
		BottomBar.updateTurnInfo(turn, turnCount);
		winner = GameLogic.checkWinner(pieceLayout);
		if (winner != '0') {
			finalMenu = new FinalMenu(winner);
		}
		return turnCount;
    }

    /**
     * This function is called whenever a square is clicked on the game board.
     * @param c This is the column of the square clicked.
     * @param r This is the row of the square clicked.
     * @param clickedOn This is the specific piece (JButton) that was clicked on.
     */
	public static void squareClicked(int c, int r, JButton clickedOn){
		GameBoard.unselectLast(pieceIsSelected, selectedLoc, selected); // unselects last piece
		// highlighting valid moves for the newly chosen piece
		char chosenSquaresPiece = GameLogic.getPiece(c, r);
		if (GameLogic.pieceCanMove(chosenSquaresPiece,turn)) {
			boolean[][] highlight = GameLogic.getValidMoves(chosenSquaresPiece, c, r, pieceLayout);
			for(int i = 0; i < highlight.length; i++){
				for(int j = 0; j < highlight[0].length; j++){
					if(highlight[i][j] == true){
						hBoard.highlightButton(i,j);
					}
				}
			}
		}
		// moves piece if appropriate
		if((chosenSquaresPiece == '0' || chosenSquaresPiece == 'c') && pieceIsSelected){
			movePiece(c,r);
		}
		selectNew(clickedOn,c,r);
	}

    /**
     * This function is called to try and move the selected piece (selectedLoc) if it is a valid move.
     * @param c This parameter is the column they are trying to move to.
     * @param r This parameter is the row they are trying to move to.
     */
	public static void movePiece(int c, int r){
        char pieceType = pieceLayout[selectedLoc.getColumn()][selectedLoc.getRow()];
        boolean[][] validMoves = GameLogic.getValidMoves(pieceType, selectedLoc.getColumn(),selectedLoc.getRow(), pieceLayout);
		if(validMoves[c][r] == true){
			movePieceOnBoard(selectedLoc.getColumn(),selectedLoc.getRow(),c,r);
            endTurn();
		}else{
			JOptionPane.showMessageDialog(null, "Invalid Move");
		}
	}

    /**
     * This function is called to determine if there is a shieldwall during a move by a piece.
     * TODO: Refactor this code!
     * @param piece This is the piece that has been moved.
     * @param col This is the column of where the piece will be going.
     * @param row This is the row of where the piece will be going.
     * @param capturePiece This is the kind of piece that is allowed to be captured.
     * @param helperPiece This is the kind of piece that helps in a capture:
     *                     i.e. another black piece if piece == 'b'
     *                          the king piece if piece == 'w'
     */
    private static void findShieldwall(char piece, int col, int row, char capturePiece, char helperPiece) {
        LinkedList<Integer> capturedPieceCol = new LinkedList<>();
        LinkedList<Integer> capturedPieceRow = new LinkedList<>();
        int counter = 0;

        if (col == 0) {
            if (row <= 7) {
                for (int i = row+1; i < 11; i++) {
                    if (pieceLayout[col][i] == '0') {
                        break;
                    }
                    else if (pieceLayout[col][i] == capturePiece && (pieceLayout[col+1][i] == piece || pieceLayout[col+1][i] == helperPiece)) {
                        capturedPieceCol.add(col);
                        capturedPieceRow.add(i);
                        counter++;
                    }
                    else if (pieceLayout[col][i] == piece || pieceLayout[col][i] == helperPiece || pieceLayout[col][i] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
            if (row >= 3) {
                for (int i = row-1; i >= 0; i--) {
                    if (pieceLayout[col][i] == '0') {
                        break;
                    }
                    else if (pieceLayout[col][i] == capturePiece && (pieceLayout[col+1][i] == piece || pieceLayout[col+1][i] == helperPiece)) {
                        capturedPieceCol.add(col);
                        capturedPieceRow.add(i);
                        counter++;
                    }
                    else if (pieceLayout[col][i] == piece || pieceLayout[col][i] == helperPiece || pieceLayout[col][i] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
        }

        if (col == boardSize-1) {
            if (row <= 7) {
                for (int i = row+1; i < 11; i++) {
                    if (pieceLayout[col][i] == '0') {
                        break;
                    }
                    else if (pieceLayout[col][i] == capturePiece && (pieceLayout[col-1][i] == piece || pieceLayout[col-1][i] == helperPiece)) {
                        capturedPieceCol.add(col);
                        capturedPieceRow.add(i);
                        counter++;
                    }
                    else if (pieceLayout[col][i] == piece || pieceLayout[col][i] == helperPiece || pieceLayout[col][i] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
            if (row >= 3) {
                for (int i = row-1; i >= 0; i--) {
                    if (pieceLayout[col][i] == '0') {
                        break;
                    }
                    else if (pieceLayout[col][i] == capturePiece && (pieceLayout[col-1][i] == piece || pieceLayout[col-1][i] == helperPiece)) {
                        capturedPieceCol.add(col);
                        capturedPieceRow.add(i);
                        counter++;
                    }
                    else if (pieceLayout[col][i] == piece || pieceLayout[col][i] == helperPiece || pieceLayout[col][i] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
        }

        if (row == 0) {
            if (col <= 7) {
                for (int i = col+1; i < 11; i++) {
                    if (pieceLayout[i][row] == '0') {
                        break;
                    }
                    else if (pieceLayout[i][row] == capturePiece && (pieceLayout[i][row+1] == piece || pieceLayout[i][row+1] == helperPiece)) {
                        capturedPieceCol.add(i);
                        capturedPieceRow.add(row);
                        counter++;
                    }
                    else if (pieceLayout[i][row] == piece || pieceLayout[i][row] == helperPiece || pieceLayout[i][row] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
            if (col >= 3) {
                for (int i = col-1; i >= 0; i--) {
                    if (pieceLayout[i][row] == '0') {
                        break;
                    }
                    else if (pieceLayout[i][row] == capturePiece && (pieceLayout[i][row+1] == piece || pieceLayout[i][row+1] == helperPiece)) {
                        capturedPieceCol.add(i);
                        capturedPieceRow.add(row);
                        counter++;
                    }
                    else if (pieceLayout[i][row] == piece || pieceLayout[i][row] == helperPiece || pieceLayout[i][row] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
        }

        if (row == boardSize-1) {
            if (col <= 7) {
                for (int i = col+1; i < 11; i++) {
                    if (pieceLayout[i][row] == '0') {
                        break;
                    }
                    else if (pieceLayout[i][row] == capturePiece && (pieceLayout[i][row-1] == piece || pieceLayout[i][row-1] == helperPiece)) {
                        capturedPieceCol.add(i);
                        capturedPieceRow.add(row);
                        counter++;
                    }
                    else if (pieceLayout[i][row] == piece || pieceLayout[i][row] == helperPiece || pieceLayout[i][row] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
            if (col >= 3) {
                for (int i = col-1; i >= 0; i--) {
                    if (pieceLayout[i][row] == '0') {
                        break;
                    }
                    else if (pieceLayout[i][row] == capturePiece && (pieceLayout[i][row-1] == piece || pieceLayout[i][row-1] == helperPiece)) {
                        capturedPieceCol.add(i);
                        capturedPieceRow.add(row);
                        counter++;
                    }
                    else if (pieceLayout[i][row] == piece || pieceLayout[i][row] == helperPiece || pieceLayout[i][row] == 'c') {
                        if (counter >= 2) {
                            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * This function finds all the pieces that will be captured on the board by the move just completed.
     * @param piece This parameter is the piece that getting moved.
     * @param col This parameter is which column the piece will be at after it is moved.
     * @param row This parameter is which row the piece will be at after it is moved.
     */
    public static void findCapturedPieces(char piece, int col, int row) {
	    char capturablePiece;
	    char kingPiece;
	    LinkedList<Integer> capturedPieceCol = new LinkedList<>();
        LinkedList<Integer> capturedPieceRow = new LinkedList<>();
	    if (piece == 'b') {
	        capturablePiece = 'w';
	        kingPiece = 'b';
        }
        else if (piece == 'w'){
            capturablePiece = 'b';
            kingPiece = 'k';
        }
        else {
            capturablePiece = 'b';
            kingPiece = 'w';
        }
        if (col-2 >= 0) {
            if (pieceLayout[col - 1][row] == capturablePiece && (pieceLayout[col - 2][row] == piece || pieceLayout[col - 2][row] == kingPiece || pieceLayout[col - 2][row] == 'c')) {
                capturedPieceCol.add(col - 1);
                capturedPieceRow.add(row);
            }
        }
        if (col+2 <= boardSize-1) {
            if (pieceLayout[col + 1][row] == capturablePiece && (pieceLayout[col + 2][row] == piece || pieceLayout[col + 2][row] == kingPiece || pieceLayout[col + 2][row] == 'c')) {
                capturedPieceCol.add(col + 1);
                capturedPieceRow.add(row);
            }
        }
        if (row-2 >= 0) {
            if (pieceLayout[col][row - 1] == capturablePiece && (pieceLayout[col][row - 2] == piece || pieceLayout[col][row - 2] == kingPiece || pieceLayout[col][row - 2] == 'c')) {
                capturedPieceCol.add(col);
                capturedPieceRow.add(row - 1);
            }
        }
        if (row+2 <= boardSize-1) {
            if (pieceLayout[col][row + 1] == capturablePiece && (pieceLayout[col][row + 2] == piece || pieceLayout[col][row + 2] == kingPiece || pieceLayout[col][row + 2] == 'c')) {
                capturedPieceCol.add(col);
                capturedPieceRow.add(row + 1);
            }
        }
        if (!capturedPieceCol.isEmpty()) {
            pieceLayout = GameLogic.removeCapturedPieces(capturedPieceCol, capturedPieceRow, pieceLayout);
	    }
	    else {
            if (col == 0 || col == 10 || row == 0 || row == 10) {
                findShieldwall(piece, col, row, capturablePiece, kingPiece);
            }
        }
    }


    /**
     * This is the function that does the actual work from moving a game piece from its original starting row and column
     * to its new row and column.
     * @param startCol This parameter is the starting column of the game piece.
     * @param startRow This parameter is the starting row of the game piece.
     * @param destCol This parameter is the destination column of the game piece.
     * @param destRow This parameter is the destination column of the game piece.
     */
	public static void movePieceOnBoard(int startCol,int startRow,int destCol, int destRow){
		//update gameboard array
		char pieceType = GameLogic.getPiece(startCol, startRow);
		if ((startCol==0 && startRow==0) ||
			(startCol==boardSize-1 && startRow==boardSize-1) ||
			(startCol==0 && startRow==boardSize-1) ||
			(startCol==boardSize-1 && startRow==0) || (startCol==5 && startRow==5)) {
			pieceLayout[startCol][startRow] = 'c';
			GameLogic.updateGameBoard(startCol, startRow, 'c');

		}else{
			pieceLayout[startCol][startRow] = '0';
            GameLogic.updateGameBoard(startCol, startRow, '0');
        }
		pieceLayout[destCol][destRow] = pieceType;
        GameLogic.updateGameBoard(destCol, destRow, pieceType);
        findCapturedPieces(pieceType, destCol, destRow);

		//update gameboard gui
		JButton sButton = hBoard.getButtonByLocation(startCol,startRow);
		sButton.setIcon(null);

		JButton dButton = hBoard.getButtonByLocation(destCol,destRow);
		setButtonImage(pieceType,dButton);
	}


	/**
     * This function sets the icon of a particular button.
     * @param pieceType The value of the piece from the characters specified in gameboard.java
     * @param button The button to add this image to
     */
	public static void setButtonImage(char pieceType, JButton button){
		try {
			Image img;
			ImageIcon icon;
			if(pieceType == 'b'){
				img = ImageIO.read(Hnefatafl.class.getResource("images/blackpiece.png"));
				icon = new ImageIcon(img);
				button.setIcon(icon);
			}else if(pieceType == 'w'){
				img = ImageIO.read(Hnefatafl.class.getResource("images/whitePiece.png"));
				icon = new ImageIcon(img);
				button.setIcon(icon);
			}else if(pieceType == 'k'){
				img = ImageIO.read(Hnefatafl.class.getResource("images/king.png"));
				icon = new ImageIcon(img);
				button.setIcon(icon);
			}
		} catch (IOException e) {
			System.out.println("Image Didn't Load");
			System.exit(1);
		}
	}

    /**
     * This function sets a new game piece to the selected game piece.
     * @param clickedOn This parameter is the game piece (JButton) that is clicked on.
     * @param c This parameter is the column of the game piece that is clicked on.
     * @param r This parameter is the row of the game piece that is clicked on.
     */
	public static void selectNew(JButton clickedOn,int c, int r){
		char piece = GameLogic.getPiece(c, r);

        try {
			selectedLoc.setColumn(c);
			selectedLoc.setRow(r);
			selected = clickedOn;
			Image img;
			ImageIcon icon;
			pieceIsSelected = true;
			if(piece == 'b' && turn == 'b'){
				img = ImageIO.read(Hnefatafl.class.getResource("images/blackpieceSelected.png"));
				icon = new ImageIcon(img);
				clickedOn.setIcon(icon);
			}else if(piece == 'w' && turn == 'w'){
				img = ImageIO.read(Hnefatafl.class.getResource("images/whitepieceSelected.png"));
				icon = new ImageIcon(img);
				clickedOn.setIcon(icon);
			}else if(piece == 'k' && turn == 'w'){
				img = ImageIO.read(Hnefatafl.class.getResource("images/kingSelected.png"));
				icon = new ImageIcon(img);
				clickedOn.setIcon(icon);
			}else{
				pieceIsSelected = false;
			}
		} catch (IOException e) {
			System.out.println("Image Didn't Load");
			System.exit(1);
		}
	}

    /**
     * This function saves the present game state to a save file.
     * @return This function will return true if successful or false in the case of an IOException.
     */
	public static boolean saveGame(){
		return SaveAndLoad.save(boardSize, pieceLayout, turn,turnCount);
	}

    /**
     * This function loads a game state from a file and validates it.
     * It must have a .hnef extension to be accepted.
     * @return The return value is a boolean representing success or failure
     */
	public static boolean loadGame(){
		File loadFile = SaveAndLoad.load();
		if(loadFile == null){
			return false;
		}
		return true;
	}

    /**
     * This function begins a new game.
     */
	public static void newGame(){
		newGameResetTurns();
		newGameGUI();
	}

	/**
	 * This function sets up the logic for a new game.
	 * @return The return value is the reset turn count (which should always be 1)
	 */
	public static int newGameResetTurns(){
		turnCount = 1;
		turn = 'b';
		return turnCount;
	}

	/**
	 * This function sets up the GUI for a new game.
	 */
	public static void newGameGUI(){
		frame.remove(bottom);
		BottomBar bBar = new BottomBar(primaryColor, letteringColor, turn, turnCount);
		bottom = bBar.getBottomBar();
		frame.remove(board);
		hBoard = new GameBoard(boardSize, primaryColor, secondaryColor, specialColor);
		board = hBoard.getBoard();
		pieceLayout = GameLogic.getGameBoardArray();
		frame.add(board, BorderLayout.LINE_START);
		frame.add(bottom, BorderLayout.SOUTH);
		frame.pack();
	}

    /**
     * This functions gets the HBoard.
     * @return This function will return the jPanel that is displayed to the user of the current gameboard.
     */
	public static GameBoard getHBoard(){
		return hBoard;
	}
}

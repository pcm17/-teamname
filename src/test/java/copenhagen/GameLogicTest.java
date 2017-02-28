import org.junit.Test;
import static org.junit.Assert.*;

import copenhagen.GameLogic;

public class GameLogicTest {
	
	// This test checks if a piece can move if the piece and turn are white.
	@Test
	public void testPieceCanMoveIfPieceAndTurnAreWhite() {
		assertEquals(true, GameLogic.pieceCanMove('w', 'w'));
	}
	
	// This test checks if a piece can move if the piece and turn are black.
	@Test
	public void testPieceCanMoveIfPieceAndTurnAreBlack() {
		assertEquals(true, GameLogic.pieceCanMove('b', 'b'));
	}
	
	// This test checks if a piece can move if the piece is the king and turn is white.
	@Test
	public void testPieceCanMoveIfPieceIsKingAndTurnIsWhite() {
		assertEquals(true, GameLogic.pieceCanMove('k', 'w'));
	}
	
	// This test checks if a piece can move if the piece is white and turn is black.
	@Test
	public void testPieceCanMoveIfPieceIsWhiteAndTurnIsBlack() {
		assertEquals(false, GameLogic.pieceCanMove('w', 'b'));
	}
	
	// This test checks if a piece can move if the piece is black and turn is white.
	@Test
	public void testPieceCanMoveIfPieceIsBlackAndTurnIsWhite() {
		assertEquals(false, GameLogic.pieceCanMove('b', 'w'));
	}
	
	// This test checks to make sure that correct piece is returned.
	@Test
	public void testGetPiece() {
		int col = 4;
		int row = 8;
		char expected = 'k';
		char[][] gameBoard = new char [11][11];
		gameBoard[col][row] = 'k';
		gameBoard[col-1][row] = gameBoard[col+1][row] = gameBoard[col][row-1] = gameBoard[col][row+1] = 'b';
		GameLogic gl = new GameLogic();
		gl.gameBoardArray = gameBoard;
		char actual = gl.getPiece(col, row);
		assertEquals(expected, actual);
	}
	
	// This test is to ensure that the game board array is created correctly with all the right pieces in the correct
	// spot.
	@Test
	public void testSetStartingPiecesIfSizeIsEleven() {
		int size = 11;
		
		char[][] expected = new char [size][size];
		for (int i = 0; i < expected.length; i++) {
			for (int j = 0; j < expected[i].length; j++) {
				expected[i][j] = '0';
			}
		}
		expected[0][3] = expected[0][4] = expected[0][5] = expected[0][6] = expected[0][7] = expected[1][5] = 'b';
		expected[3][0] = expected[4][0] = expected[5][0] = expected[6][0] = expected[7][0] = expected[5][1] = 'b';
		expected[10][3] = expected[10][4] = expected[10][5] = expected[10][6] = expected[10][7] = expected[9][5] = 'b';
		expected[3][10] = expected[4][10] = expected[5][10] = expected[6][10] = expected[7][10] = expected[5][9] = 'b';
		expected[3][5] = expected[4][4] = expected[4][5] = expected[4][6] = expected[5][3] = expected[5][4] = 'w';
		expected[5][6] = expected[5][7] = expected[6][4] = expected[6][5] = expected[6][6] = expected[7][5] = 'w';
		expected[0][0] = expected[0][10] = expected[10][0] = expected[10][10] = expected[5][5] = 'c';
		expected[5][5] = 'k';
		
		char[][] actual = GameLogic.setStartingPieces(size);
		
		// check each individual position to ensure correct value
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				//System.out.println("Expected: " + expected[i][j]);
				//System.out.println("Actual: " + actual[i][j]);
				assertEquals(expected[i][j], actual[i][j]);
			}
		}
	}
	
	// This test checks to see if the game board array is filled with 0s if the size of the board is not 11
	// or potentially 9 if that feature is added later.
	@Test
	public void testSetStartingPiecesIfInvalidSize() {
		int size = 1;
		
		char[][] expected = new char [size][size];
		expected[0][0] = '0';
		
		char[][] actual = GameLogic.setStartingPieces(size);
		assertEquals(expected[0][0], actual[0][0]);
	}
	
	// This test checks to make sure the game board array is sent back properly when called.
	@Test
	public void testGetGameBoardArray() {
		char[][] expected = new char [1][1];
		expected[0][0] = '0';
		GameLogic gl = new GameLogic();
		gl.gameBoardArray = expected;
		char[][] actual = gl.getGameBoardArray();
		assertEquals(expected[0][0], actual[0][0]);
	}
	
	// This test checks that the defenders win when the king reaches one of the corners
	@Test
	public void testDefendersWinWhenKingInCorner() {
		char expected = 'w';
		char[][] board = new char[11][11];
		board[0][0] = 'k';
		GameLogic gl = new GameLogic();
		
		char actual = gl.checkWinner(board);
		assertEquals(actual, expected);
		
	}
	
	@Test
	public void testAttckersWinWhenSurroundKing() {
		char expected = 'b';
		char[][] board = new char[11][11];
		GameLogic gl = new GameLogic();
		char actual;
		board[2][2] = 'k';
		board[2][1] = board[1][2] = board[3][2] = board[2][3] = 'b';
		
		actual = gl.checkWinner(board);
		assertEquals(actual, expected);
		
	}
	@Test
	public void testAttckersWinWhenSurroundKingOnLeftEdge() {
		char expected = 'b';
		char[][] hBoard = new char[11][11];
		GameLogic gl = new GameLogic();
		
		hBoard[5][0] = 'k';
		hBoard[4][0] = hBoard[6][0] = hBoard[5][1] = 'b';
		char actual = gl.checkWinner(hBoard);
		assertEquals(actual, expected);
	}
	@Test
	public void testAttckersWinWhenSurroundKingOnRightEdge() {
		char expected = 'b';
		char[][] hBoard = new char[11][11];
		GameLogic gl = new GameLogic();
		
		hBoard[5][10] = 'k';
		hBoard[4][10] = hBoard[6][10] = hBoard[5][9] = 'b';
		char actual = gl.checkWinner(hBoard);
		assertEquals(actual, expected);
	}
	
	@Test
	public void testAttckersWinWhenSurroundKingOnBottomEdge() {
		char expected = 'b';
		char[][] hBoard = new char[11][11];
		GameLogic gl = new GameLogic();
		
		hBoard[10][5] = 'k';
		hBoard[10][4] = hBoard[10][6] = hBoard[9][5] = 'b';
		char actual = gl.checkWinner(hBoard);
		assertEquals(actual, expected);
	}
	@Test
	public void testAttckersWinWhenSurroundKingOnTopEdge() {
		char expected = 'b';
		char[][] hBoard = new char[11][11];
		GameLogic gl = new GameLogic();
		
		hBoard[0][5] = 'k';
		hBoard[0][4] = hBoard[0][6] = hBoard[1][5] = 'b';
		char actual = gl.checkWinner(hBoard);
		assertEquals(actual, expected);
	}
	
	
}

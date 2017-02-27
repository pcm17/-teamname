import org.junit.Test;
import static org.junit.Assert.*;

import copenhagen.GameLogic;

public class ValidMovesTest {
	
	/**
	 This test ensures that all spaces not along a straight path from the start space are invalid locations to move to. It tests all locations on the board with pieces set up in their initial locations.
	 */
	@Test
	public void testMovesOnlyHorizontalVertical() {
		char piece = 'w';
		int boardSize = 11;
		char[][] pieceLayout = new char [boardSize][boardSize];
		boolean[][] validMoves;
		
		for (int i = 0; i < pieceLayout.length; i++) {
			for (int j = 0; j < pieceLayout[i].length; j++) {
				pieceLayout[i][j] = '0';
			}
		}
		pieceLayout[0][3] = pieceLayout[0][4] = pieceLayout[0][5] = pieceLayout[0][6] = pieceLayout[0][7] = pieceLayout[1][5] = 'b';
		pieceLayout[3][0] = pieceLayout[4][0] = pieceLayout[5][0] = pieceLayout[6][0] = pieceLayout[7][0] = pieceLayout[5][1] = 'b';
		pieceLayout[10][3] = pieceLayout[10][4] = pieceLayout[10][5] = pieceLayout[10][6] = pieceLayout[10][7] = pieceLayout[9][5] = 'b';
		pieceLayout[3][10] = pieceLayout[4][10] = pieceLayout[5][10] = pieceLayout[6][10] = pieceLayout[7][10] = pieceLayout[5][9] = 'b';
		pieceLayout[3][5] = pieceLayout[4][4] = pieceLayout[4][5] = pieceLayout[4][6] = pieceLayout[5][3] = pieceLayout[5][4] = 'w';
		pieceLayout[5][6] = pieceLayout[5][7] = pieceLayout[6][4] = pieceLayout[6][5] = pieceLayout[6][6] = pieceLayout[7][5] = 'w';
		pieceLayout[0][0] = pieceLayout[0][10] = pieceLayout[10][0] = pieceLayout[10][10] = pieceLayout[5][5] = 'c';
		pieceLayout[5][5] = 'k';
		
		for (int row = 0; row<boardSize; row++ ) {
			for (int col = 0; col<boardSize; col++) {
				
				validMoves = GameLogic.getValidMoves(piece, row, col, pieceLayout);
				
				for (int i = 0 ; i < boardSize ; i++){
					for (int j = 0 ; j < boardSize ; j++){
						if(i != row && j != col) {
							assertEquals(validMoves[i][j], false);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 This test is to ensure an obstactle below the piece being moved invalids all moves to locations beyond the obstacle. It tests all locations on the board with pieces set up in their initial locations.
	 */
	@Test
	public void testObstacleBlocksSouthernPath() {
		char piece = 'w';
		int boardSize = 11;
		boolean pieceInWay = false;
		char[][] pieceLayout = new char [boardSize][boardSize];
		boolean[][] validMoves;
		
		for (int i = 0; i < pieceLayout.length; i++) {
			for (int j = 0; j < pieceLayout[i].length; j++) {
				pieceLayout[i][j] = '0';
			}
		}
		pieceLayout[0][3] = pieceLayout[0][4] = pieceLayout[0][5] = pieceLayout[0][6] = pieceLayout[0][7] = pieceLayout[1][5] = 'b';
		pieceLayout[3][0] = pieceLayout[4][0] = pieceLayout[5][0] = pieceLayout[6][0] = pieceLayout[7][0] = pieceLayout[5][1] = 'b';
		pieceLayout[10][3] = pieceLayout[10][4] = pieceLayout[10][5] = pieceLayout[10][6] = pieceLayout[10][7] = pieceLayout[9][5] = 'b';
		pieceLayout[3][10] = pieceLayout[4][10] = pieceLayout[5][10] = pieceLayout[6][10] = pieceLayout[7][10] = pieceLayout[5][9] = 'b';
		pieceLayout[3][5] = pieceLayout[4][4] = pieceLayout[4][5] = pieceLayout[4][6] = pieceLayout[5][3] = pieceLayout[5][4] = 'w';
		pieceLayout[5][6] = pieceLayout[5][7] = pieceLayout[6][4] = pieceLayout[6][5] = pieceLayout[6][6] = pieceLayout[7][5] = 'w';
		pieceLayout[0][0] = pieceLayout[0][10] = pieceLayout[10][0] = pieceLayout[10][10] = pieceLayout[5][5] = 'c';
		pieceLayout[5][5] = 'k';
		
		for (int pieceType = 0; pieceType < 2; pieceType++){
			for (int row = 0; row<boardSize; row++ ) {
				for (int col = 0; col<boardSize; col++) {
					
					validMoves = GameLogic.getValidMoves(piece, row, col, pieceLayout);
					pieceInWay = false;
					
					for (int i = row+1; i < boardSize ; i++){
						if (pieceLayout[i][col] == 'b' || pieceLayout[i][col] == 'w' || (pieceLayout[i][col] == 'c' && piece != 'k') || pieceLayout[i][col] == 'k' || pieceInWay == true) {
							assertEquals(validMoves[i][col], false);
							pieceInWay = true;
						} else {
							assertEquals(validMoves[i][col], true);
						}
					}
				}
			}
			piece = 'k';
		}
	}

	
	/**
	 This test is to ensure an obstactle above the piece being moved invalids all moves to locations beyond the obstacle. It tests all locations on the board with pieces set up in their initial locations.
	 
	 */
	@Test
	public void testObstacleBlocksNorthernPath() {
		char piece = 'w';
		int boardSize = 11;
		boolean pieceInWay = false;
		char[][] pieceLayout = new char [boardSize][boardSize];
		boolean[][] validMoves;
		
		for (int i = 0; i < pieceLayout.length; i++) {
			for (int j = 0; j < pieceLayout[i].length; j++) {
				pieceLayout[i][j] = '0';
			}
		}
		pieceLayout[0][3] = pieceLayout[0][4] = pieceLayout[0][5] = pieceLayout[0][6] = pieceLayout[0][7] = pieceLayout[1][5] = 'b';
		pieceLayout[3][0] = pieceLayout[4][0] = pieceLayout[5][0] = pieceLayout[6][0] = pieceLayout[7][0] = pieceLayout[5][1] = 'b';
		pieceLayout[10][3] = pieceLayout[10][4] = pieceLayout[10][5] = pieceLayout[10][6] = pieceLayout[10][7] = pieceLayout[9][5] = 'b';
		pieceLayout[3][10] = pieceLayout[4][10] = pieceLayout[5][10] = pieceLayout[6][10] = pieceLayout[7][10] = pieceLayout[5][9] = 'b';
		pieceLayout[3][5] = pieceLayout[4][4] = pieceLayout[4][5] = pieceLayout[4][6] = pieceLayout[5][3] = pieceLayout[5][4] = 'w';
		pieceLayout[5][6] = pieceLayout[5][7] = pieceLayout[6][4] = pieceLayout[6][5] = pieceLayout[6][6] = pieceLayout[7][5] = 'w';
		pieceLayout[0][0] = pieceLayout[0][10] = pieceLayout[10][0] = pieceLayout[10][10] = pieceLayout[5][5] = 'c';
		pieceLayout[5][5] = 'k';
		
		for (int pieceType = 0; pieceType < 2; pieceType++){
			for (int row = 0; row<boardSize; row++ ) {
				for (int col = 0; col<boardSize; col++) {
					
					validMoves = GameLogic.getValidMoves(piece, row, col, pieceLayout);
					pieceInWay = false;
					
					for (int i = row-1; i >= 0 ; i--){
						if (pieceLayout[i][col] == 'b' || pieceLayout[i][col] == 'w' || (pieceLayout[i][col] == 'c' && piece != 'k') || pieceLayout[i][col] == 'k' || pieceInWay == true) {
							assertEquals(validMoves[i][col], false);
							pieceInWay = true;
						} else {
							assertEquals(validMoves[i][col], true);
						}
						
					}
				}
			}
			piece = 'k';
		}
	}
	
	/**
	 This test is to ensure an obstactle to the right of the piece being moved invalids all moves to locations beyond the obstacle. It tests all locations on the board with pieces set up in their initial locations.
	 
	 */
	@Test
	public void testObstacleBlocksEasternPath() {
		char piece = 'w';
		int boardSize = 11;
		boolean pieceInWay = false;
		char[][] pieceLayout = new char [boardSize][boardSize];
		boolean[][] validMoves;
		
		for (int i = 0; i < pieceLayout.length; i++) {
			for (int j = 0; j < pieceLayout[i].length; j++) {
				pieceLayout[i][j] = '0';
			}
		}
		pieceLayout[0][3] = pieceLayout[0][4] = pieceLayout[0][5] = pieceLayout[0][6] = pieceLayout[0][7] = pieceLayout[1][5] = 'b';
		pieceLayout[3][0] = pieceLayout[4][0] = pieceLayout[5][0] = pieceLayout[6][0] = pieceLayout[7][0] = pieceLayout[5][1] = 'b';
		pieceLayout[10][3] = pieceLayout[10][4] = pieceLayout[10][5] = pieceLayout[10][6] = pieceLayout[10][7] = pieceLayout[9][5] = 'b';
		pieceLayout[3][10] = pieceLayout[4][10] = pieceLayout[5][10] = pieceLayout[6][10] = pieceLayout[7][10] = pieceLayout[5][9] = 'b';
		pieceLayout[3][5] = pieceLayout[4][4] = pieceLayout[4][5] = pieceLayout[4][6] = pieceLayout[5][3] = pieceLayout[5][4] = 'w';
		pieceLayout[5][6] = pieceLayout[5][7] = pieceLayout[6][4] = pieceLayout[6][5] = pieceLayout[6][6] = pieceLayout[7][5] = 'w';
		pieceLayout[0][0] = pieceLayout[0][10] = pieceLayout[10][0] = pieceLayout[10][10] = pieceLayout[5][5] = 'c';
		pieceLayout[5][5] = 'k';
		
		for (int pieceType = 0; pieceType < 2; pieceType++){
			for (int row = 0; row<boardSize; row++ ) {
				for (int col = 0; col<boardSize; col++) {
					
					validMoves = GameLogic.getValidMoves(piece, row, col, pieceLayout);
					pieceInWay = false;
					
					for (int i = col+1; i < boardSize ; i++){
						if (pieceLayout[row][i] == 'b' || pieceLayout[row][i] == 'w' || (pieceLayout[row][i] == 'c' && piece != 'k') || pieceLayout[row][i] == 'k' || pieceInWay == true) {
							assertEquals(validMoves[row][i], false);
							pieceInWay = true;
						} else {
							assertEquals(validMoves[row][i], true);
						}
					}
				}
			}
			piece = 'k';
		}
	}
	/**
	 This test is to ensure an obstactle to the left of the piece being moved invalids all moves to locations beyond the obstacle. It tests all locations on the board with pieces set up in their initial locations.
	 */
	@Test
	public void testObstacleBlocksWesternPath() {
		char piece = 'w';
		int boardSize = 11;
		boolean pieceInWay = false;
		char[][] pieceLayout = new char [boardSize][boardSize];
		boolean[][] validMoves;
		
		for (int i = 0; i < pieceLayout.length; i++) {
			for (int j = 0; j < pieceLayout[i].length; j++) {
				pieceLayout[i][j] = '0';
			}
		}
		pieceLayout[0][3] = pieceLayout[0][4] = pieceLayout[0][5] = pieceLayout[0][6] = pieceLayout[0][7] = pieceLayout[1][5] = 'b';
		pieceLayout[3][0] = pieceLayout[4][0] = pieceLayout[5][0] = pieceLayout[6][0] = pieceLayout[7][0] = pieceLayout[5][1] = 'b';
		pieceLayout[10][3] = pieceLayout[10][4] = pieceLayout[10][5] = pieceLayout[10][6] = pieceLayout[10][7] = pieceLayout[9][5] = 'b';
		pieceLayout[3][10] = pieceLayout[4][10] = pieceLayout[5][10] = pieceLayout[6][10] = pieceLayout[7][10] = pieceLayout[5][9] = 'b';
		pieceLayout[3][5] = pieceLayout[4][4] = pieceLayout[4][5] = pieceLayout[4][6] = pieceLayout[5][3] = pieceLayout[5][4] = 'w';
		pieceLayout[5][6] = pieceLayout[5][7] = pieceLayout[6][4] = pieceLayout[6][5] = pieceLayout[6][6] = pieceLayout[7][5] = 'w';
		pieceLayout[0][0] = pieceLayout[0][10] = pieceLayout[10][0] = pieceLayout[10][10] = pieceLayout[5][5] = 'c';
		pieceLayout[5][5] = 'k';
		
		for (int pieceType = 0; pieceType < 2; pieceType++){
			for (int row = 0; row<boardSize; row++ ) {
				for (int col = 0; col<boardSize; col++) {
					
					validMoves = GameLogic.getValidMoves(piece, row, col, pieceLayout);
					pieceInWay = false;
					
					for (int i = col-1; i >= 0 ; i--){
						if (pieceLayout[row][i] == 'b' || pieceLayout[row][i] == 'w' || (pieceLayout[row][i] == 'c' && piece != 'k') || pieceLayout[row][i] == 'k' || pieceInWay == true) {
							assertEquals(validMoves[row][i], false);
							pieceInWay = true;
						} else {
							assertEquals(validMoves[row][i], true);
						}
					}
				}
			}
			piece = 'k';
		}
	}
}

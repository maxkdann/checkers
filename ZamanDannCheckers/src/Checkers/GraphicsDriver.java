package Checkers;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * Checkers
 * 
 * @author Kiyan Zaman and Max Dann
 * @version 1.71
 */
public class GraphicsDriver extends Application {
	// set the dimensions of the tiles
	public static final int TILE_SIZE = 80;
	public static final int WIDTH = 8;
	public static final int HEIGHT = 8;
	// create a board of tiles
	private Tile[][] board = new Tile[WIDTH][HEIGHT];
	// create groups to stores tiles as well as pieces
	private Group tileGroup = new Group();
	private Group pieceGroup = new Group();
	//at the start of the game no one has won
	public static boolean win = false;

	/**
	 * Creates pieces on the board
	 * 
	 * @return the pane for which the board is on
	 */
	private Parent createContent() {
		// create a pane based on the size of the board
		Pane root = new Pane();
		root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
		// add the tiles and pieces to that pane
		root.getChildren().addAll(tileGroup, pieceGroup);

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				// create white tiles when the x and y coordinates add to be even
				// opposite for the black tiles
				Tile tile = new Tile((x + y) % 2 == 0, x, y);
				// add the tiles to the board
				board[x][y] = tile;
				// add tiles to the tile group
				tileGroup.getChildren().add(tile);
				// initially make a blank piece
				Piece piece = null;
				// make blue pieces on top half of the board
				if (y <= 2 && (x + y) % 2 != 0) {
					piece = makePiece(PieceType.BLUE, x, y);
				}
				// make red pieces on bottom half of the board
				if (y >= 5 && (x + y) % 2 != 0) {
					piece = makePiece(PieceType.RED, x, y);
				}
				// if there is a blank piece on a tile show it and change its colour
				if (piece != null) {
					tile.setPiece(piece);
					pieceGroup.getChildren().add(piece);
				}
			}
		}

		return root;
	}
	//a
	/**
	 * Tries three different move types
	 * 
	 * @param piece selected
	 * @param newX  new x location on board
	 * @param newY  new y location on board
	 * @return none if the move is illegal, normal if it is a valid move, or kill if
	 *         it is a jump
	 */
	private MoveResult tryMove(Piece piece, int newX, int newY) {
		// doesn't allow pieces to move off the board
		// doesn't move pieces if they aren't selected

		try {
			if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
				return new MoveResult(MoveType.NONE);
			}
		} catch (Exception e) {
			return new MoveResult(MoveType.NONE);
		}

		// store piece location before move
		int x0 = toBoard(piece.getOldX());
		int y0 = toBoard(piece.getOldY());

		//forced moves for kings
		if(piece.getType()==PieceType.BKING || piece.getType()==PieceType.RKING) {
			if(Math.abs(newX - x0) == 1) {
				if (y0>1) {
					if (x0 > 1) {
						if (board[x0 - 1][y0 - 1].hasPiece() && board[x0 - 1][y0 - 1].getPiece().getType() != piece.getType() && board[x0 - 2][y0 - 2].noPiece()) {
							return new MoveResult(MoveType.NONE);
						}
					}
					if (x0 < 6) {
						if (board[x0 + 1][y0 - 1].hasPiece() && board[x0 + 1][y0 - 1].getPiece().getType() != piece.getType() && board[x0+2][y0 - 2].noPiece()) {
							return new MoveResult(MoveType.NONE);
						}
					}
				}
				if (y0<6) {
					if (x0 > 1) {
						if (board[x0 - 1][y0 + 1].hasPiece() && board[x0 - 1][y0 + 1].getPiece().getType() != piece.getType() && board[x0 - 2][y0 + 2].noPiece()) {
							return new MoveResult(MoveType.NONE);
						}
					}
					if (x0 < 6) {
						if (board[x0 + 1][y0 + 1].hasPiece() && board[x0 + 1][y0 + 1].getPiece().getType() != piece.getType() && board[x0+2][y0 + 2].noPiece()) {
							return new MoveResult(MoveType.NONE);
						}
					}
				}
				Piece.turn.incTurn();
				return new MoveResult(MoveType.NORMAL);
			}else if(Math.abs(newX - x0) == 2) {
				int x1 = x0 + (newX - x0) / 2;
				int y1 = y0 + (newY - y0) / 2;
				if(piece.getType()==PieceType.RKING) {
					if(board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType() && board[x1][y1].getPiece().getType() != PieceType.RED) {
						Piece.turn.incTurn();
						return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
					}
				}else if(piece.getType()==PieceType.BKING){
					if(board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType() && board[x1][y1].getPiece().getType() != PieceType.BLUE) {
						Piece.turn.incTurn();
						return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
					}
				}
			}
		}else if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {

			// This if() tree forces your move if you have the option to kill
			if (newY > y0 && newY < 7) {
				if (newX > x0 && x0 > 2) {
					if (board[newX - 2][newY].hasPiece() && board[newX - 2][newY].getPiece().getType() != piece.getType() && board[newX - 3][newY + 1].noPiece()) {
						return new MoveResult(MoveType.NONE);
					}
				}
				if (newX < x0 && x0 < 5) {
					if (board[newX + 2][newY].hasPiece() && board[newX + 2][newY].getPiece().getType() != piece.getType() && board[newX + 3][newY + 1].noPiece()) {
						return new MoveResult(MoveType.NONE);
					}
				}
			}
			if (newY < y0 && newY > 0) {
				if (newX > x0 && x0 > 2) {
					if (board[newX - 2][newY].hasPiece() && board[newX - 2][newY].getPiece().getType() != piece.getType() && board[newX - 3][newY - 1].noPiece()) {
						return new MoveResult(MoveType.NONE);
					}
				}

				if (newX < x0 && x0 < 5) {
					if (board[newX + 2][newY].hasPiece() && board[newX + 2][newY].getPiece().getType() != piece.getType() && board[newX + 3][newY - 1].noPiece()) {
						return new MoveResult(MoveType.NONE);
					}
				}

			}

			// if all above booleans return false the move is approved
			Piece.turn.incTurn();
			return new MoveResult(MoveType.NORMAL);
		} else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

			int x1 = x0 + (newX - x0) / 2;
			int y1 = y0 + (newY - y0) / 2;
			// jump move
			if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
				Piece.turn.incTurn();
				return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
			}
		}

		return new MoveResult(MoveType.NONE);
	}
	
	/** 
	 * Checks if a multijump opportunity is there and resets the turn by 1 if it is.
	 * @param piece selected piece
	 * @param newX new x location on board
	 * @param newY new y location on board
	 * @return whether a multijump occurred 
	 */
	private boolean tryMultiJump(Piece piece, int newX, int newY) {
		//stores turn
		int turn = Piece.turn.getTurn();		
		//don't look close this method
		if(piece.getType() == PieceType.RED) {
			if(newX<6 && newY>1) {
				if(board[newX+2][newY-2].noPiece() && board[newX+1][newY-1].hasPiece() && board[newX+1][newY-1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
			if(newX>1 && newY>1) {
				if(board[newX-2][newY-2].noPiece() && board[newX-1][newY-1].hasPiece() && board[newX-1][newY-1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
		}else if(piece.getType() == PieceType.BLUE) {
			if(newX<6 && newY<6) {
				if(board[newX+2][newY+2].noPiece() && board[newX+1][newY+1].hasPiece() && board[newX+1][newY+1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
			
			if(newX>1 && newY<6) {
				if(board[newX-2][newY+2].noPiece() && board[newX-1][newY+1].hasPiece() && board[newX-1][newY+1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
		}else if(piece.getType() == PieceType.RKING) {
			if(newX<6 && newY>1) {
				if(board[newX+2][newY-2].noPiece() && board[newX+1][newY-1].hasPiece() && board[newX+1][newY-1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
			if(newX>1 && newY>1) {
				if(board[newX-2][newY-2].noPiece() && board[newX-1][newY-1].hasPiece() && board[newX-1][newY-1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
			if(newX<6 && newY<6) {
				if(board[newX+2][newY+2].noPiece() && board[newX+1][newY+1].hasPiece() && board[newX+1][newY+1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
			
			if(newX>1 && newY<6) {
				if(board[newX-2][newY+2].noPiece() && board[newX-1][newY+1].hasPiece() && board[newX-1][newY+1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
		}else if(piece.getType() == PieceType.BKING) {
			if(newX<6 && newY>1) {
				if(board[newX+2][newY-2].noPiece() && board[newX+1][newY-1].hasPiece() && board[newX+1][newY-1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
			if(newX>1 && newY>1) {
				if(board[newX-2][newY-2].noPiece() && board[newX-1][newY-1].hasPiece() && board[newX-1][newY-1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
			if(newX<6 && newY<6) {
				if(board[newX+2][newY+2].noPiece() && board[newX+1][newY+1].hasPiece() && board[newX+1][newY+1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
		
			if(newX>1 && newY<6) {
				if(board[newX-2][newY+2].noPiece() && board[newX-1][newY+1].hasPiece() && board[newX-1][newY+1].getPiece().getType()!=board[newX][newY].getPiece().getType()) {
					Piece.turn.setTurn(turn-1);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * converts pixels to board locations
	 * 
	 * @param pixel
	 * @return board location
	 */
	private int toBoard(double pixel) {
		return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
	}

	@Override
	/**
	 * Graphics driver
	 */
	public void start(Stage primaryStage) throws Exception {
		Scene scene = welcome(primaryStage, 640, 640);
		primaryStage.setTitle("Welcome to Checkers");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	/**
	 * Checks for whether blue has won
	 * 
	 * @return true or false
	 */
	private boolean checkBlueWin() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j].hasPiece() && board[i][j].getPiece().getType() == PieceType.RED) {
					return false;
				}
				if (board[i][j].hasPiece() && board[i][j].getPiece().getType() == PieceType.RKING) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks for whether red has won
	 * 
	 * @return true or false
	 */
	private boolean checkRedWin() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j].hasPiece() && board[i][j].getPiece().getType() == PieceType.BLUE) {
					return false;
				}
				if (board[i][j].hasPiece() && board[i][j].getPiece().getType() == PieceType.BKING) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * makes and deletes pieces
	 * 
	 * @param type
	 * @param x    location
	 * @param y    location
	 * @return piece after move
	 **/
	private Piece makePiece(PieceType type, int x, int y) {
		// create a piece object
		Piece piece = new Piece(type, x, y);
		// event handler for when piece is placed
		piece.setOnMouseReleased(e -> {
			// get new location of piece
			int newX = toBoard(piece.getLayoutX());
			int newY = toBoard(piece.getLayoutY());
			

			// try to move the piece there
			MoveResult result = tryMove(piece, newX, newY);

			// store old piece location in case the move is invalid
			int x0 = toBoard(piece.getOldX());
			int y0 = toBoard(piece.getOldY());

			switch (result.getType()) {
			case NONE:
				// abort the move if it is illegal
				piece.abortMove();
				break;
			case NORMAL:
				// move the piece to new location and remove it from the old location
				piece.move(newX, newY);
				board[x0][y0].setPiece(null);
				board[newX][newY].setPiece(piece);
				if(newY==0&&piece.getType()==PieceType.RED) {
					piece.setRKing();
					piece.changeColorRKING();
				}
				if(newY==7&&piece.getType()==PieceType.BLUE) {
					piece.setBKing();
					piece.changeColorBKING();
				}
				
				break;
			case KILL:
				// move the piece as well as delete the opponent's piece
				piece.move(newX, newY);
				board[x0][y0].setPiece(null);
				board[newX][newY].setPiece(piece);
				// opponent's piece to be deleted
				Piece otherPiece = result.getPiece();
				board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
				pieceGroup.getChildren().remove(otherPiece);
				//checks MultiJump
				tryMultiJump(piece, newX, newY);
				// checks if either team has won
				if (checkRedWin()) {

					winScreen.display("Red");

				} else if (checkBlueWin()) {

					winScreen.display("Blue");
				}
				if(newY==0&&piece.getType()==PieceType.RED) {
					piece.setRKing();
					piece.changeColorRKING();
				}
				
				if(newY==7&&piece.getType()==PieceType.BLUE) {
					piece.setBKing();
					piece.changeColorBKING();
				}

				break;

			}
		});
		
		return piece;
	}

	/**
	 * Welcome menu scene, gets user's choice
	 * 
	 * @param window (stage)
	 * @param width  of the stage
	 * @param height of the stage
	 * @return the scene
	 */
	public Scene welcome(Stage window, double width, double height) {
		final Image titleScreen = new Image("asset.jpg"); // title screen image
		final ImageView flashScreen_node = new ImageView();
		flashScreen_node.setImage(titleScreen); // set the image of the title screen
		window.getIcons().add(titleScreen); // stage icon
		Label label = new Label("Please choose one of the following: ");
		HBox hbox = new HBox();
		Button button2 = new Button("Start");

		button2.setOnAction(e -> {
			boolean result = ConfirmBox.display("Verify", "Are you sure you're ready to start?");
			if (result) {
				Scene scene = new Scene(createContent());
				window.setTitle("Welcome to Checkers");
				window.setScene(scene);
				window.show();
			}
		});
		HBox buttons = new HBox();
		buttons.getChildren().addAll(button2);
		buttons.setAlignment(Pos.CENTER);
		VBox layout = new VBox();
		layout.getChildren().addAll(label, buttons);
		layout.setAlignment(Pos.CENTER);
		StackPane root = new StackPane();
		root.getChildren().addAll(flashScreen_node, layout);
		Scene scene = new Scene(root, width, height, Color.BLACK);
		return scene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}

package Checkers;

import static Checkers.GraphicsDriver.TILE_SIZE;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;


public class Piece extends StackPane {
	//Creates turn counter  object
	public static Turn turn = new Turn();
	//Creates ellipse object for pieces
	Ellipse ellipse = new Ellipse(TILE_SIZE * 0.25, TILE_SIZE * 0.25);

	private PieceType type;
	// create variables to store the location of the mouse when it clicks a piece
	private double mouseX, mouseY;
	// create variables to store the old location of a piece if it makes an illegal
	// move to return it there
	private double oldX, oldY;

	/**
	 * returns whether the piece is red or white
	 * 
	 * @return type of piece
	 */
	public PieceType getType() {
		return type;
	}
	//a
	/**
	 * get's the x position of the piece before it was moved
	 * 
	 * @return old x position of the piece
	 */
	public double getOldX() {
		return oldX;
	}

	/**
	 * get's the y position of the piece before it was moved
	 * 
	 * @return old y position of the piece
	 */
	public double getOldY() {
		return oldY;
	}
	
	
	/**
	 * make a default piece
	 * 
	 * @param type red or white
	 * @param x    location on board
	 * @param y    location on board
	 */
	public Piece(PieceType type, int x, int y) {
		this.type = type;
		// put all pieces on separate tiles
		move(x, y);
		// Fills ellipses red or blue based on piecetype
		ellipse.setFill(type == PieceType.RED ? Color.valueOf("#c40003") : Color.valueOf("#0000ff"));
		// outline in black
		ellipse.setStroke(Color.BLACK);
		ellipse.setStrokeWidth(TILE_SIZE * 0.03);
		// set the location of the piece
		ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.25 * 2) / 2);
		ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.25 * 2) / 2);

		getChildren().addAll(ellipse);	
		// event handler for when a piece is clicked on
		setOnMousePressed(e -> {
			mouseX = e.getSceneX();
			mouseY = e.getSceneY();
		});
		//action event to move piece as well as logic for turns 
		setOnMouseDragged(e -> {
			
			if(turn.getTurn()%2!=0 && type == PieceType.RED) {
				relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
			}else if(turn.getTurn()%2==0 && type == PieceType.BLUE) {
				relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
			}
			
			

		});

	}

	/**
	 * moves the pieces to starting tiles
	 * 
	 * @param x location on board
	 * @param y location on board
	 */
	public void move(int x, int y) {
		oldX = x * TILE_SIZE;
		oldY = y * TILE_SIZE;
		relocate(oldX, oldY);
	}
	
	public void disable() {
		ellipse.setDisable(true);
	}
	
	/**
	 * Changes piece color to dark red if it's a red king
	 */
	public void changeColorRKING() {
		ellipse.setFill(Color.DARKRED);
	}
	
	/**
	 * Changes piece color to dark blue if it's a blue king
	 */
	public void changeColorBKING() {
		ellipse.setFill(Color.DARKBLUE);
	}


	/**
	 * brings a piece back to where it was before the move if the move is illegal
	 */
	public void abortMove() {
		relocate(oldX, oldY);
	}
	
	public void setRKing() {
		type = PieceType.RKING;
	}
	
	public void setBKing() {
		type = PieceType.BKING;
	}
}

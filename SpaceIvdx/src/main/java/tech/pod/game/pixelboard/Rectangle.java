package tech.pod.game.pixelboard;

/**<b>Permet de definir la taille et la position d'un objet sur le plan</b>
 * @author alexandrecremieux
 */
public class Rectangle {
	int wid, len, x, y;

	//Constructor
	/**
	 * @param x position en abscisse
	 * @param y position en ordonnee
	 * @param wid largeur du rectangle
	 * @param len hauteur ou longueur de l'objet
	 */
	Rectangle(int x, int y, int wid, int len){
		this.x = x;
		this.y = y;
		this.wid = wid;
		this.len = len;
	}

	public static boolean collision(Rectangle aRec, Rectangle bRec){
		boolean inWid = aRec.x <= bRec.x && aRec.x + aRec.wid >= bRec.x;
		inWid = inWid || aRec.x >= bRec.x && aRec.x <= bRec.x + bRec.wid;
		boolean inHig = aRec.y <= bRec.y && aRec.y + aRec.len >= bRec.y;
		inHig = inHig || aRec.y >= bRec.y && aRec.y  <= bRec.y + bRec.wid;
		return inWid && inHig;
	}

}

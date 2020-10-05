/**<b>Missile d'un enemi tireur ou d'un vaisseau ami</b>
 * @author alexandrecremieux
 */

public class Missile extends GameObject{
	
	private int gameId = 3;
	
	//Constructeur
	/**<b>Reprend le constructeur de super</b>
	 * <p>Couleur jaune par defaut</p>
	 * <p>{@link GameObject#GameObject(int, int, int, int, boolean, boolean, boolean)}</p>
	 * @param x abscisse
	 * @param y ordonnee
	 * @param wid largeur de l'image
	 * @param hig hauteur de l'image
	 */
	public Missile (int x, int y, int wid, int hig){
		super(x, y, wid, hig, true, true, false);
		int[] temp = new int[wid*hig];
		for (int i = 0; i < temp.length; i++){
			temp[i] = ImageUtil.computePixel(255, 255, 255, 0);
		}
		this.img = new Image(wid, hig, temp);
		this.gameId = GameObject.initObjectGameId("Missile");
	}
	
	//Accessor
	
	public int getGameId(){
		return this.gameId;
	}
	
	//Static method
	public void move(Vector vector){
		this.rec.y += vector.y;
	}
}

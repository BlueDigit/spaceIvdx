	
/**<b>La classe craft represente un vaisseau qui sera pilote par l'utilisateur</b>
 * <p>Herite de GameObject {@link GameObject#GameObject(int, int, int, int, boolean, boolean, boolean)}</p> 
 * @author alexandrecremieux
 */

public class Craft extends GameObject {

	//private int gameId = 2;
	private int gameId;
	private long shootTime = -1;
	private long lastShTm = 0;
	
	//Constructor
	/**<b>Reprise du constructeur de super</b>
	 * @param x abscisse
	 * @param y ordonnee
	 * @param wid largeur de l'image formant le sprite a l'ecran
	 * @param hig hauteur de l'image formant le sprite a l'ecran
	 * @param isShooter tire ou pas
	 */
	public Craft(int x, int y, int wid, int hig, boolean isShooter){
		super(x, y, wid, hig, true, true, isShooter);
		this.gameId = GameObject.initObjectGameId("Craft");
	}
	
	public Craft(int x, int y, int wid, int hig, boolean isShooter, int[] sprite){
		super(x, y, wid, hig, true, true, isShooter);
		this.gameId = GameObject.initObjectGameId("Craft");
		this.rec = new Rectangle(x, y, wid, hig);
		this.img.setSprite(new Image(wid, hig, sprite));
	}
	
	//Accessor
	/**<b>Retourne l'identifiant de l'instance de l'objet dans le jeu</b>
	 */
	public int getGameId(){
		return this.gameId;
	}
	
	/**<b>Redefini le temps minimum entre deux tirs en ms</b>
	 * <p>Par defaut le temps de tir est a zero ms</p>
	 * @param shootTime temps de tir
	 */
	public void setShootTime(long shootTime){
		this.shootTime = shootTime;
	}
	
	//Static method
	/**<b>Permet de modifier la position de l'objet sur le plan</b>
	 * @see Vector#Vector(int, int)
	 * @param vector contient le deplacement en absisse et en ordonnee
	 */
	public void move(Vector vector){
		this.rec.x += vector.x;
		this.rec.y += vector.y;
	}
	
	//Methode d'instance
	/**<b>Tir de missile</b>
	 * @see Missile#Missile(int, int, int, int)
	 * @see Craft#setShootTime(long)
	 */
	public Missile shoot(){
		Missile ms = null;
		if (System.currentTimeMillis() - lastShTm >= shootTime && this.shooter){
			ms = new Missile (
					this.rec.x + this.rec.wid / 2  - this.rec.wid / (this.rec.wid / 10), 
					this.rec.y - 10,
					this.rec.wid / (this.rec.wid / 10), 
					this.rec.len / (this.rec.len / 10));
			this.lastShTm = System.currentTimeMillis();
		}
		return ms;
	}
	
}

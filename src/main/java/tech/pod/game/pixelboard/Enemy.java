package tech.pod.game.pixelboard;

/**<b>La classe craft représente un vaisseau qui sera pilote par l'ordinateur</b>
 * <p>Herite de GameObject {@link GameObject#GameObject(int, int, int, int, boolean, boolean, boolean)}</p>
 * @author alexandrecremieux
 */
public class Enemy extends GameObject{

	private long shootTime = -1;
	private long lastShTm = 0;
	private int imgIdx = 0;
	private int gameId;
	private Color[] imgs;
	private boolean isMul = false;

	//Constructor
	/**<b>Reprise du constructeur de super</b>
	 * @param x abscisse
	 * @param y ordonnee
	 * @param wid largeur de l'image formant le sprite a l'ecran
	 * @param hig hauteur de l'image formant le sprite a l'ecran
	 * @param isShooter tireur ?
	 */
	public Enemy (int x, int y, int wid, int hig, boolean isShooter){
		super(x, y, wid, hig, true, true, isShooter);
	}

	/**<b>Reprise du constructeur de super avec ajout d'une image</b>
	 * @param x abscisse
	 * @param y ordonnee
	 * @param wid largeur de l'image formant le sprite a l'ecran
	 * @param hig hauteur de l'image formant le sprite a l'ecran
	 * @param isShooter tireur ?
	 * @param sprites tableau de pixels passe en parametre qui ecrase celui du super
	 */
	public Enemy (int x, int y, int wid, int hig, boolean isShooter, int[][] sprites){
		super(x, y, wid, hig, true, true, isShooter);
		if (sprites != null && sprites[1].length > 0){
			imgs = new Color[sprites.length];
			for (int i = 0; i < imgs.length; i++){
				imgs[i] = new Color(this.rec.wid, this.rec.len, sprites[i]);
			}
			this.img = this.imgs[0];
			this.isMul = true;
		} else {
			this.isMul = false;
		}
		this.gameId = initObjectGameId("Enemy");
	}

	//Accessor
	/**<b>Retourne l'identifiant de la classe de l'objet dans le jeu</b>
	 * <p>Le comportement individuel d'un ennemi est celui de sa classe</p>
	 */
	public int getGameId(){
		return this.gameId;
	}

	/**<b>Redefini le temps minimum entre deux tirs en ms</b>
	 * <p>Par defaut le temps de tir est a zero ms</p>
	 * @param shootTime termps de tir
	 */
	public void setShootTime(long shootTime){
		this.shootTime = shootTime;
	}

	//Methode Static de classe
	/**<b>Retourne l'identifiant de la classe de l'objet dans le jeu</b>
	 * L'identifiant est demandé à la classe GameObject
	 *@return Retourne l'identifiant de la classe pour ce type d'enemy
	 */
	public static int classId(){
		return initObjectGameId("Enemy");
	}

	//Methode d'instance
	/**<b>Retourne l'identifiant de la classe de l'objet dans le jeu</b>
	 */
	/**<b>Tir de missile</b>
	 * @see Missile#Missile(int, int, int, int)
	 * @see Craft#setShootTime(long)
	 */
	public Missile shoot(){
		Missile ms = null;
		if (System.currentTimeMillis() - lastShTm >= shootTime && this.shooter){
			 ms = new Missile (
					this.rec.x + this.rec.wid / 2  - this.rec.wid / 8,
					this.rec.y + this.rec.len + 5,
					this.rec.wid / 4,
					this.rec.len / 2);
			 this.lastShTm = System.currentTimeMillis();
		}
		return ms;
	}

	/**<b>Tir de missile</b>
	 * @see Missile#Missile(int, int, int, int)
	 * @see Craft#setShootTime(long)
	 */
	public void move(Vector vector){
		if ((vector.x != 0 || vector.y != 0) && isMul){
			//this.img.setSprite(this.imgs[imgIdx]);
			this.img = this.imgs[imgIdx];
			imgIdx++;
			if (imgIdx >= this.imgs.length)
				imgIdx = 0;
		}
		this.rec.x += vector.x;
		this.rec.y += vector.y;
	}

}

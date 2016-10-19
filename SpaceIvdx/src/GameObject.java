	

/**<h1>GameObject represente la classe principale du modele</h1>
 *<br>
	 * <ul>
	 * <li>Un element du jeu a l'ecran est forcement un game object</li>
	 * <li>La position de chaque GO est contenue dans une instance de Rectangle</li>
	 * <li>La classe peut implementer des interfaces pouvant faire reference a
	 * des comportements communs a plusieurs sous-classe mais dont les mecanismes
	 * peuvent varier (pattern strategie)</li>
	 * <li>Les interfaces specifiques a certains types de sous classes sont 
	 * implementees directement dans les sous-classes concernees et non ici</li>
	 *</ul>
	 *@author : Alexandre Crémieux,
	 *@version : 1.0
	 * 
	 */
public abstract class GameObject implements Moves{
	
	protected Image img; protected Rectangle rec;
	protected boolean isMoving, isVisible, shooter;
	private static int nbGo = 0; 
	private int gameId = 0;
	private boolean isColsn;
	
	//Constructeur
	public GameObject(int x, int y, int wid, int len, boolean isMoving, boolean isVisible, boolean isShooter){
		
		//Remplissage du tableau en jaune par defaut
		//Creation de l'image
		
		nbGo += 1;
		int[] temp = new int[wid*len];
		for (int i = 0; i < temp.length; i++){
			temp[i] = ImageUtil.computePixel(255, 255, 255, 0);
		}
		this.img = new Image(wid, len, temp);
		this.rec = new Rectangle(x, y, wid, len);
		this.isMoving = isMoving;
		this.isVisible = isVisible;
		this.shooter = isShooter;
		this.isColsn = false;

	}
	
	//Mutator
	/**<b>Met a jour la visibilite de l'objet</b>
	 * @param is boolean à true pour active la visibilite, false pour l'inverse
	 */
	public void setVisible(boolean is){
		this.isVisible = is;
	}
	
	/**<b>Definie si un objet est en colision</b>
	 * @param is boolean
	 */
	public void setCollision(boolean is){
		this.isColsn = is;
	}
	
	//Access
	/**<b>Retourne le Rectangle de position de l'objet</b>
	 * @return Rectangle
	 */
	
	public Rectangle getRectangle(){
		return this.rec;
	}
	
	/**<b>L'objet est-il visible ?</b>
	 * @return boolean
	 */
	public boolean isVisible(){
		return this.isVisible;
	}
	
	/**<b>L'objet est-il en collision ?</b>
	 * @return	boolean
	 */
	public boolean isCollision(){
		return this.isColsn;
	}
	
	/*
	public int getClassId(){
		return getGameId();
	}*/
	
	/**<b>Retourne l'id de jeu de l'objet</b>
	 * @return int Correspond a l'identifiant de l'objet dans le jeu
	 */
	public int getGameId(){
		return this.gameId;
	}
	
	/**<b>Retourne l'image de l'objet</b>
	 * <p>Retourne un pointeur vers l'adresse ou se situe l'image en memoire (dans le tas)</p>
	 * @return {@link Image#Image(int, int, int[])}
	 */
	public Image getImage(){
		return this.img;
	}
	
	//Methodes d'instance
	/**<b>Retourne une copie du rectangle</b>
	 * <p>Par securite, il est retourne un pointeur vers une copie en memoire
	 * de l'objet plutot que vers le rectangle en lui meme. 
	 * <br>Seule une instance peut modifier elle meme son Rectangle()</p>
	 * @return new Rectangle()
	 */
	public Rectangle CopyRectangle(){
		return new Rectangle(this.rec.x, this.rec.y, this.rec.wid, this.rec.wid);
	}
	
	/**<b>Calcule si l'objet est en collision avec un autre rectangle</b>
	 * @param oRec Rectangle() {@link Rectangle#Rectangle(int, int, int, int)}
	 * @return boolean
	 */
	public boolean collision(Rectangle oRec){
		/*boolean inWid = this.rec.x <= oRec.x && this.rec.x + this.rec.wid >= oRec.x;
		inWid = inWid || this.rec.x >= oRec.x && this.rec.x <= oRec.x + oRec.wid;
		boolean inHig = this.rec.y <= oRec.y && this.rec.y + this.rec.len >= oRec.y;
		inHig = inHig || this.rec.y >= oRec.y && this.rec.y  <= oRec.y + oRec.wid;
		return inWid && inHig;
		*/
		return Rectangle.collision(this.rec, oRec);
	}
	
	/**<b>Permet de savoir si les pixels d'un objet recouvrent celui-ci</b>
	 * <p>Parcourt tous les pixels des images des deux objets et les compare</p>
	 * @param other Autre image à tester
	 * @return recouvre ou pas
	 */
	public boolean isCovering(GameObject other){
		boolean output = false;
		if (this.collision(other.getRectangle())){
			int maxWid = Math.min(other.getRectangle().x + other.getRectangle().wid, this.rec.x + this.rec.wid);
			int minWid = Math.max(other.rec.x, this.rec.x);
			int maxHig = Math.min(other.getRectangle().y + other.getRectangle().len, this.rec.y + this.rec.len);
			int minHig = Math.max(other.rec.y, this.rec.y);
			for (int i = minHig; i < maxHig; i++){
				for (int j = minWid; j < maxWid; j++){
					boolean myBlindPx = this.img.getPx(j - this.rec.x, i - this.rec.y) == ImageUtil.computePixel(0, 0, 0, 0);
					boolean oBlindPx = other.getImage().getPx(j - other.rec.x, i - other.rec.y) == ImageUtil.computePixel(0, 0, 0, 0);
					if (!myBlindPx && !oBlindPx) return true;
				}
			}
		}
		return output;
	}
	
	/*<b>Calcule si l'objet est collision stricte avec un autre rectangle</b>
	 @param oRec Rectangle() {@link Rectangle#Rectangle(int, int, int, int)}
	 @return boolean
	 
	public boolean collisionStraight(Rectangle oRec){
		boolean inWid = this.rec.x < oRec.x && this.rec.x + this.rec.wid > oRec.x;
		inWid = inWid || this.rec.x > oRec.x && this.rec.x < oRec.x + oRec.wid;
		boolean inHig = this.rec.y < oRec.y && this.rec.y + this.rec.len > oRec.y;
		inHig = inHig || this.rec.y > oRec.y && this.rec.y  < oRec.y + oRec.wid;
		return inWid && inHig;
	}*/
	
	/**<b>Deplace le rectanle sur un plan cartesien</b>
	 * @param vector {@link Vector#Vector(int, int)}
	 */
	public void move(Vector vector){
		this.rec.x += vector.x;
		this.rec.y += vector.y;
	}
	
	/**<b>Tire de missile</b>
	 * <p>Par defaut, il n'y pas de missile et ceci provoque une retour a null</p>
	 * @return null
	 */
	public Missile shoot(){
		return null;
	}
	
	/**<b>L'image depasse-t-elle le cadre d'un rectangle ?</b>
	 * <p>Par exemple, le rectangle passe en parametre peut-etre l'image de fond
	 * Il peut aussi s'agir d'une image sur laquelle on souhaite rajouter l'image de cet objet</p>
	 * @param oRec Rectangle
	 */
	public boolean overBounded(Rectangle oRec){
		boolean output = false;
		if (this.rec.x + this.rec.wid > oRec.wid 
				|| this.rec.y + this.rec.len > oRec.len 
				|| this.rec.y <= oRec.y){
			output = true;
		}
		return output;
	}
	
	/**<b>Redefini la taille de l'image</b>
	 * @param wid nouvelle largeur
	 * @param len nouvelle hauteur
	 */
	public void setSize(int wid , int len){
		this.rec.wid = wid;
		this.rec.len = len;
		//Troncage du sprite
		//rajouter une exception
		this.img.truncate(this.rec.wid, this.rec.len);
	}
	
	//Method static de classe
	
	/**<b>Retourne le nombre d'instances de GameObject</b>
	 * @return int
	 */
	static int nbOfGameObject(){
		return nbGo;
	}
	
	/**<b>Permet de retourner l'id d'un objet du jeu</b>
	 * <p>Si l'appel est effectuer par un objet identifie, la fonction retourne l'id reserve correspondant.
	 * <br>Dans le cas contraire, la fonction s'appuie sur le nombre d'instances de l'objet GameObject
	 * pour calculer l'identifiant de l'appel</p>
	 * @param type type d'objet du jeu
	 * @return numero d'identifiant
	 */
	protected static int initObjectGameId(String type){
		if (type.equals("Enemy")){
			return 1;
		} else if (type.equals("Craft")){
			return 2;
		}
		return 2 + GameObject.nbOfGameObject();
	}
	
}

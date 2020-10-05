
public class Image {
	private int[] sprite;
	private int wid, hig, size;
	
	//Constructeur**********
	
	/**<b>Constructeur</b>
	 * <p>Definit le taille de l'image
	 * <br>Le contenu de l'image est passe en parametre sous forme d'un tableau de int[]
	 * <br>Lance une exception au cas ou l'image ne fait pas 1 pixel au moins</p>
	 * @param 	wid largeur de l'image
	 * @param 	hig hauteur de l'image
	 * @param 	image tableau de pixel composant l'image
	 * @exception 	ImageInitException
	 */
	public Image(int wid, int hig, int[] image){
		
		if (image.length == 0 || wid < 1 || hig < 1){
			throw new ImageInitException();
		}
		
		this.wid = wid;
		this.hig = hig;
		this.size = image.length;
		
		this.sprite = new int[this.size];
		for (int i = 0; i < this.size; i++){
			this.sprite[i] = image[i];
		}
	}
	
	//Mutateur**********
	
	/**<b>Positionne un pixel dans l'image en fonction de ses coordonnees</b>
	 * @param 	x position en abscisse
	 * @param 	y position en ordonnee
	 * @param 	val couleur du pixel
	 * @exception 	ImagePixelAccessException
	 */
	public void setPx (int x, int y, int val){
		int pos = (y * this.wid) + x;
		if (pos > sprite.length){
			throw new ImagePixelAccessException();
		}
		this.sprite[(y * this.wid) + x] = val;
	}
	
	/**<b>Positionne un pixel dans l'image en fonction de son adresse dans le tableau</b>
	 * @param 	i	adresse du pixel dans le tableau
	 * @param 	val couleur du pixel
	 * @exception 	ImagePixelAccessException
	 */
	public void setPx(int i, int val){
		if (i < 0 || i > this.sprite.length - 1){
			throw new ImagePixelAccessException();
		}
		this.sprite[i] = val;
	}
	
	/**<b>Redefini complement l'image en fonction d'une autre</b>
	 * @param another image externe qui servira de patron
	 */
	public void setSprite(Image another){
		this.sprite = another.getSpriteCopy();
		this.hig = another.getHig();
		this.wid = another.getWid();
	}
	
	//Accesseur**********
	
	/**<b>Renvoie un pixel en particulier en fonction de sa position</b>
	 * <p>Lance une exception si les coordonnees sortent de l'image</p>
	 * @param x position en abscisse
	 * @param y position en ordonnee
	 * @return Retourne un int correspondant a un pixel
	 * @exception ImagePixelAccessException
	 */
	public int getPx (int x, int y){
		try {
			return this.sprite[(y * this.wid) + x];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Taille du depassement : " + (this.cptPos(x, y) - this.size));
			throw new ImagePixelAccessException();
		}
	}
	
	/**<b>Renvoie un pixel en particulier en fonction de son adresse</b>
	 * <p>Lance une exception si l'adresse basee n'est pas dans l'image</p>
	 * @param 	i adresse dans le tableau d'image
	 * @return 	Retourne un int correspondant a un pixel
	 * @exception 	ImagePixelAccessException
	 */
	public int getPx(int i){
		if (i < 0 || i > this.sprite.length - 1){
			throw new ImagePixelAccessException();
		}
		return this.sprite[i];
	}
	
	/**<b>Retourne une copie du tableau formant l'image</b>
	 * <p>Par mesure de securite, l'adresse du tableau n'est jamais passe par pointeur
	 * <br>Pointer sur cette variable int[] dans le tas ne doit se faire que dans la classe
	 * <br>Seule la classe peut modifier directement ce tableau</p>
	 * @return int[] copie du sprite
	 */
	public int[] getSpriteCopy(){
		int[] temp = new int[this.size];
		for (int i = 0; i < this.size; i++){
			temp[i] = this.sprite[i];
		}
		return temp;
	}
	
	/**<b>Retourne la taille de l'image</b>
	 * @return int
	 */
	public int getSize(){
		return this.size;
	}
	
	/**<b>Retourne la largeur de l'image</b>
	 * @return int
	 */
	public int getWid(){
		return this.wid;
	}
	
	/**<b>Retourne la hauteur de l'image</b>
	 * @return	int
	 */
	public int getHig(){
		return this.hig;
	}
	
	//Méthodes d'instance
	/**<b>Ajoute une image par dessus l'image</b>
	 * <p>Dessine une image par dessus une autre
	 * <br>L'image est positionnee par dessus celle-ci en fonction d'une position
	 * <br>Si l'image depasse, un exception est levee</p>
	 * @param 	x abscisse 
	 * @param 	y ordonnee
	 * @param 	img Image a ajouter
	 * @exception 	ImageOverBoundedAdding
	 * @see 	#cptPos(int, int)
	 */
	public void add(int x, int y, Image img){
		
		if (this.cptPos(x, y) + img.getWid() > this.size || x < 0 || y < 0){
			System.out.println("x : " + x);
			System.out.println("y : " + y);
			System.out.println("len :  " + 
					(this.cptPos(x, y) + img.getWid() > this.size));
			throw new ImageOverBoundedAdding();
		}
		
		for (int i = 0; i < img.hig; i++){
			int pos = cptPos(x, y + i);
			for (int j = 0; j < img.wid; j++){
				if (img.getPx(j, i) != ImageUtil.computePixel(0, 0, 0, 0)){
					this.sprite[pos] = img.getPx(j, i);
				}
				pos++;
			}
		}
		
	}
	
	/**<b>Permet de tronquer l'image</b>
	 * <p>Redefini une nouvelle taille de l'image en partant du haut</p>
	 * @param wid nouvelle largeur d'image
	 * @param len nouvelle hauteur d'image
	 */
	public void truncate(int wid, int len){
		//Permet de tronquer l'image
		//Rajouter une exception
		if (this.wid < wid || this.hig < hig){
			throw new ImageOverSizedTruncation();
		}
		int[] temp = new int[wid * len];
		for (int i = 0; i < len; i++){
			for (int j = 0; j < wid; j++){
				temp[(i * len) + j] = this.getPx(this.cptPos(j, i));
			}
		}
		this.sprite = temp;
		this.wid = wid;
		this.hig = len;
		this.size = wid * len;
	}
	
	//Methode private
	/**<b>Calcule l'adresse basee du tableau d'une position sur le plan</b>
	 * <p>La methode est private afin qu'elle ne soit appellee que par le plan
	 * sur lequel le calcul est effectuee</p>
	 * @param 	x abscisse
	 * @param 	y ordonnee
	 * @return	int
	 */
	private int cptPos(int x, int y){
		return (this.wid * y) + x;
	}
	
}

class ImageInitException extends NegativeArraySizeException{
	private static final long serialVersionUID = 1830917179075716341L;

	ImageInitException(){
		System.out.println("Erreur sur les variables d'instance de l'image");
		this.printStackTrace();
	}
}

class ImagePixelAccessException extends ArrayIndexOutOfBoundsException{
	private static final long serialVersionUID = 996363894166397605L;

	ImagePixelAccessException(){
		System.out.println("Erreur à l'accés d'un pixel sur image " + this.getClass().getName());
		this.printStackTrace();
	}
}

class ImageOverBoundedAdding extends ArrayIndexOutOfBoundsException{
	private static final long serialVersionUID = 9073519076318702540L;

	ImageOverBoundedAdding(){
		System.out.println("Erreur à l'ajout d'image " + this.getClass().getName());
		this.printStackTrace();
	}
}

class ImageOverSizedTruncation extends ArrayIndexOutOfBoundsException{
	private static final long serialVersionUID = 1272179062073017824L;

	ImageOverSizedTruncation(){
		System.out.println("Erreur à l'ajout d'image " + this.getClass().getName());
		this.printStackTrace();
	}
}
package tech.pod.game.pixelboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

public class SpaceInvasion {

	//Game control
	private VectorQueue vectors;
	private Afficheur aff;
	static ArrayList<GameObject> gmObjs;
	private KeyListener gmKbLst;

	//Background
	private Color screen, buff;
	private int WID = 1280;
	private int HIG = 800;
	private Rectangle board;

	//Moves
	private static ArrayList<Moves> moves;

	//Craft
	private int[] craftSprite;
	private int craftWid, craftHig;
	private Craft craft;
	private Vector craftVec;
	private Rectangle craftRec;
	static final private long  CRAFTIME = 10;
	static final private int CRAFTMISSY = -5;
	static final private long MSCRAFTME = 500;

	//Enemies
	private int[][][] enmySprites;
	private Vector enmyVec;
	private Rectangle enmyRec;
	private int enmyRows = 3;
	private int enmyCol = 8;
	private static ArrayList<ArrayList<Enemy>> enemies;
	static final private long ENMYTIME = 500;
	static final private long ENMYSHOOTME = 2000;


	//Missiles
	private static ArrayList<Missile> enMsls;
	private static ArrayList<Missile> msls;
	static final private int ENMYMISSY = 5;
	static final private long ENMYMISTIME = 10;

	/**<h1>Constructeur</h1>
	 * <p>Initialise le fond, le vaisseau, le tableau d'ennemis, charge les images</p>
	 * <p>Lance la boucle</p>
	 * {@link #initBackGround()}, {@link #initCraft(int wid, int hig)}, {@link #loadEnemiesSrites()}, {@link #initEnemies()}, {@link #gameLoop()}
	 */
	SpaceInvasion(){
		boolean play = true;
		loadEnemiesSrites();
		loadCraftSprite();
		while(play){
			//Creation des objets
			initCollections();
			initCraft(70, 50);
			initEnemies();
			initBackGround();

			//Ouverture de la fenetre en premier plan
			if(aff == null){
				aff = new Afficheur(screen.getWid(), screen.getHig(),
											screen.getSpriteCopy());
				aff.addKeyListener(this.keyListener());
			}

			//Boucle du jeu
			int rep = 0;
			if (this.gameLoop()){
				rep = JOptionPane.showConfirmDialog(null,
						"Vous avez gagné. \n\n Souhaitez-vous rejouer ?",
						"Space Invdx", JOptionPane.YES_NO_OPTION);
			} else {
				rep = JOptionPane.showConfirmDialog(null,
						"Vous avez perdu. \n\n Souhaitez-vous rejouer ?",
						"Space Invdx", JOptionPane.YES_NO_OPTION);
			}

			freeCollections();

			if (rep == JOptionPane.NO_OPTION){ play = false; }
		}

		//Ferme la fenêtre et securise la zone memoire
		enmySprites = null;
		craftSprite = null;
		this.closeMe();

	}

	public void closeMe(){
		aff.removeKeyListener(gmKbLst);
		aff.setVisible(false);
		aff.dispose();
		aff.dispatchEvent(new WindowEvent(aff, WindowEvent.WINDOW_CLOSING));
	}

	//Methodes Private
	/**<b>Initilialise le fond</b>
	 * <p>Remplit un tableau de la taille de la fenetre en noir
	 * <br>Instancie l'image et le buffer
	 * @see		ImageUtil#computePixel(int, int, int, int)
	 * @see 	Color#Color(int, int, int[])
	 */
	private void initBackGround(){
		board = new Rectangle(0, 0, WID, HIG);

		int[] tab = new int[WID*HIG];
		for (int i = 0; i< tab.length; i++){
			tab[i] = ImageUtil.computePixel(255, 0, 0, 0);
		}

		screen = new Color(WID, HIG, tab);
		buff = new Color(screen.getWid(), screen.getHig(), screen.getSpriteCopy());
	}

	/**<b>Instancie les collections d'objets</b>
	 * <p>Cree de nouveaux ArrayList
	 * <br>Dans un contexte de production, la fonction permet
	 * un nouvel emplacement memoire pour manipuler les objets</p>
	 */
	private void initCollections(){
		gmObjs = new ArrayList<GameObject>();
		moves = new ArrayList<Moves>();
		craftVec = new Vector(0, 0);
		vectors = new VectorQueue(craftVec, CRAFTMISSY);
	}

	private void loadCraftSprite(){
		try{
			craftSprite = ImageUtil.readImage("Craft.png");
			craftWid = ImageUtil.getImageWidth("Craft.png");
			craftHig = ImageUtil.getImageHeight("Craft.png");
		} catch (IOException e) {
			System.out.println("L'image du vaisseau , n'a pas ete chargee");
			e.printStackTrace();
		}
	}

	/**<b>Initialise le vaisseau</b>
	 * <p>Instancie le vaisseau, ajoute le vecteur de mouvement correspondant</p>
	 * <p>Le vaisseau n'appartient pas au tableau principal des objets du jeu</p>
	 * @param wid largeur
	 * @param hig hauteur
	 * @see 	Craft#Craft(int, int, int, int, boolean)
	 * @see 	#addToVectors(Vector, int, long)
	 */
	private void initCraft(int wid, int hig){
		if (craftSprite == null){
			System.out.println("false");
			craftRec = new Rectangle(0, 0, wid, hig);
			craft = new Craft(WID / 2 - (craftRec.wid / 2),
					HIG - (craftRec.len + 1),
					craftRec.wid,
					craftRec.len, true);
		} else {
			craftRec = new Rectangle(0, 0, craftWid, craftHig);
			craft = new Craft(WID / 2 - (craftRec.wid / 2),
					HIG - (craftRec.len + 1),
					craftRec.wid,
					craftRec.len, true,
					craftSprite);
		}
		craft.setShootTime(MSCRAFTME);
		craftVec = new Vector(0, 0);
		moves.add(craft);
		//gameObjects.add(this.craft);
		addToVectors(craftVec, craft.getGameId(), CRAFTIME);
		msls = new ArrayList<Missile>();
	}

	/**<b>Chargement des images d'ennemis</b>
	 * <p>Trois types d'ennemis, trois images par type
	 * <br>Les images doivent etre dans le même dossier que SpaceInvasion.class
	 * <br>Plante le programme si les images ne sont pas chargees
	 * @see	IOException#IOException()
	 */
	private void loadEnemiesSrites(){
		enmySprites = new int[3][3][0];
		int ascii = (int)'A';
		try {
			for (int i = 0; i < enmySprites.length; i++){
				for (int j = 0; j < enmySprites[i].length; j++){
					enmySprites[i][j] = ImageUtil.readImage("Ennemy" + (i + 1) +
							(char)(ascii + j) + ".png");
				}
			}
		} catch (IOException e) {
			System.out.println("Les images des ennemis n'ont pas ete chargees");
			e.printStackTrace();
		}
	}

	/**<b>Initialise les ennemis</b>
	 * <p>Cree un ArrayList temporaire par colonne d'ennemis, cree un ennemi pour chaque cellule
	 * <br>Ajoute la colonne dans tableau d'ennemis principal du jeu
	 * <br>Ajoute chaque ennemi dans le tableau des objets mobiles
	 * <br>Ajoute un vecteur pour tous les ennemis</p>
	 * @see Enemy#Enemy(int, int, int, int, boolean, int[][])
	 * @see #addToVectors(Vector, int, long)
	 */
	private void initEnemies(){
		enmyVec = new Vector(0, 0);
		enmyRec = new Rectangle(0, 0, 80, 80);
		enemies = new ArrayList<ArrayList<Enemy>>();
		enMsls = new ArrayList<Missile>();
		long timeSur = 0;
		for (int i = 0; i < enmyCol; i++){
			ArrayList<Enemy> enmyCol = new ArrayList<Enemy>();
			for (int j = 0, k = 0; j < enmyRows; j++, k++){
				if (k >= enmySprites.length){
					k = 0;
				}
 				Enemy enmy = new Enemy(
						((enmyRec.wid / 2) * (i + 1)) + i * enmyRec.wid,
						((enmyRec.len / 2) * (j + 1)) + j * enmyRec.len ,
						enmyRec.wid, enmyRec.len, true, enmySprites[k]);
				enmy.setShootTime(ENMYSHOOTME + timeSur);
				timeSur += 100;
				enmyCol.add(enmy);
				moves.add(enmy);
				gmObjs.add((GameObject)enmy);
			}
			enemies.add(enmyCol);
		}

		//On plante le programme à la première initialisation si le vecteur existe deja
		addToVectors(new Vector(0, 0), Enemy.classId(), ENMYTIME);
	}

	/**<b>Ajoute un vecteur dans le tableau de vecteurs de mouvement du jeu</b>
	 * <p>Leve un une exception et crash le jeu si l'objet a déjà enregistré son vecteur
	 * <br>L'Exception plante le programme</p>
	 * @param 	vec Vector a jouter
	 * @param 	id identifiant de l'objet auquel appartient le vecteur
	 * @param 	time temps d'attente minimum en ms entre deux deplacement
	 * @see 	ReferencedElement#ReferencedElement(int)
	 */
	private void addToVectors(Vector vec, int id, long time){
		try {
			vectors.add(vec, id, time);
		} catch (ReferencedElement e){
			System.out.println("Vecteur déjà enregistré");
		}
	}

	/**<b>Calcule le vecteur de mouvement pour chaque ennemi</b>
	 * <p>Le deplacement vertical est assigne a 0 a chaque appel
	 * <br>Le deplacement horizontal est calcule si un des ennemis arrive a gauche ou a droite de l'ecran
	 * ou qu'il doive en repartir</p>
	 * @param 	preproc Le dernier vecteur utilise par le jeu pour les ennemis
	 * @return 	Retourne un nouveau vecteur qui sera le prochain vecteur calcule
	 */
	private Vector computeEnmyVec(Vector preproc){

		Vector output = preproc;
		output.y = 0;

		for (int i = 0; i < enemies.size(); i++){
			for (int j = 0; j < enemies.get(i).size(); j++){

				Enemy en = enemies.get(i).get(j);
				boolean isLeftBound = en.rec.x <= en.rec.wid / 2;
				boolean isLeft = preproc.x == - en.rec.wid / 2;
				boolean isRightBound = en.rec.x + en.rec.wid + en.rec.wid / 2 >= screen.getWid() - 1;
				boolean isRight = preproc.x == en.rec.wid / 2;

				if (isLeftBound && isLeft || isRightBound && isRight){
					output.y = en.rec.len / 3;
					output.x = 0;
					return output;
				} else if (isRightBound){
					output.x = - en.rec.wid / 2;
					return output;
				} else if (isLeftBound){
					output.x = en.rec.wid / 2;
					return output;
				}
			}
		}
		return output;
	}

	/**<b>Calcule le deplacement des objets</b>
	 * <p>Parcourt tout le tableau des objets mobiles et appelle leur vecteur respectif
	 * <br>Interrompt le processus si le vecteur n'existe pas pour un objet</p>
	 * <p>Si le vaisseau depace de l'image, sa position est recalculée</p>
	 * <p>Les objets qui ne doivent pas etre deplaces doivent etre retires d'Array List avant l'appel de
	 * fonction </p>
	 * @see VectorQueue#getVector(int)
	 * @see Moves#move(Vector)
	 * @see NotReferencedElement#NotReferencedElement(int)
	 * @throws NotReferencedElement
	 */
	private void moveGameObjects() throws NotReferencedElement{

		synchronized(moves){
			for (int i = 0; i < moves.size(); i++){
				moves.get(i).move(vectors.getVector(moves.get(i).getGameId()));
			}
		}

		//Remet en place le vaisseau si il depasse de l'image
		if (craft.rec.x < 0){
			craft.rec.x = 0;
		} else if (craft.rec.x + craft.rec.wid > board.wid){
			craft.rec.x = board.wid - craft.rec.wid;
		}

	}

	/**<b>Dessine les objets dans l'image principale du jeu</b>
	 * <p>Parcourt le tableau des objets du jeu
	 * <br>Gere la sortie d'un objet par le haut et par le bas de l'ecran
	 * <br>Dessine le vaisseau si il est toujours visible
	 * <p>Gestion de l'acces concurent a gmObjs et a chaque element du tableau pour gérer l'arrivee d'un nouveau missile</p>
	 * @see 	Color#add(int, int, Color)
	 * @see 	GameObject#setVisible(boolean)
	 */
	private void drawGameObject(){

 		synchronized(gmObjs){

 			if (craft.isVisible) screen.add(craft.rec.x, craft.rec.y, craft.img);

			for (int i = 0; i < gmObjs.size(); i++){
				GameObject go = gmObjs.get(i);
				synchronized(go){
					if ((go.overBounded(board)) && go.isVisible()){
						int newLen = go.rec.y + go.rec.len;
						int wid = board.wid - go.rec.x - go.rec.wid;
						int len = board.len - go.rec.y - go.rec.len;
						if (len >= 0 && wid >= 0 && go.rec.y > 0){
							go.setSize(wid, len);
						} else if (go.rec.y < 0 && 0 < newLen){
							go.setSize(go.rec.wid, newLen);
							go.rec.y = 1;
						} else {
							go.setVisible(false);
						}
					}

					if (go.isVisible) screen.add(go.rec.x, go.rec.y, go.getImage());
				}
			}
 		}


	}

	/**<b>Tire de missile depuis le vaisseau</b>
	 * <p>Le missille est cree puis ajoute dans les arraylist des GO, des mobiles,
	 * dans les missiles du vaisseau, un vecteur spécifique est cree</p>
	 * @see Craft#shoot()
	 * @see #addToVectors(Vector, int, long)
	 */
	private void craftShoot(){
		Missile ms = craft.shoot();
		if (ms != null){
			gmObjs.add(ms);
			moves.add(ms);
			msls.add(ms);
			this.addToVectors(new Vector(0, CRAFTMISSY), ms.getGameId(), CRAFTIME);
		}
	}

	/**<b>Tire de missile depuis le ennemis</b>
	 * <p>Recherche l'ennemie qui doit tirer : colonne au hasard, dernier de la colonne (le plus bas)
	 * <br>Le missille est cree puis ajoute dans les arraylist des GO, des mobiles,
	 * dans les missiles des ennemis, un vecteur spécifique est créé</p>
	 * <p>Si l'ennemi n'est plus visible il est retire et ne tire pas</p>
	 * @see Enemy#shoot()
	 * @see #addToVectors(Vector, int, long)
	 * @throws ReferencedElement
	 */
	private void enmyShoot() throws ReferencedElement{

		int rd = new Random().nextInt(enemies.size());

		if (enemies.get(rd).size() > 0){
			Enemy en = enemies.get(rd).get(enemies.get(rd).size() - 1);

			if (en.isVisible()){
				Missile ms = en.shoot();
				if (ms != null){
					gmObjs.add(ms);
					moves.add(ms);
					enMsls.add(ms);
					vectors.add(new Vector(0, ENMYMISSY),
								ms.getGameId(), ENMYMISTIME);
				}
			} else {
				enemies.get(rd).remove(enemies.get(rd).size() - 1);
			}
		}
		if (enemies.get(rd).size() == 0){
			enemies.remove(rd);
		}
	}

	/**<b>Mise a jour du vecteur du vaisseau</b>
	 * @param left Permet de savoir si le vaisseau va à gauche ou a droite
	 * @see		VectorQueue#updateNow(Vector, int)
	 * @see 	GameKeyListener#keyPressed(KeyEvent)
	 */
	private void computeShipVec(boolean left){
		//synchronized(vectors){
			try {
				if (left && craft.rec.x - 5 > 0){
					craftVec.x = - 5;
				} else if (!left && craft.rec.x + craft.rec.wid + 5 < board.wid){
					craftVec.x = 5;
				} else {
					craftVec.x = 0;
				}
				vectors.updateNow(craftVec, craft.getGameId());
			} catch (NotReferencedElement e){
				e.printStackTrace();
			}
		//}
	}

	/**<b>Mise a zero du vecteur du vaisseau</b>
	 * @see		VectorQueue#updateNow(Vector, int)
	 * @see		GameKeyListener#keyReleased(KeyEvent)
	 */
	private void zeroShipVec(){
		try {
			craftVec.x = 0;
			vectors.update(craftVec, craft.getGameId());
		} catch (NotReferencedElement e){
			e.printStackTrace();
		}
	}

	/**<b>Calcule des collisions entre les objets à l'écran</b>
	 * <p>Test la condition de collision entre les missiles du vaisseau et les autres objets (sauf le vaisseau)
	 * <br>Test la condition de collision entre le vaisseau et les autres elements du jeu</p>
	 * <p>Si deux objets sont en collision, ils deviennent invisibles</p>
	 * <p>La methode ne retire pas les objets de collections, ceci est fait par la methode {@link SpaceInvasion#drawGameObject()}</p>
	 * @see GameObject#isCovering(GameObject other)
	 */
	private void computeCollision(){

		//La méthode n'enléve pas les objets du tableau d'objets
		//Les objets sont enlevés dans la méthode de calcul de l'affichage

		synchronized(gmObjs){
			for (Missile ms : msls){
				for (int i = 0; i < enemies.size() && !ms.isCollision(); i++){
					for (Enemy en : enemies.get(i)){
						if (!en.isCollision() && en.isCovering(ms)){
							en.setCollision(true);
							ms.setCollision(true);
						}
					}
				}

				for (Missile enMs : enMsls){
					if (!enMs.isCollision() && enMs.isCovering(ms)){
						enMs.setCollision(true);
						ms.setCollision(true);
					}
				}
			}

			for (int i = 0; i < gmObjs.size() - 1; i++){
				if (!gmObjs.get(i).isCollision() && gmObjs.get(i).isCovering(craft)){
					gmObjs.get(i).setCollision(true);
					craft.setCollision(true);
				}
			}
		}

	}


	/**<b>Permet de gérer le comportement des objets lorsqu'ils sont en collision</b>
	 *
	 */
	private void manageGameObjectsCollision(){
		synchronized(gmObjs){
			for (int i = gmObjs.size() - 1; i >= 0; i--){
				GameObject go = gmObjs.get(i);
				if (go.isCollision()){
					go.setVisible(false);
					gmObjs.remove(i);
					moves.remove((Moves)go);
				}
			}

			for (int i = msls.size() - 1; i >= 0; i--){
				Missile ms = msls.get(i);
				if (ms.isCollision() || !ms.isVisible()){ msls.remove(i); }
			}

			for (int i = enMsls.size() - 1; i >= 0; i--){
				Missile ms = enMsls.get(i);
				if (ms.isCollision() || !ms.isVisible()){ enMsls.remove(i); }
			}

			if (craft.isCollision()){
				craft.setVisible(false);
				moves.remove((Moves)craft);
			}
		}

	}

	/**<p><b>Boucle principale du jeu</b></p>
	 * <ul>
	 * <li>Mise à zero de l'image de fond par le buffer {@link Color#setSprite(Color)}</li>
	 * <li>Calcule et MAJ du vecteur de mouvement des ennemis {@link #computeEnmyVec(Vector)}</li>
	 * <li>Calcule des tirs ennemis {@link VectorQueue#update(Vector, int)}</li>
	 * <li>Deplacement et dessin des objets {@link #enmyShoot()}</li>
	 * <li>M.A.J de l'image du jeu dans l'afficheur {@link Afficheur#update(int[])} , {@link Color#getSpriteCopy()}</li>
	 * <li>Calcule des collisions {@link #computeCollision()}</li>
	 * <li>Test la valeur de retour de la fonction</li>
	 * </ul>
	 * @return boolean le joueur a-t-il gagne la partie
	 */
	private boolean gameLoop(){
		boolean play = true;
		enmyVec.x = 0; enmyVec.y = 0;
		try {
			while (play){

				screen.setSprite(buff);

				computeCollision();
				manageGameObjectsCollision();
				enmyShoot();
				enmyVec = computeEnmyVec(enmyVec);
				vectors.update(enmyVec, Enemy.classId());
				moveGameObjects();
				drawGameObject();
				aff.update(screen.getSpriteCopy());

				if (!craft.isVisible()){
					play = false;
				} else if (enemies.size() == 0){
					play = false;
					return true;
				}

				Thread.sleep(2);
			}
			//Thread.sleep(750);
		//Catch pour debug
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NotReferencedElement e){
			e.printStackTrace();
		} catch (ReferencedElement e){
			e.printStackTrace();
		}
		return false;
	}

	/**<b>Permet de détruire tous les liens entre la pile et le tas</b>
	 * <p>Cette fonction est une tentative afin de forcer le garbage collector</p>
	 * <p>Cette operation est menee car il apparait que le garbage collector n'arrive pas a
	 * nettoyer correctement la memoire lorsque des liens entre la pile et la tas subsistent a la fin
	 * de la boucle de jeu</p>
	 */
	private void freeCollections(){

		//Cette fonction est surtout scolaire
		//Les boucles parcourent les tableaux en sens inverse
		//pour annuler les references

		for (int i = enemies.size() - 1; i >= 0; i--){
			for (int j = enemies.get(i).size() - 1; j >= 0; j--){
				enemies.get(i).remove(j);
			}
			enemies.remove(i);
		}

		for (int i = enMsls.size() - 1; i >=0; i--){
			enMsls.remove(i);
		}

		for (int i = msls.size() - 1; i >=0; i--){
			msls.remove(i);
		}

		for (int i = gmObjs.size() - 1; i >=0; i--){
			gmObjs.remove(i);
		}

		for (int i = moves.size() - 1; i >=0; i--){
			moves.remove(i);
		}

		enemies.clear();
		enemies = null;
		enMsls.clear();
		enMsls = null;
		craft = null;
		craftRec = null;
		enmyVec = null;
		enmyRec = null;
		vectors.empty();
		vectors = null;
		gmObjs.clear();
		gmObjs = null;
		moves.clear();
		moves = null;
	}

	/**<b>Implemente l'interruption du jeu par le clavier</b>
	 * <p>Permet d'instancier la classe GameKeyListener appartenant à l'application</p>
	 * @return		KeyListener
	 */
	public KeyListener keyListener(){
		gmKbLst = new GameKeyListener(this);
		return gmKbLst;
	}

	/**<b>Lance le programme</b>
	 * <p>Instancie la classe SpaceInvasion en appellant son constructeur</p>
	 * @param 	args String[] arguments a l'appel du programme
	 * @see		SpaceInvasion#SpaceInvasion()
	 */
	public static void main(String[] args){

		new SpaceInvasion();

	}


	/**<b>Definie un automate fini deterministe permettant de gerer les entrees au clavier pour le jeu</b>
	 * @author Alexandre Cremieux
	 */
	//Gestion des évenements
class GameKeyListener implements KeyListener {

		private SpaceInvasion si = null;

		GameKeyListener(SpaceInvasion si){
			this.si = si;
		}

		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_LEFT){
				//Gauche
				si.computeShipVec(true);
			} else if (keyCode == KeyEvent.VK_RIGHT){
				//Droite
				si.computeShipVec(false);
			} else if (keyCode == KeyEvent.VK_SPACE){
				//Shoot
				si.craftShoot();
			}
		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT){
				//Remise à zero du vecteur du vaisseau
				si.zeroShipVec();
			}
		}
	}

}

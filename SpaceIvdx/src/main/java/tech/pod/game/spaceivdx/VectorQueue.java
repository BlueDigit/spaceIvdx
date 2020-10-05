/**<b>Structure recursive permettant le stockage de Vector() (s)</b>
 * <p>Cette structure est une liste simplement chainnee</p>
 * <p>Les elements sont retrouves par un identifiant</p>
 * <p>Par simplicite cette liste n'integre pas de table de hachage</p>
 * <p>Cette liste n'est pas une file selon le modele FIFO</p>
 * @see Vector#Vector(int, int)
 * @author alexandrecremieux
 */

public class VectorQueue extends Vector{
	private VectorQueue next = null;
	private VectorQueue prev = null;
	private int ref;
	private long timeStamp;
	private long time = 0;
	private boolean released = false;
	
	//Constructors
	/**<b>Cree l'objet avec un premier Vector() a stocker</b>
	 * @param v le premier Vector()
	 * @param ref reference du Vector()
	 */
	VectorQueue(Vector v, int ref){
		super(v.x, v.y);
		this.ref = ref;
		this.timeStamp = System.currentTimeMillis();
	}
	 
	/**<b>Cree l'objet avec un premier Vector() a stocker</b>
	 * <p>L'objet enregistre l'heure a laquelle le vecteur est enregistre</p>
	 * <p>L'objet enregistre un temps minimum entre deux renvoie du vecteur enregistre</p>
	 * <p>Le temps permet par exemple de controle de temps de deplacement d'un objet du jeu</p>
	 * @param v le premier vecteur à stocker
	 * @param ref la reference de l'objet auquel appartient le vecteur
	 * @param time le temps de relachement
	 */
	VectorQueue(Vector v, int ref, long time){
		super(v.x, v.y);
		this.ref = ref;
		this.timeStamp = System.currentTimeMillis();
		this.time = time;
	}
	
	//Accesseur
	public Vector getVector(int reference) throws NotReferencedElement{
		if (this.ref == reference){
			if (this.time == 0 || System.currentTimeMillis() - this.timeStamp >= this.time){
				this.released = true;
				return this;
			} else {
				return new Vector(0, 0);
			}
		} else {
			if (this.next != null)
				return this.next.getVector(reference);
			else 
				throw new NotReferencedElement(reference);
		}
	}
	
	//Methode d'instance de la structure
	public void add(Vector toAdd, int ref) throws ReferencedElement{
		if (this.ref == ref)
			throw new ReferencedElement(ref);
			
		if (this.next == null){
			this.next = new VectorQueue(toAdd, ref);
			this.next.prev = this;
		}else{
			this.next.add(toAdd, ref);
		}
	}
	
	public void add(Vector toAdd, int ref, long time) throws ReferencedElement{
		if (this.ref == ref)
			throw new ReferencedElement(ref);
		
		if (this.next == null)
			next = new VectorQueue(toAdd, ref, time);
		else
			this.next.add(toAdd, ref, time);
	}
	
	public void update(Vector update, int ref) throws NotReferencedElement{
		if (this.ref == ref){
			if (this.time == 0 || (System.currentTimeMillis() - this.timeStamp >= this.time && released)){
				this.x = update.x;
				this.y = update.y;
				this.timeStamp = System.currentTimeMillis();
				this.released = false;
			}
		} else {
			if (this.next != null)
				this.next.update(update, ref);
			else
				throw new NotReferencedElement(ref);
		}
	}
	
	public void updateNow(Vector update, int ref) throws NotReferencedElement{
		if (this.ref == ref){
			this.x = update.x;
			this.y = update.y;
			this.timeStamp = System.currentTimeMillis();
			this.released = false;
		} else {
			if (this.next != null)
				this.next.update(update, ref);
			else
				throw new NotReferencedElement(ref);
		}
	}
	
	public void remove(int ref){
		if (this.ref == ref){
			if (this.prev != null){
				this.next.prev = this.prev;
			}
			if (this.next != null){
				this.prev = this.next;
			}
		} else {
			this.next.remove(ref);
		}
	}
	
	public void empty(){
		if(this.next!= null ){
			this.next.empty();
		}
	}
	
}

class NotReferencedElement extends Exception{
	private static final long serialVersionUID = 4550338511485247983L;

	NotReferencedElement (int ref){
		System.out.println("Vecteur non référencé : " + ref);
	}
}

class ReferencedElement extends Exception{
	private static final long serialVersionUID = 7435066750359713312L;

	ReferencedElement(int ref){
		System.out.println("Vecteur déjà référencé : " + ref);
	}
}
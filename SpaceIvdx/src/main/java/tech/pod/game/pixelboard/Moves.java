package tech.pod.game.pixelboard;

/**<b>Permet de creer une interface entre les objets mobiles du jeu</b>
 * @author alexandrecremieux
 */
public interface Moves {
	public void move(Vector vector);
	public int getGameId();
	//public boolean isVisible();
	public boolean overBounded(Rectangle oRec);
}

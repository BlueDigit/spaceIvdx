package tech.pod.game.generics.ui.graphics;

/**
 * Minimal interface for interact with a game board user interface.
 */
public interface GameScreen<C extends Color>
{
    /**
     * Configure or reconfigure the game board UI.
     *
     * The implementation should be able to overwrite already set configuration.
     *
     * @param configuration should not be null
     * @return this
     * @throws NullPointerException if configuration is null
     */
    GameScreen<C> configure(UIConfiguration configuration, GameImage<C> backGround);

    /**
     * Spool the UI.
     * @return this
     */
    GameScreen<C> spool();

    /**
     * Show the resulting image
     * @return this
     */
    GameScreen<C> draw();

    /**
     * Show the resulting image.
     * @param image should not be null
     * @return this
     */
    GameScreen<C> draw(GameImage<C> image);

    /**
     * Close the screen
     * @return this
     */
    GameScreen<C> close();
}

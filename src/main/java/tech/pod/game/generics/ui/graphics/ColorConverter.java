package tech.pod.game.generics.ui.graphics;

@FunctionalInterface
public interface ColorConverter<C extends Color, O>
{
    O convert(C color);
}

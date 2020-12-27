package tech.pod.game.generics.entity.td;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Vector;

/**
 * Define a read only TDGrid.
 *
 * This class should be considered as a data transfer object and be used in cases where only the state
 * of a {@link TDGrid} is needed. This is particularly useful to transfer data from a layer to another
 * and/or to parallelize computation on the grid.
 */
public class ReadTDGrid implements TDGrid
{
    private final TDMaterial area;
    private final TreeSet<Material<TDPosition, TDMaterial>> materials = new TreeSet<>();
    private final int width;
    private final int cellWidth;

    /**
     * Private constructor to force the calling to use a defined static method to create an instance.
     * @param area The grid's area
     * @param width Should be > 0
     * @param cellWidth Should be > 0 and width should be a multiple of cellWidth
     */
    private ReadTDGrid(TDMaterial area, int width, int cellWidth)
    {
        this.area = Objects.requireNonNull(area, "Null area");
        this.width = width;
        this.cellWidth = cellWidth;
    }

    @Override
    public ReadTDGrid spawn()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getCellWidth()
    {
        return this.cellWidth;
    }

    @Override
    public boolean contains(Material<TDPosition, TDMaterial> material)
    {
        return this.materials.contains(material);
    }

    @Override
    public ReadTDGrid add(Material<TDPosition, TDMaterial> material)
    {
        this.materials.add(material);
        return this;
    }

    @Override
    public ReadTDGrid remove(Material<TDPosition, TDMaterial> material)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Stream the inner materials and filter those that are in collision with the defined position.
     * @param position should not be null
     * @return A new list of materials in collision with the given position
     */
    @Override
    public List<Material<TDPosition, TDMaterial>> get(TDPosition position)
    {
        var posArea = TDMaterial.of(position, position);
        return this.materials.stream().filter(posArea::isInCollision).collect(Collectors.toList());
    }

    /**
     * Just embed the output of {@link ReadTDGrid#get(TDPosition)} in a TreeSet.
     * @param position Should not be null
     * @return A new instance of TreeSet
     */
    @Override
    public TreeSet<Material<TDPosition, TDMaterial>> getFromCell(TDPosition position)
    {
        return new TreeSet<>(this.get(position));
    }

    @Override
    public Material<TDPosition, TDMaterial> translate(TDPosition position, Vector<TDPosition, TDMaterial> vector)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <O extends Material<TDPosition, TDMaterial>> Material<TDPosition, TDMaterial> translate(
            Class<O> jazz, Vector<TDPosition,
            TDMaterial> vector
    ){
        throw new UnsupportedOperationException();
    }

    @Override
    public <O extends Material<TDPosition, TDMaterial>> TreeSet<O> getFromCell(Class<O> jazz)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<Material<TDPosition, TDMaterial>> stream()
    {
        return this.materials.stream().sorted();
    }

    @Override
    public Stream<Material<TDPosition, TDMaterial>> stream(Comparator<Material<TDPosition, TDMaterial>> comparator)
    {
        return this.materials.stream().sorted(comparator);
    }

    @Override
    public Function<TDPosition, Optional<TDPosition>> getHashFunctionByPosition()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Function<TDMaterial, List<TDPosition>> getHashFunctionByMaterial()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInCollision(Material<TDPosition, TDMaterial> other)
    {
        return this.area.isInCollision(other);
    }

    @Override
    public Material<TDPosition, TDMaterial> translate(Vector<TDPosition, TDMaterial> vector)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TDPosition> computePositions()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public TDMaterial get()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(TDMaterial o)
    {
        return this.area.compareTo(o);
    }

    public static ReadTDGrid of(TDMaterial area, int width, int cellWidth)
    {
        return new ReadTDGrid(area, width, cellWidth);
    }
}

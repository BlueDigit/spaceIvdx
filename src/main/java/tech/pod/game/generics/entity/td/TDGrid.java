package tech.pod.game.generics.entity.td;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import tech.pod.game.generics.entity.core.Grid;
import tech.pod.game.generics.entity.core.Material;
import tech.pod.game.generics.entity.core.Vector;

/**
 * Represent a {@link Grid} composed by {@link TDMaterial} objects localized by {@link TDPosition}
 *
 * This grid offers a double localization system based on two maps:
 * <ul>
 *     <li>
 *         One is based on the positions. The area is divided by cells. The cell have a standard size
 *         defined by the user.
 *     </li>
 *     <li>
 *         The other map is based on type. Each materials is indexed by its class.
 *     </li>
 * </ul>
 * In all cases the materials are held by a {@link TreeSet} in order to offer a sorted structure
 * to the calling code and ease computations.
 */
public class TDGrid implements Grid<TDPosition, TDMaterial>
{
    public final int width;
    public final int height;
    public final int cellWidth;
    public final int cellHeight;
    public final Object lockObj = new Object();
    private final Map<TDPosition, TreeSet<Material<TDPosition, TDMaterial>>> mapByPosition = new HashMap<>();
    private final Map<Class<? extends TDMaterial>, TreeSet<Material<TDPosition, TDMaterial>>> mapByClass =
            new HashMap<>();
    private TDMaterial area;

    protected TDGrid(int height, int cellHeight, int width, int cellWidth)
    {
        if (height % cellHeight != 0) {
            throw new IllegalArgumentException("TDGrid: height % cellHeight != 0");
        } else if(width % cellWidth != 0) {
            throw new IllegalArgumentException("TDGrid: width % cellWidth != 0");
        }
        this.height = height;
        this.width = width;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.area = TDMaterial.of(TDPosition.of(0, 0), TDPosition.of(this.width, this.height));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Material<TDPosition, TDMaterial> material)
    {
        Objects.requireNonNull(material, "TDGrid: Cannot remove null material");
        //var position = this.getHashFunctionByPosition().apply(material.get().upperLeft);
        return this.mapByClass.getOrDefault(material.getClass(), new TreeSet<>()).contains(material);
                //|| (position.isPresent() && this.mapByPosition.getOrDefault(position.get(), new TreeSet<>()).contains(material));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDGrid add(Material<TDPosition, TDMaterial> material)
    {
        Objects.requireNonNull(material, "TDGrid: Cannot add null material");
        if (this.isInCollision(material.get()) && !this.contains(material)) {
            material.computePositions()
                    .stream()
                    .map(this.getHashFunctionByPosition())
                    .distinct()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(position -> this.mapByPosition.computeIfAbsent(position, p -> new TreeSet<>())
                                                           .add(material.get())
                    );
            this.mapByClass
                    .computeIfAbsent(material.get().getClass(), c -> new TreeSet<>())
                    .add(material.get());
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDGrid remove(Material<TDPosition, TDMaterial> material)
    {
        Objects.requireNonNull(material, "TDGrid: Cannot remove null material");
        if (this.contains(material)) {
            material.computePositions()
                    .stream()
                    .map(this.getHashFunctionByPosition())
                    .distinct()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(p -> this.mapByPosition.get(p).remove(material));
            this.mapByClass
                    .get(material.getClass())
                    .remove(material);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Material<TDPosition, TDMaterial>> get(TDPosition position)
    {
        Objects.requireNonNull(position, "TDGrid: null position");
        var material = TDMaterial.of(position, position);
        return this.getFromCell(position)
                   .stream()
                   .filter(m -> m.isInCollision(material))
                   .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<Material<TDPosition, TDMaterial>> getFromCell(TDPosition position)
    {
        return this.getHashFunctionByPosition()
                   .apply(position)
                   .flatMap(p -> Optional.ofNullable(this.mapByPosition.get(p)))
                   .map(TreeSet::new)
                   .orElseGet(TreeSet::new);
    }

    @Override
    public TDGrid
    translate(TDPosition position, Vector<TDPosition, TDMaterial> vector)
    {
        Objects.requireNonNull(position, "TDGrid: null position");
        Objects.requireNonNull(vector, "TDGrid: null vector");
        this.get(position)
            .forEach(m -> {
                this.remove(m);
                this.add(m.translate(vector));
            });
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <O extends Material<TDPosition, TDMaterial>> Material<TDPosition, TDMaterial>
    translate(Class<O> jazz, Vector<TDPosition, TDMaterial> vector)
    {
        var materials = new ArrayList<>(this.mapByClass.getOrDefault(jazz, new TreeSet<>()));
        if (!materials.isEmpty()) {
            materials.forEach(this::remove);
            materials.forEach(m -> this.add(m.translate(vector)));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <O extends Material<TDPosition, TDMaterial>> TreeSet<O> getFromCell(Class<O> jazz)
    {
        return new TreeSet<>((TreeSet<O>) this.mapByClass.getOrDefault(jazz, new TreeSet<>()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Material<TDPosition, TDMaterial>> stream()
    {
        return this.mapByClass.values()
                                 .stream()
                                 .flatMap(Set::stream)
                                 .distinct()
                                 .sorted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Material<TDPosition, TDMaterial>> stream(Comparator<Material<TDPosition, TDMaterial>> comparator)
    {
        return this.mapByClass.values()
                                 .stream()
                                 .flatMap(Set::stream)
                                 .sorted(comparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Function<TDPosition, Optional<TDPosition>> getHashFunctionByPosition()
    {
        return position -> Optional
                .ofNullable(position)
                .map(p -> TDPosition.of(p.x / this.cellWidth, p.y / this.cellHeight));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Function<TDMaterial, List<TDPosition>> getHashFunctionByMaterial()
    {
        return material -> material.computePositions()
                                   .stream()
                                   .map(position -> this.getHashFunctionByPosition().apply(position))
                                   .filter(Optional::isPresent)
                                   .map(Optional::get)
                                   .distinct()
                                   .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInCollision(Material<TDPosition, TDMaterial> other)
    {
        return this.area.isInCollision(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized TDGrid spawn()
    {
        var spawn = TDGrid.of(this.height, this.cellHeight, this.width, this.cellWidth);
        this.mapByPosition
                .values()
                .stream()
                .flatMap(Set::stream)
                .map(Material::spawn)
                .forEach(spawn::add);
        return spawn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDGrid translate(Vector<TDPosition, TDMaterial> vector)
    {
        Objects.requireNonNull(vector, "TDGrid: null vector");
        this.area = this.area.translate(vector);
        this.mapByClass
                .values()
                .stream()
                .flatMap(Set::stream)
                .distinct()
                .forEach(material -> {
                    material
                        .computePositions()
                        .stream()
                        .map(p -> this.getHashFunctionByPosition().apply(p))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .distinct()
                        .forEach(position -> this.mapByPosition.get(position).remove(material));

                    material
                        .translate(vector)
                        .computePositions()
                        .stream()
                        .map(p -> this.getHashFunctionByPosition().apply(p))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .distinct()
                        .forEach(position -> this.mapByPosition
                                .computeIfAbsent(position, k -> new TreeSet<>())
                                .add(material)
                            );
                });
        return this;
    }

    @Override
    public List<TDPosition> computePositions()
    {
        return this.mapByPosition.keySet()
                                 .stream()
                                 .filter(k -> !this.mapByPosition.get(k).isEmpty())
                                 .sorted()
                                 .collect(Collectors.toList());
    }

    @Override
    public TDMaterial get()
    {
        return this.area;
    }

    @Override
    public int compareTo(TDMaterial o)
    {
        return this.area.compareTo(o);
    }

    public static TDGrid of(int height, int cellHeight, int width, int cellWidth) {
        return new TDGrid(height, cellHeight, width, cellWidth);
    }
}

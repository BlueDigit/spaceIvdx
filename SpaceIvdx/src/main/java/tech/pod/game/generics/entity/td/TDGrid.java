package tech.pod.game.generics.entity.td;

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

public class TDGrid implements Grid<TDPosition, TDMaterial>
{
    private final int width;
    private final int height;
    private final int cellWidth;
    private final int cellHeight;
    private final Map<TDPosition, TreeSet<Material<TDPosition, TDMaterial>>> mapByPosition = new HashMap<>();
    private final Map<Class<? extends TDMaterial>, TreeSet<Material<TDPosition, TDMaterial>>> mapByClass = new HashMap<>();
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

    @Override
    public boolean contains(Material<TDPosition, TDMaterial> material)
    {
        Objects.requireNonNull(material, "TDGrid: Cannot remove null material");
        var set = this.mapByClass.getOrDefault(material.getClass(), new TreeSet<>());
        return set.contains(material);
    }

    @Override
    public TDGrid add(Material<TDPosition, TDMaterial> material)
    {
        Objects.requireNonNull(material, "TDGrid: Cannot add null material");
        if (this.isInCollision(material.get()) && !this.contains(material)) {
            synchronized (this.mapByPosition) {
                final var positionSet = new TreeSet<TDPosition>();
                material.computePositions()
                        .stream()
                        .map(this.getHashFunctionByPosition())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(positionSet::add)
                        .forEach(position -> this.mapByPosition.computeIfAbsent(position, p -> new TreeSet<>())
                                                               .add(material.get())
                        );
                this.mapByClass
                        .computeIfAbsent(material.get().getClass(), c -> new TreeSet<>())
                        .add(material.get());
            }
        }
        return this;
    }

    @Override
    public TDGrid remove(Material<TDPosition, TDMaterial> material)
    {
        Objects.requireNonNull(material, "TDGrid: Cannot remove null material");
        if (this.contains(material)) {
            synchronized (this.mapByPosition) {
                var emptyTreeSet = new TreeSet<Material<TDPosition, TDMaterial>>();
                material.computePositions()
                        .stream()
                        .map(this.getHashFunctionByPosition())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(position -> this.mapByPosition.getOrDefault(position, emptyTreeSet).remove(material));
                this.mapByClass
                        .get(material.getClass())
                        .remove(material);
            }
        }
        return this;
    }

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

    @Override
    public TreeSet<Material<TDPosition, TDMaterial>> getFromCell(TDPosition position)
    {
        return this.getHashFunctionByPosition()
                                   .apply(position)
                                   .map(p -> this.mapByPosition.getOrDefault(p, new TreeSet<>()))
                                   .orElseGet(TreeSet::new);
    }

    @Override
    public Material<TDPosition, TDMaterial>
    translate(TDPosition position, Vector<TDPosition, TDMaterial> vector)
    {
        Objects.requireNonNull(position, "TDGrid: null position");
        Objects.requireNonNull(vector, "TDGrid: null vector");
        synchronized (this.mapByPosition) {
            this.get(position)
                .forEach(m -> {
                    this.remove(m);
                    this.add(m.translate(vector));
                });
        }
        return this;
    }

    @Override
    public <O extends Material<TDPosition, TDMaterial>>
    Material<TDPosition, TDMaterial> translate(Class<O> jazz, Vector<TDPosition, TDMaterial> vector)
    {
        synchronized (this.mapByPosition) {
            this.mapByClass
                    .getOrDefault(jazz, new TreeSet<>())
                    .forEach(m -> {
                        this.remove(m);
                        this.add(m.translate(vector));
                    });
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O extends Material<TDPosition, TDMaterial>> TreeSet<O> getFromCell(Class<O> jazz)
    {
        return (TreeSet<O>) this.mapByClass.getOrDefault(jazz, new TreeSet<>());
    }

    @Override
    public Stream<Material<TDPosition, TDMaterial>> stream()
    {
        return this.mapByPosition.values()
                                 .stream()
                                 .flatMap(Set::stream)
                                 .sorted();
    }

    @Override
    public Stream<Material<TDPosition, TDMaterial>> stream(Comparator<Material<TDPosition, TDMaterial>> comparator)
    {
        return this.mapByPosition.values()
                                 .stream()
                                 .flatMap(Set::stream)
                                 .sorted(comparator);
    }

    @Override
    public Function<TDPosition, Optional<TDPosition>> getHashFunctionByPosition()
    {
        return position -> Optional
                .ofNullable(position)
                .map(p -> TDPosition.of(p.x / this.cellWidth, p.y / this.cellHeight));
    }

    @Override
    public Function<TDMaterial, List<TDPosition>> getHashFunctionByMaterial()
    {
        final var positionSet = new TreeSet<TDPosition>();
        return material -> material.computePositions()
                                   .stream()
                                   .map(position -> this.getHashFunctionByPosition().apply(position))
                                   .filter(Optional::isPresent)
                                   .map(Optional::get)
                                   .filter(positionSet::add)
                                   .collect(Collectors.toList());
    }

    @Override
    public boolean isInCollision(Material<TDPosition, TDMaterial> other)
    {
        return this.area.isInCollision(other);
    }

    @Override
    public TDGrid spawn()
    {
        var grid = TDGrid.of(this.height, this.cellHeight, this.width, this.cellWidth);
        this.mapByPosition
                .values()
                .stream()
                .flatMap(Set::stream)
                .map(Material::spawn)
                .forEach(grid::add);
        return grid;
    }

    @Override
    public Material<TDPosition, TDMaterial> translate(Vector<TDPosition, TDMaterial> vector)
    {
        Objects.requireNonNull(vector, "TDGrid: null vector");
        synchronized (this.mapByPosition) {
            this.area = this.area.translate(vector);
            this.mapByClass
                    .values()
                    .stream()
                    .flatMap(Set::stream)
                    .forEach(material -> {
                        material.computePositions()
                                .forEach(position -> this.mapByPosition.get(position).remove(material));
                        material.translate(vector)
                                .computePositions()
                                .forEach(position -> this.mapByPosition
                                        .computeIfAbsent(position, k -> new TreeSet<>())
                                        .add(material)
                                );
                    });
        }
        return this;
    }

    @Override
    public List<TDPosition> computePositions()
    {
        synchronized (this.mapByPosition) {
            return this.mapByPosition.keySet()
                                     .stream()
                                     .filter(k -> !this.mapByPosition.get(k).isEmpty())
                                     .sorted()
                                     .collect(Collectors.toList());
        }
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

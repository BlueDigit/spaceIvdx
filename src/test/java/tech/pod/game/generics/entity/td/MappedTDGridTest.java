package tech.pod.game.generics.entity.td;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.pod.game.generics.entity.core.Material;

class MappedTDGridTest
{
    private static class Ship extends TDMaterial {
        public Ship() {
            super(TDMaterial.BASIC_COMPARATOR);
        }
    }

    @Test
    @DisplayName("Simple add should work")
    void add()
    {
        var topLeft = TDPosition.of(0, 5);
        // 0. Init
        var grid = MappedTDGrid.of(10, 5, 10, 5);
        var materials = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .peek(grid::add)
                .collect(Collectors.toList());

        Assertions.assertEquals(grid, grid.add(materials.get(0)));

        // 1. Check by classes
        var materialOne = grid.getFromCell(TDMaterial.class);
        Assertions.assertFalse(materialOne.isEmpty());

        // 2. Check by position
        var otherMaterials = grid.getFromCell(topLeft);
        Assertions.assertFalse(otherMaterials.isEmpty());

        // 3. Check with defined material
        var definedMaterials = grid.getFromCell(materials.get(0).upperLeft);
        Assertions.assertFalse(definedMaterials.isEmpty());
        Assertions.assertTrue(definedMaterials.contains(materials.get(0)));
        var targetedMaterial = definedMaterials
                .stream()
                .filter(materials.get(0)::equals)
                .findFirst();
        Assertions.assertTrue(targetedMaterial.isPresent());
    }

    @Test
    void remove() {
        var topLeft = TDPosition.of(0, 5);
        // 0. Init
        var grid = MappedTDGrid.of(10, 5, 10, 5);
        var materials = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .filter(grid::isInCollision)
                .peek(grid::add)
                .collect(Collectors.toList());

        Assertions.assertEquals(grid, grid.add(materials.get(0)));
        materials.forEach(m -> Assertions.assertTrue(grid.contains(m)));

        // 1. Check that we can remove only part of the set
        var otherMaterials = grid.getFromCell(topLeft);
        Assertions.assertFalse(otherMaterials.isEmpty());

        otherMaterials.forEach(m -> Assertions.assertEquals(grid, grid.remove(m)));
        var noMoreOtherMaterials = grid.getFromCell(topLeft);
        Assertions.assertTrue(noMoreOtherMaterials.isEmpty());
        var rest = grid.getFromCell(TDMaterial.class);
        Assertions.assertEquals(materials.size() - otherMaterials.size(), rest.size());

        // 2. Check that we can remove
        materials.forEach(grid::remove);
        materials.forEach(m -> Assertions.assertFalse(grid.contains(m)));
    }

    @Test
    void contains() {
        // 0. Init
        var grid = MappedTDGrid.of(10, 2, 10, 2);
        var materials = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .peek(grid::add)
                .collect(Collectors.toList());

        // 1. Assertions
        Assertions.assertTrue(grid.contains(materials.get(0)));
    }

    @Test
    @DisplayName("Getting element at one position should work")
    void gettingAnElementAtPositionShouldWork() {
        // 0. Init
        var grid = MappedTDGrid.of(10, 2, 10, 2);
        List<TDMaterial> materials = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .peek(grid::add)
                .collect(Collectors.toList());
        List<Material<TDPosition, TDMaterial>> searched = grid.get(TDPosition.of(0, 2));
        List<Material<TDPosition, TDMaterial>> shouldBeEmpty = grid.get(TDPosition.of(6, 0));

        // 1. Assertions
        Assertions.assertEquals(1, searched.size());
        Assertions.assertEquals(materials.get(0), searched.get(0));
        Assertions.assertTrue(shouldBeEmpty.isEmpty());

        // 2. Add the same list again
        TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .forEach(grid::add);
        List<Material<TDPosition, TDMaterial>> shouldBeOfSizeTwo = grid.get(TDPosition.of(0, 2));

        // 3. Assertions
        Assertions.assertEquals(2, shouldBeOfSizeTwo.size());
    }

    @Test
    void translateTest() {
        // 0. Init
        var grid = MappedTDGrid.of(10, 2, 10, 2);
        var materialsPosition = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .peek(grid::add)
                .collect(Collectors.toMap(m -> m, m -> m.upperLeft));
        var vector = TDVector.of(1, 1);

        // 1. Test
        var counter = grid.translate(vector)
            .stream().map(m -> {
                if (materialsPosition.containsKey(m.get())) {
                    Assertions.assertNotEquals(materialsPosition.get(m.get()),
                                               m.get().upperLeft);
                    return 1;
                }
                return 0;
            }).reduce(0, Integer::sum);
        Assertions.assertTrue(counter > 0);
    }

    @Test
    void stream()
    {
        // 0. Init
        int numbedOfMaterials = 10;
        var grid = MappedTDGrid.of(10, 2, 10, 2);
        TDTestEntityGenerator
                .generateMaterialsByTranslation(
                        TDPosition.of(0, 0),
                        TDPosition.of(5, 5),
                        TDVector.of(1, 1), numbedOfMaterials
                )
                .forEach(grid::add);

        // 1. Assertions
        TDMaterial[] last = new TDMaterial[1];
        var uniqueMaterials = new HashSet<Material<TDPosition, TDMaterial>>();
        var counter = grid.stream().map(m -> {
            if (last[0] != null && (m instanceof TDMaterial)) {
                Assertions.assertTrue(last[0].compareTo(m.get()) <= 0);
            }
            Assertions.assertTrue(uniqueMaterials.add(m));
            last[0] = m.get();
            return 1;
        }).reduce(0, Integer::sum);
        Assertions.assertTrue(counter > 0);
    }

    @Test
    void streamWithComparator()
    {
        // 0. Init
        int numbedOfMaterials = 10;
        var grid = MappedTDGrid.of(10, 2, 10, 2);
        TDTestEntityGenerator
                .generateMaterialsByTranslation(
                        TDPosition.of(0, 0),
                        TDPosition.of(5, 5),
                        TDVector.of(1, 1), numbedOfMaterials
                )
                .forEach(grid::add);

        // 1. Assertions
        TDMaterial[] last = new TDMaterial[1];
        var uniqueMaterials = new HashSet<Material<TDPosition, TDMaterial>>();
        var counter = grid.stream(TDMaterial.BASIC_COMPARATOR).map(m -> {
            if (last[0] != null && (m instanceof TDMaterial)) {
                Assertions.assertTrue(last[0].compareTo(m.get()) <= 0);
            }
            Assertions.assertTrue(uniqueMaterials.add(m));
            last[0] = m.get();
            return 1;
        }).reduce(0, Integer::sum);
        Assertions.assertTrue(counter > 0);
    }

    @Test
    void isInCollision()
    {
        // 0. Init
        var grid = MappedTDGrid.of(10, 5, 10, 5);

        // 1. Test that the grid is in collision with itself
        Assertions.assertTrue(grid.isInCollision(grid));
    }

    @Test
    void testTranslateByClassShouldWork()
    {
        // 0. Init
        var grid = MappedTDGrid.of(10, 2, 10, 2);
        TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .forEach(grid::add);
        var ship = new Ship();
        ship.setUpperLeft(TDPosition.of(0, 0));
        ship.setLowerRight(TDPosition.of(1, 1));
        grid.add(ship);

        // 1. checks
        Assertions.assertTrue(grid.contains(ship));
        Assertions.assertEquals(TDPosition.of(0, 0), ship.upperLeft);
        Assertions.assertTrue(grid.get(TDPosition.of(0, 0)).contains(ship));
        grid.translate(Ship.class, TDVector.of(1, 1));
        var ships = grid.getFromCell(Ship.class);
        Assertions.assertFalse(ships.isEmpty());
        Assertions.assertSame(ship, ships.first());
        Assertions.assertEquals(TDPosition.of(1, 1), ships.first().upperLeft);
        Assertions.assertEquals(TDPosition.of(1, 1), ship.upperLeft);
        Assertions.assertFalse(grid.get(TDPosition.of(0, 0)).contains(ship));
        Assertions.assertTrue(grid.get(TDPosition.of(1, 1)).contains(ship));
    }

    @Test
    void spawn()
    {
        // 0. Init
        var grid = MappedTDGrid.of(10, 2, 10, 2);
        var materials = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .peek(grid::add)
                .collect(Collectors.toList());
        var spawn = grid.spawn();
        var firstMaterials = spawn.getFromCell(materials.get(0).upperLeft);

        // 1. Assertions
        Assertions.assertFalse(firstMaterials.isEmpty());
        Assertions.assertNotSame(grid, spawn);
        Assertions.assertNotEquals(grid, spawn);

        Assertions.assertTrue(grid.contains(materials.get(0)));
        Assertions.assertFalse(spawn.contains(materials.get(0)));
        var position = materials.get(0).getUpperLeft();
        var fromSpawn = spawn.get(position);
        Assertions.assertFalse(materials.isEmpty());
        var isCopiedInSpawn = fromSpawn
                .stream()
                .map(m -> m.get().getUpperLeft().equals(materials.get(0).upperLeft)).filter(b -> b)
                .findFirst()
                .orElse(false);
        Assertions.assertTrue(isCopiedInSpawn);

        Assertions.assertNotSame(materials.get(0), firstMaterials.first());
        Assertions.assertSame(materials.get(0).upperLeft, firstMaterials.first().get().upperLeft);
    }
}
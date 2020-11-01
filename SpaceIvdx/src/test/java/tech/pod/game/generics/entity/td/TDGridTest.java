package tech.pod.game.generics.entity.td;

import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.pod.game.generics.entity.core.Material;

class TDGridTest
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
        var grid = TDGrid.of(10, 5, 10, 5);
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
    void contains() {
        // 0. Init
        var grid = TDGrid.of(10, 2, 10, 2);
        var materials = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .peek(grid::add)
                .collect(Collectors.toList());

        // 1. Assertions
        Assertions.assertTrue(grid.contains(materials.get(0)));
    }

    @Test
    void gettingAnElementAtPositionShouldWork() {
        // 0. Init
        var grid = TDGrid.of(10, 2, 10, 2);
        var materials = TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .stream()
                .peek(grid::add)
                .collect(Collectors.toList());
        var searched = grid.get(TDPosition.of(0, 2));
        var shouldBeEmpty = grid.get(TDPosition.of(6, 0));

        // 1. Assertions
        Assertions.assertEquals(1, searched.size());
        Assertions.assertEquals(materials.get(0), searched.get(0));
        Assertions.assertTrue(shouldBeEmpty.isEmpty());
    }

    @Test
    void stream()
    {
        // 0. Init
        var grid = TDGrid.of(10, 2, 10, 2);
        TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .forEach(grid::add);

        // 1. Assertions
        int[] counter = new int[1];
        Material<TDPosition, TDMaterial>[] last = new TDMaterial[1];
        grid.stream().forEach(m -> {
            if (last[0] != null && (m instanceof TDMaterial)) {
                Assertions.assertTrue(last[0].compareTo((TDMaterial)m) <= 0);
                counter[0]+= 1;
            }
            last[0] = m;
        });
        Assertions.assertTrue(counter[0] > 0);
    }

    @Test
    void isInCollision()
    {
        // 0. Init
        var grid = TDGrid.of(10, 5, 10, 5);

        // 1. Test that the grid is in collision with itself
        Assertions.assertTrue(grid.isInCollision(grid));
    }

    @Test
    void testTranslateByClassShouldWork()
    {
        // 0. Init
        var grid = TDGrid.of(10, 2, 10, 2);
        TDTestEntityGenerator
                .generateMaterialsByTranslation(TDPosition.of(0, 0), TDPosition.of(5, 5), TDVector.of(1, 1), 10)
                .forEach(grid::add);
        var ship = new Ship();
        ship.setUpperLeft(TDPosition.of(0, 0));
        ship.setLowerRight(TDPosition.of(1, 1));
        grid.add(ship);

        // 2. checks
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
        var grid = TDGrid.of(10, 2, 10, 2);
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
        Assertions.assertTrue(spawn.contains(materials.get(0)));
        Assertions.assertNotSame(materials.get(0), firstMaterials.first());
        Assertions.assertSame(materials.get(0).upperLeft, firstMaterials.first().get().upperLeft);
    }
}
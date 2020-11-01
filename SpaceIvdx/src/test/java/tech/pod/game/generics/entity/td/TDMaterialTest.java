package tech.pod.game.generics.entity.td;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TDMaterialTest
{
    @Test
    @DisplayName("Simple is in collision should work")
    void isInCollision()
    {
        var upperLeft = TDPosition.of(0, 0);
        var lowerRight = TDPosition.of(10, 10);
        var anotherUpperLeft = TDPosition.of(2, 2);
        var material = TDMaterial.of(upperLeft, lowerRight);
        var other = TDMaterial.of(anotherUpperLeft, lowerRight);
        Assertions.assertTrue(material.isInCollision(other));
        Assertions.assertTrue(other.isInCollision(material));
    }

    @Test
    @DisplayName("Is in collision when overlaps should work")
    void isInCollisionWhenOverlapShouldWork()
    {
        var upperLeft = TDPosition.of(0, 0);
        var lowerRight = TDPosition.of(10, 10);
        var anotherUpperLeft = TDPosition.of(2, 2);
        var anotherLowerRight = TDPosition.of(15, 15);
        var material = TDMaterial.of(upperLeft, lowerRight);
        var other = TDMaterial.of(anotherUpperLeft, anotherLowerRight);
        Assertions.assertTrue(material.isInCollision(other));
        Assertions.assertTrue(other.isInCollision(material));
    }

    @Test
    void spawn()
    {
        var upperLeft = TDPosition.of(0, 0);
        var lowerRight = TDPosition.of(10, 10);
        var material = TDMaterial.of(upperLeft, lowerRight);
        var other = material.spawn();
        Assertions.assertNotEquals(material, other);
        Assertions.assertEquals(upperLeft, other.upperLeft);
        Assertions.assertEquals(lowerRight, other.lowerRight);
    }

    @Test
    void translate()
    {
        var upperLeft = TDPosition.of(0, 0);
        var lowerRight = TDPosition.of(10, 10);
        var material = TDMaterial.of(upperLeft, lowerRight);
        var translated = material.translate(TDVector.of(10, 10));
        Assertions.assertEquals(material, translated);
        Assertions.assertEquals(10, material.upperLeft.x);
        Assertions.assertEquals(10, material.upperLeft.y);
        Assertions.assertEquals(20, material.lowerRight.x);
        Assertions.assertEquals(20, material.lowerRight.y);
    }
}
package tech.pod.game.generics.entity.td;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TDTestEntityGenerator
{
    public static class Enemy extends TDMaterial
    {
        public Enemy() {
            super(TDMaterial.BASIC_COMPARATOR);
        }
    }

    private TDTestEntityGenerator() {}

    public static <E extends TDMaterial> Function<Integer, TDMaterial> generator(TDVector vector, Supplier<E> initial) {
        return i -> {
            var iVector = TDVector
                    .of(vector.x * i, vector.y * i);
            return iVector.apply(initial.get());
        };
    }

    public static List<TDMaterial> generateMaterialsByTranslation(Function<Integer, TDMaterial> generator, int number) {
        return IntStream
                .rangeClosed(0, number)
                .mapToObj(generator::apply)
                .collect(Collectors.toList());
    }

    public static <E extends TDMaterial> List<TDMaterial> generateMaterialsByTranslation(Supplier<E> initial,
                                                                                         TDVector vector,
                                                                                         int number) {
        return TDTestEntityGenerator
                .generateMaterialsByTranslation(generator(vector, initial), number);
    }

    public static List<TDMaterial> generateMaterialsByTranslation(TDPosition upperLeft,
                                                                  TDPosition lowerRight,
                                                                  TDVector vector,
                                                                  int number) {
        var generator = TDTestEntityGenerator
                .generator(vector, () -> TDMaterial.of(upperLeft, lowerRight));
        return TDTestEntityGenerator
                .generateMaterialsByTranslation(generator, number);
    }
}

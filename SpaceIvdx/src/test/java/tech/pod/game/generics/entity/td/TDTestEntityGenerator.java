package tech.pod.game.generics.entity.td;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TDTestEntityGenerator
{
    private TDTestEntityGenerator() {}

    public static List<TDMaterial> generateMaterialsByTranslation(TDPosition upperLeft,
                                                           TDPosition lowerRight,
                                                           TDVector vector,
                                                           int number) {
        return IntStream
                .rangeClosed(0, number)
                .mapToObj(i -> {
                    var v = TDVector
                            .of(vector.x * i, vector.y * i);
                    return TDMaterial
                            .of(upperLeft, lowerRight)
                            .translate(v);
                })
                .collect(Collectors.toList());
    }
}

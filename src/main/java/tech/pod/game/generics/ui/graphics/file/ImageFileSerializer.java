package tech.pod.game.generics.ui.graphics.file;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import tech.pod.game.generics.utils.Tuple;

public interface ImageFileSerializer<I>
{
    I deserialize(String fileName);
    I deserialize(InputStream fileStream);
    Map<String, I> deserialize(List<String> fileNames);
    Map<String, I> deserializeStreams(List<Tuple.Pair<String, InputStream>> fileStream);
    void serialize(String fileName);
}

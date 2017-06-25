package api.helper;


import java.util.List;

public class ContainerHelper {
    public static <T> boolean isPresent(List<T> sizeable) {
        return sizeable != null && !sizeable.isEmpty();
    }

    public static boolean isPresent(String sizeable) {
        return sizeable != null && !sizeable.isEmpty();
    }
}

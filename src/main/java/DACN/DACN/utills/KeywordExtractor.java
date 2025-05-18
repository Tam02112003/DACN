package DACN.DACN.utills;



import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeywordExtractor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "tôi", "muốn", "cần", "mua", "có", "bạn", "cho", "xem", "sản", "phẩm",
            "loại", "nào", "được", "hàng", "gì", "giúp", "được", "tìm", "giới", "thiệu"
    ));

    public static String extractKeyword(String message) {
        return Arrays.stream(message.toLowerCase().split("\\s+"))
                .filter(word -> !STOP_WORDS.contains(word))
                .findFirst()
                .orElse(""); // nếu không có từ phù hợp
    }
}


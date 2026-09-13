import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TextFileDemo {
    public static void main(String[] args) {
        Path file = Path.of("data", "ghi_chu.txt");

        try {
            Files.createDirectories(file.getParent());

            // CREATE: tạo file nếu chưa tồn tại
            // APPEND: ghi nối tiếp vào cuối file
            try (BufferedWriter writer = Files.newBufferedWriter(
                    file,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {

                writer.newLine();
                writer.write("Java I/O làm việc với các luồng dữ liệu.");
                writer.newLine();
                writer.write("BufferedWriter giúp đọc/ghi dữ liệu hiệu quả hơn.");
                writer.newLine();
                writer.write("UTF-8 hỗ trợ tiếng Việt ổn định.");
            }

            System.out.println("Đường dẫn tuyệt đối: "
                    + file.toAbsolutePath());

            System.out.println("\nĐọc bằng UTF-8:");
            readFile(file, StandardCharsets.UTF_8);

            System.out.println("\nĐọc bằng ISO-8859-1:");
            readFile(file, StandardCharsets.ISO_8859_1);

        } catch (IOException e) {
            System.err.println("Lỗi xử lý tệp " + file + ": " + e.getMessage());
        }
    }

    private static void readFile(Path file, Charset charset)
            throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            int number = 1;

            while ((line = reader.readLine()) != null) {
                System.out.printf("Dòng %d: %s%n", number++, line);
            }
        }
    }
}
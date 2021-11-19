import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentTypeTest {

public static void main(String[] argv) {
        String[] input = {"index.html", "index.htm"};
        int failCount = 0;
        try {
                for (String s : input) {
                        Path path = Paths.get(s);
                        String filetype = Files.probeContentType(path);
                        if (filetype == null || !filetype.equals("text/html")) {
                                System.out.println(path.toString() + " - file type is not text/html; file type is - " + filetype);
                                failCount++;
                        } else {
                                System.out.println(path.toString() + " - file type is text/html");
                        }
                }
        } catch (Exception e) {
                e.printStackTrace();
        }

        if (failCount > 0) {
                System.out.println("TEST FAIL!");
        } else {
                System.out.println("TEST PASS!");
        }
}
}

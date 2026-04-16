import java.io.File;

/**
 * Простая диагностика путей и файлов.
 * Запуск: mvn exec:java -Dexec.mainClass="DiagnosticTest" -Dexec.classpathScope="test"
 */
public class DiagnosticTest {

    public static void main(String[] args) {
        System.out.println("=== Diagnostic Test ===");

        // Проверка текущей директории
        System.out.println("Current dir: " + System.getProperty("user.dir"));

        // Проверка существования JSP в исходниках
        checkFile("src/main/webapp/pages/confirm_success.jsp");
        checkFile("src/main/webapp/pages/error/confirm_error.jsp");

        // Проверка в собранном артефакте
        checkFile("target/blinov_first-1.0-SNAPSHOT/pages/confirm_success.jsp");
        checkFile("target/blinov_first-1.0-SNAPSHOT/pages/error/confirm_error.jsp");

        // Проверка в WEB-INF/classes
        checkFile("target/blinov_first-1.0-SNAPSHOT/WEB-INF/classes/blinov_first/util/PagePath.class");
        checkFile("target/blinov_first-1.0-SNAPSHOT/WEB-INF/classes/blinov_first/command/impl/ConfirmEmailCommand.class");

        System.out.println("=== Test Finished ===");
    }

    private static void checkFile(String path) {
        File f = new File(path);
        System.out.println((f.exists() ? "✅" : "❌") + " " + path + " : " + (f.exists() ? "EXISTS" : "NOT FOUND"));
    }
}
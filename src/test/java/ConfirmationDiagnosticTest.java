import blinov_first.dao.UserDao;
import blinov_first.dao.impl.UserDaoImpl;
import blinov_first.entity.User;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.impl.UserServiceImpl;
import blinov_first.util.TokenGenerator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Optional;

/**
 * Diagnostic test for email confirmation flow.
 * Run via: mvn exec:java -Dexec.mainClass="ConfirmationDiagnosticTest" -Dexec.classpathScope="test"
 */
@SuppressWarnings({"SpellCheckingInspection"})
public class ConfirmationDiagnosticTest {

    // === НАСТРОЙКИ: вставь токен из письма или логина ===
    private static final String TEST_TOKEN = "uEtr9VvGGiN-JYMQ4P5gwLX1zg4CKJXlhHsHAXHc5xY"; // <-- замени на свой
    private static final String TEST_LOGIN = "testuser4"; // <-- или логин пользователя

    public static void main(String[] args) {
        System.out.println("=== Confirmation Diagnostic Test ===\n");

        try {
            // 1. Проверка подключения к БД
            testDatabaseConnection();

            // 2. Проверка структуры таблицы users
            testTableStructure();

            // 3. Поиск пользователя по токену
            testFindByToken(TEST_TOKEN);

            // 4. Поиск пользователя по логину
            testFindByLogin(TEST_LOGIN);

            // 5. Проверка статуса активности
            testUserActiveStatus(TEST_LOGIN);

            // 6. Тест активации (только если токен валиден!)
            // testActivation(TEST_TOKEN); // <-- раскомментируй осторожно!

            // 7. Тест генерации токена
            testTokenGeneration();

        } catch (Exception e) {
            System.err.println("❌ Test failed with exception:");
            e.printStackTrace();
        }

        System.out.println("\n=== Test Finished ===");
    }

    private static void testDatabaseConnection() {
        System.out.println("🔹 [1] Testing database connection...");
        try {
            UserDao dao = UserDaoImpl.getInstance();
            dao.findAll(); // Простой запрос для проверки соединения
            System.out.println("   ✅ Database connection: OK\n");
        } catch (DaoException e) {
            System.err.println("   ❌ Database connection: FAILED");
            System.err.println("   Error: " + e.getMessage() + "\n");
        }
    }

    private static void testTableStructure() {
        System.out.println("🔹 [2] Checking 'users' table structure...");
        try {
            UserDaoImpl dao = UserDaoImpl.getInstance();
            // Получаем соединение через пул (рефлексия, т.к. нет публичного метода)
            // Для теста просто попробуем выполнить запрос с новыми колонками
            String sql = "SELECT confirmation_token, is_active, token_created_at FROM users LIMIT 1";
            try (Connection conn = blinov_first.pool.ConnectionPool.getInstance().getConnection();
                 var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(sql)) {
                System.out.println("   ✅ Table structure: OK (columns exist)");
            }
        } catch (Exception e) {
            System.err.println("   ❌ Table structure: MISSING COLUMNS");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("   Fix: Run ALTER TABLE users ADD COLUMN...");
        }
        System.out.println();
    }

    private static void testFindByToken(String token) {
        if (token == null || token.isEmpty() || token.equals("YOUR_TOKEN_HERE")) {
            System.out.println("🔹 [3] Skipping token lookup (no token provided)\n");
            return;
        }
        System.out.println("🔹 [3] Looking up user by token: " + token.substring(0, 8) + "...");
        try {
            UserDao dao = UserDaoImpl.getInstance();
            Optional<User> userOpt = dao.findByConfirmationToken(token);

            if (userOpt.isPresent()) {
                User u = userOpt.get();
                System.out.println("   ✅ User found:");
                System.out.println("      - Login: " + u.getLogin());
                System.out.println("      - Email: " + u.getEmail());
                System.out.println("      - Is active: " + u.isActive());
            } else {
                System.out.println("   ⚠️  User NOT found by this token");
                System.out.println("      Possible reasons:");
                System.out.println("      • Token already used (cleared after activation)");
                System.out.println("      • Token expired");
                System.out.println("      • Wrong token copied");
            }
        } catch (DaoException e) {
            System.err.println("   ❌ Database error during lookup:");
            System.err.println("      " + e.getMessage());
        }
        System.out.println();
    }

    private static void testFindByLogin(String login) {
        if (login == null || login.isEmpty()) {
            System.out.println("🔹 [4] Skipping login lookup (no login provided)\n");
            return;
        }
        System.out.println("🔹 [4] Looking up user by login: " + login);
        try {
            UserDao dao = UserDaoImpl.getInstance();
            Optional<User> userOpt = dao.findByLogin(login);

            if (userOpt.isPresent()) {
                User u = userOpt.get();
                System.out.println("   ✅ User found:");
                System.out.println("      - Email: " + u.getEmail());
                System.out.println("      - Is active: " + u.isActive());
                System.out.println("      - Token: " +
                        (u.getConfirmationToken() != null ?
                                u.getConfirmationToken().substring(0, 8) + "..." : "null"));
            } else {
                System.out.println("   ⚠️  User NOT found with login: " + login);
            }
        } catch (DaoException e) {
            System.err.println("   ❌ Database error: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testUserActiveStatus(String login) {
        if (login == null || login.isEmpty()) {
            System.out.println("🔹 [5] Skipping active status check\n");
            return;
        }
        System.out.println("🔹 [5] Checking if user '" + login + "' is active...");
        try {
            UserDao dao = UserDaoImpl.getInstance();
            boolean isActive = dao.isUserActive(login);
            System.out.println("   ✅ is_active = " + isActive);
            if (!isActive) {
                System.out.println("   ⚠️  User is NOT active — login will be blocked!");
                System.out.println("   Fix: Confirm email or activate manually via SQL");
            }
        } catch (DaoException e) {
            System.err.println("   ❌ Error checking status: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testActivation(String token) {
        if (token == null || token.isEmpty()) return;

        System.out.println("🔹 [6] Testing activation with token: " + token.substring(0, 8) + "...");
        System.out.println("   ⚠️  WARNING: This will ACTIVATE the user and CLEAR the token!");
        System.out.println("   Press Ctrl+C to cancel, or wait 3 seconds to continue...");

        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        try {
            boolean result = UserServiceImpl.getInstance().confirmRegistration(token);
            System.out.println("   ✅ Activation result: " + result);
            if (result) {
                System.out.println("   🎉 User activated! Try logging in now.");
            } else {
                System.out.println("   ⚠️  Activation returned false (maybe already active?)");
            }
        } catch (ServiceException e) {
            System.err.println("   ❌ Activation failed: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("   Cause: " + e.getCause().getMessage());
            }
        }
        System.out.println();
    }

    private static void testTokenGeneration() {
        System.out.println("🔹 [7] Testing token generation...");
        try {
            String token = TokenGenerator.generateSecureToken();
            System.out.println("   ✅ Token generated: " + token.substring(0, 16) + "...");
            System.out.println("   Length: " + token.length() + " chars");
        } catch (Exception e) {
            System.err.println("   ❌ Token generation failed: " + e.getMessage());
        }
        System.out.println();
    }
}
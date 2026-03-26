package blinov_first.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
    private static final String URL = "jdbc:mysql://localhost:3306/phonestest2";
    private static final int POOL_SIZE = 8;

    // ЛОГ ПРЯМО В КОНСОЛЬ ДЛЯ ДИАГНОСТИКИ
    private static final ConnectionPool instance;

    static {
        System.out.println("[POOL DEBUG] Начало статической инициализации ConnectionPool...");
        try {
            // Принудительная проверка драйвера
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[POOL DEBUG] Драйвер найден успешно.");
        } catch (ClassNotFoundException e) {
            System.err.println("[POOL ERROR] КРИТИЧЕСКАЯ ОШИБКА: Драйвер MySQL не найден в classpath!");
            e.printStackTrace();
        }
        instance = new ConnectionPool();
    }

    private final BlockingQueue<Connection> free = new LinkedBlockingQueue<>(POOL_SIZE);
    private final BlockingQueue<Connection> used = new LinkedBlockingQueue<>(POOL_SIZE);

    private ConnectionPool() {
        System.out.println("[POOL DEBUG] Конструктор пула: пытаюсь создать " + POOL_SIZE + " соединений...");
        Properties prop = new Properties();
        prop.put("user", "root");
        prop.put("password", "7GAZcCrhHaLH");

        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Connection connection = DriverManager.getConnection(URL, prop);
                free.add(connection);
                System.out.println("[POOL DEBUG] Соединение #" + (i + 1) + " создано и добавлено в пул.");
            } catch (SQLException e) {
                System.err.println("[POOL ERROR] Не удалось создать соединение #" + (i + 1));
                e.printStackTrace();
            }
        }
        System.out.println("[POOL DEBUG] Инициализация окончена. Свободно: " + free.size());
    }

    public static ConnectionPool getInstance() {
        return instance;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            System.out.println("[POOL DEBUG] Запрос соединения... (Свободно: " + free.size() + ")");
            // Ждем максимум 5 секунд, чтобы не висеть вечно
            connection = free.poll(5, TimeUnit.SECONDS);

            if (connection == null) {
                System.err.println("[POOL ERROR] ТАЙМАУТ: Соединений в пуле нет! Возможно, ты забыл их возвращать?");
            } else {
                used.put(connection);
                System.out.println("[POOL DEBUG] Соединение выдано. Используется: " + used.size());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return connection;
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            used.remove(connection);
            try {
                free.put(connection);
                System.out.println("[POOL DEBUG] Соединение вернулось. Свободно: " + free.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
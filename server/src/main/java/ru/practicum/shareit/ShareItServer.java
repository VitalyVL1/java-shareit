package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения ShareIt Server.
 * <p>
 * Server модуль является основным бэкенд-сервисом приложения, содержащим всю
 * бизнес-логику и работу с базой данных. Он выполняет следующие функции:
 * <ul>
 *   <li>Обрабатывает запросы, поступающие от модуля {@code gateway}</li>
 *   <li>Реализует всю бизнес-логику приложения (пользователи, вещи, бронирования, запросы)</li>
 *   <li>Взаимодействует с базой данных PostgreSQL через Spring Data JPA</li>
 *   <li>Возвращает результаты обработки обратно в модуль {@code gateway}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Модуль server работает на отдельном порту (по умолчанию 9090) и не доступен
 * напрямую клиентам, только через модуль {@code gateway}.
 * </p>
 *
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication
public class ShareItServer {

    /**
     * Точка входа в приложение ShareIt Server.
     * <p>
     * Запускает Spring Boot приложение с конфигурацией из класса {@link ShareItServer}.
     * </p>
     *
     * @param args аргументы командной строки, передаваемые при запуске
     */
    public static void main(String[] args) {
        SpringApplication.run(ShareItServer.class, args);
    }
}
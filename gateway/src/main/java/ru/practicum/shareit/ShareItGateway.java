package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения ShareIt Gateway.
 * <p>
 * Gateway модуль является входной точкой для всех клиентских запросов.
 * Он выполняет следующие функции:
 * <ul>
 *   <li>Принимает HTTP-запросы от клиентов</li>
 *   <li>Выполняет первичную валидацию входящих данных</li>
 *   <li>Перенаправляет проверенные запросы в модуль {@code server}</li>
 *   <li>Возвращает ответы от сервера клиентам</li>
 * </ul>
 * </p>
 *
 * <p>
 * Модуль gateway работает на отдельном порту (по умолчанию 8080) и
 * взаимодействует с модулем {@code server} через REST API.
 * </p>
 *
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication
public class ShareItGateway {
    /**
     * Точка входа в приложение ShareIt Gateway.
     * <p>
     * Запускает Spring Boot приложение с конфигурацией из класса {@link ShareItGateway}.
     * </p>
     *
     * @param args аргументы командной строки, передаваемые при запуске
     */
    public static void main(String[] args) {
        SpringApplication.run(ShareItGateway.class, args);
    }
}
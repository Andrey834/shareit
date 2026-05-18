# ShareIt

Сервис для аренды вещей. Пользователи могут делиться своими вещами, оставлять заявки на аренду и бронировать предметы друг у друга.

## Архитектура

Проект состоит из двух микросервисов:

| Сервис | Порт | Описание |
|--------|------|----------|
| **Gateway** | 8080 | API-шлюз: валидация входящих запросов, маршрутизация |
| **Server** | 9090 | Бизнес-логика, хранение данных (PostgreSQL / H2) |

```
Client → Gateway (8080) → Server (9090) → PostgreSQL
```

## Технологии

- **Java 17** / Spring Boot 2.7
- **PostgreSQL** (production) / H2 (тесты)
- **Spring Data JPA** / Hibernate
- **Maven** (multi-module)
- **Docker** / Docker Compose
- **JUnit 5** + Mockito
- **Lombok**
- **Checkstyle** / SpotBugs / JaCoCo

## Запуск

### Через Docker Compose

```bash
docker-compose up
```

### Локально

```bash
# Запустить PostgreSQL (или использовать H2 для разработки)
mvn clean package -DskipTests
java -jar gateway/target/gateway-*.jar
java -jar server/target/server-*.jar
```

## Функциональность

### Пользователи
| Метод | Путь | Описание |
|-------|------|----------|
| `POST` | `/users` | Создать пользователя |
| `PATCH` | `/users/{id}` | Обновить данные |
| `GET` | `/users/{id}` | Получить пользователя |
| `GET` | `/users` | Получить всех |
| `DELETE` | `/users/{id}` | Удалить пользователя |

### Вещи
| Метод | Путь | Описание |
|-------|------|----------|
| `POST` | `/items` | Добавить вещь |
| `PATCH` | `/items/{id}` | Редактировать вещь |
| `GET` | `/items/{id}` | Получить вещь |
| `GET` | `/items` | Список вещей владельца |
| `GET` | `/items/search?text=` | Поиск по тексту |
| `POST` | `/items/{id}/comment` | Оставить комментарий |

> Комментарий можно оставить только после завершённой аренды.

### Бронирования
| Метод | Путь | Описание |
|-------|------|----------|
| `POST` | `/bookings` | Создать бронирование |
| `PATCH` | `/bookings/{id}?approved=` | Подтвердить / отклонить |
| `GET` | `/bookings/{id}` | Получить бронирование |
| `GET` | `/bookings?state=&owner=` | Список бронирований |

Заголовок аутентификации: `X-Sharer-User-Id: {userId}`

Фильтры состояния (`state`): `ALL`, `CURRENT`, `PAST`, `FUTURE`, `WAITING`, `REJECTED`

### Заявки на аренду
| Метод | Путь | Описание |
|-------|------|----------|
| `POST` | `/requests` | Создать заявку |
| `GET` | `/requests` | Свои заявки |
| `GET` | `/requests/all` | Все чужие заявки (с пагинацией) |
| `GET` | `/requests/{id}` | Получить заявку |

## Модель данных

```
User ──< Item       (пользователь владеет вещами)
User ──< Booking    (пользователь создаёт бронирования)
Item ──< Booking    (вещь бронируется)
User ──< Request    (пользователь создаёт заявки)
Item >── Request    (вещь может быть ответом на заявку)
Item ──< Comment    (пользователи оставляют комментарии)
```

## Тесты

```bash
mvn test
```

Покрытие кода: ≥ 90% по строкам (JaCoCo).

## Структура проекта

```
shareit/
├── gateway/          # API-шлюз
│   └── src/
│       └── main/java/ru/practicum/shareit/
│           ├── booking/
│           ├── client/
│           ├── item/
│           ├── request/
│           └── user/
└── server/           # Бизнес-логика
    └── src/
        ├── main/java/ru/practicum/server/
        │   ├── booking/      # Бронирования
        │   ├── exception/    # Глобальная обработка ошибок
        │   ├── item/         # Вещи и комментарии
        │   ├── request/      # Заявки
        │   └── user/         # Пользователи
        └── test/
```

# Аутентификация и авторизация с использованием Spring Security и JWT  

## Описание проекта  
Этот проект представляет собой веб-приложение на базе Spring Boot, реализующее механизм аутентификации и авторизации пользователей с использованием JWT (JSON Web Tokens). Проект использует Spring Security для обеспечения безопасности и JWT для управления сеансами пользователей.

## Основные возможности  
- Аутентификация пользователей: Пользователи могут получить токен доступа после успешной аутентификации.
- Авторизация: Токен используется для доступа к защищенным ресурсам API.
- JWT: Токены JWT используются для безопасного управления сеансами и хранения информации о пользователе.

## Структура проекта
- Config: Конфигурация безопасности. Настройка Spring Security.
Фильтрация запросов и проверка JWT.
- Util:  Утилиты JWT. Генерация, верификация и извлечение информации из JWT токенов.
- Filter: Обработка JWT токенов и установка аутентификации в контексте Spring Security.
- Aspect: Содержит аспект логирования, который перехватывает вызовы методов и исключения для их логирования.
- Controller: REST-контроллеры, обрабатывающие HTTP-запросы.
- DTO (Data Transfer Object): Объекты, используемые для передачи данных между слоями приложения.
- Exception: Пользовательские классы исключений, используемые в проекте.
- Mapper: Классы, отвечающие за преобразование между моделями и DTO.
- Model: Классы, представляющие бизнес-сущности.
- Repository: Интерфейсы для доступа к данным.
- Service: Классы, содержащие бизнес-логику.
- Tests: Тестовые классы, проверяющие логику сервиса и функциональность логирования.

## Начало работы
### Предварительные требования
- IntelliJ IDEA
- Java 21
- SpringBoot
- Spring Boot Test
- Spring Boot AOP
- Spring Boot Data JPA
- Spring Web
- PostgreSQL
- Log4j2
- Lombok
- Spring Security
- Springdoc OpenAPI
- Jwt

## Сборка и запуск приложения

### Соберите проект:
mvn clean install

### Запустите приложение:
mvn spring-boot:run

### Доступ к API:

API будет доступен по адресу http://localhost:8080.

## Тестирование
### Тесты логирования
Следующий тест проверяет,  что при успешной регистрации возвращается правильный статус:  

`@SpringBootTest  
@AutoConfigureMockMvc  
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtTokenUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterSuccess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RegisterRequest request = new RegisterRequest("username", "password", "email@example.com");
        UserDTO userDTO = new UserDTO();
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }`
## Заключение
В этом проекте реализован механизм аутентификации и авторизации с использованием Spring Security и JSON Web Tokens (JWT), что обеспечивает надежную защиту веб-приложения.
Проект предоставляет полное решение для управления безопасностью веб-приложения, включая документирование API через Swagger и реализацию тестов для обеспечения надежности.

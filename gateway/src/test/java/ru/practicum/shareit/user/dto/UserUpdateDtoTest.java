package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserUpdateDtoTest {
    private final JacksonTester<UserUpdateDto> json;
    private Validator validator;

    private static final String NAME = "John Doe";
    private static final String EMAIL = "john.doe@example.com";

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerializationWithAllFields() throws Exception {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();

        JsonContent<UserUpdateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(NAME);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(EMAIL);
    }

    @Test
    void testDeserializationWithAllFields() throws Exception {
        String jsonContent = String.format(
                "{\"name\": \"%s\", \"email\": \"%s\"}",
                NAME, EMAIL);

        UserUpdateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo(NAME);
        assertThat(dto.email()).isEqualTo(EMAIL);
    }

    @Test
    void testValidDto() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidDtoWithNullFields() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .build();

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).extracting("propertyPath")
                .toString().equals("email");
    }

    @Test
    void testInvalidEmailDto() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name(NAME)
                .email("email")
                .build();


        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("propertyPath")
                .toString().equals("email");
    }

    @Test
    void testInvalidDtoWithBlankFields() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("")
                .email("")
                .build();

        Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty(); //незаполненые поля допускаются, т.к. @NotBlank расширяет @NotNull, а обновление опционально.
    }
}
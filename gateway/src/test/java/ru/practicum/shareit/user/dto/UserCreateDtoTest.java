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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserCreateDtoTest {
    private final JacksonTester<UserCreateDto> json;
    private Validator validator;

    private static final String NAME = "John Doe";
    private static final String EMAIL = "john.doe@example.com";

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialization() throws Exception {
        UserCreateDto dto = UserCreateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();

        JsonContent<UserCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(NAME);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(EMAIL);
    }

    @Test
    void testDeserialization() throws Exception {
        String jsonContent = String.format(
                "{\"name\": \"%s\", \"email\": \"%s\"}",
                NAME, EMAIL);

        UserCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo(NAME);
        assertThat(dto.email()).isEqualTo(EMAIL);
    }

    @Test
    void testValidDto() {
        UserCreateDto dto = UserCreateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidDtoWithNullFields() {
        UserCreateDto dto = UserCreateDto.builder()
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        Set<String> fieldNames = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(fieldNames).containsExactlyInAnyOrder("name", "email");
    }

    @Test
    void testInvalidEmailDto() {
        UserCreateDto dto = UserCreateDto.builder()
                .name(NAME)
                .email("email")
                .build();


        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("propertyPath")
                .toString().equals("email");
    }

    @Test
    void testInvalidDtoWithBlankFields() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("   ")
                .email("")
                .build();

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        Set<String> fieldNames = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(violations).hasSize(2);
        assertThat(fieldNames).containsExactlyInAnyOrder("name", "email");
    }
}
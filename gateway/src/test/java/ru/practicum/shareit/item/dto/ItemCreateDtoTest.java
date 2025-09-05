package ru.practicum.shareit.item.dto;

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
class ItemCreateDtoTest {
    private final JacksonTester<ItemCreateDto> json;
    private Validator validator;

    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final Boolean AVAILABLE = true;
    private static final Long REQUEST_ID = 1L;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerializationWithAllFields() throws Exception {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .available(AVAILABLE)
                .requestId(REQUEST_ID)
                .build();

        JsonContent<ItemCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(NAME);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(DESCRIPTION);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(AVAILABLE);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(REQUEST_ID.intValue());
    }

    @Test
    void testDeserializationWithAllFields() throws Exception {
        String jsonContent = String.format(
                "{\"name\": \"%s\", \"description\": \"%s\", \"available\": %b, \"requestId\": %d}",
                NAME, DESCRIPTION, AVAILABLE, REQUEST_ID);

        ItemCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo(NAME);
        assertThat(dto.description()).isEqualTo(DESCRIPTION);
        assertThat(dto.available()).isEqualTo(AVAILABLE);
        assertThat(dto.requestId()).isEqualTo(REQUEST_ID);
    }

    @Test
    void testDeserializationWithoutRequestId() throws Exception {
        String jsonContent = String.format(
                "{\"name\": \"%s\", \"description\": \"%s\", \"available\": %b}",
                NAME, DESCRIPTION, AVAILABLE);

        ItemCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.name()).isEqualTo(NAME);
        assertThat(dto.description()).isEqualTo(DESCRIPTION);
        assertThat(dto.available()).isEqualTo(AVAILABLE);
        assertThat(dto.requestId()).isNull();
    }

    @Test
    void testValidDto() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .available(AVAILABLE)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidDtoWithBlankFields() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("   ")
                .description("   ")
                .available(AVAILABLE)
                .build();

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);

        Set<String> fieldNames = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(fieldNames).containsExactlyInAnyOrder("name", "description");
    }

    @Test
    void testInvalidDtoWithNullFields() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .build();

        Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(dto);

        Set<String> fieldNames = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(fieldNames).containsExactlyInAnyOrder("name", "description", "available");
    }
}
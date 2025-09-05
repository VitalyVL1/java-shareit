package ru.practicum.shareit.request.dto;

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
class ItemRequestCreateDtoTest {
    private final JacksonTester<ItemRequestCreateDto> json;
    private Validator validator;

    private static final String DESCRIPTION = "Description";

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialization() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto(DESCRIPTION);

        JsonContent<ItemRequestCreateDto> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(DESCRIPTION);
    }

    @Test
    void testDeserialization() throws Exception {
        String jsonContent = String.format("{\"description\": \"%s\"}", DESCRIPTION);
        ItemRequestCreateDto dto = json.parseObject(jsonContent);
        assertThat(dto.description()).isEqualTo(DESCRIPTION);
    }

    @Test
    void testValidDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto(DESCRIPTION);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidNullFieldsDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto(null);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("propertyPath")
                .toString().equals("description");
    }

    @Test
    void testValidBlankFieldsDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("   ");

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("propertyPath")
                .toString().equals("description");
    }
}
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

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentCreateOrUpdateDtoTest {
    private final JacksonTester<CommentCreateOrUpdateDto> json;
    private Validator validator;

    private static final String NORMAL_TEXT = "Normal text";

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNormalTextSerialization() throws Exception {
        CommentCreateOrUpdateDto dto = new CommentCreateOrUpdateDto(NORMAL_TEXT);

        JsonContent<CommentCreateOrUpdateDto> result = json.write(dto);

        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(NORMAL_TEXT);
        assertThat(result).doesNotHaveJsonPath("$.otherField"); // Убедиться, что нет лишних полей
    }

    @Test
    void testNormalTextDeserialization() throws Exception {
        String jsonContent = String.format("{\"text\": \"%s\"}", NORMAL_TEXT);

        CommentCreateOrUpdateDto dto = json.parseObject(jsonContent);

        assertThat(dto.text()).isEqualTo(NORMAL_TEXT);
    }

    @Test
    void testValidDto() {
        CommentCreateOrUpdateDto dto = new CommentCreateOrUpdateDto(NORMAL_TEXT);

        Set<ConstraintViolation<CommentCreateOrUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testValidNullFieldsDto() {
        CommentCreateOrUpdateDto dto = new CommentCreateOrUpdateDto(null);

        Set<ConstraintViolation<CommentCreateOrUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("propertyPath")
                .toString().equals("text");
    }

    @Test
    void testValidBlankFieldsDto() {
        CommentCreateOrUpdateDto dto = new CommentCreateOrUpdateDto("   ");

        Set<ConstraintViolation<CommentCreateOrUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("propertyPath")
                .toString().equals("text");
    }
}
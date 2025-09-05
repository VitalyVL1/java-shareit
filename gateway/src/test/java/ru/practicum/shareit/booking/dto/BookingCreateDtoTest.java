package ru.practicum.shareit.booking.dto;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingCreateDtoTest {
    private final JacksonTester<BookingCreateDto> json;
    private Validator validator;

    private static final long ITEM_ID = 1;
    private static final LocalDateTime START = LocalDateTime.now().plusHours(1);
    private static final LocalDateTime END = LocalDateTime.now().plusHours(2);
    private static final String START_STRING = START.format(DateTimeFormatter.ISO_DATE_TIME);
    private static final String END_STRING = END.format(DateTimeFormatter.ISO_DATE_TIME);

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testBookingCreateDtoSerialization() throws Exception {
        BookingCreateDto bookingDto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(START)
                .end(END)
                .build();

        JsonContent<BookingCreateDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo((int) ITEM_ID);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(START_STRING);
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(END_STRING);
    }

    @Test
    void testBookingCreateDtoDeserialization() throws Exception {
        String jsonContent = String.format(
                "{\"itemId\": %d, \"start\": \"%s\", \"end\": \"%s\"}",
                ITEM_ID, START_STRING, END_STRING);

        BookingCreateDto dto = json.parseObject(jsonContent);

        assertThat(dto.itemId()).isEqualTo(ITEM_ID);
        assertThat(dto.start()).isEqualTo(START);
        assertThat(dto.end()).isEqualTo(END);
    }

    @Test
    void testValidationStartBeforeEnd() {
        BookingCreateDto validDto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(START)
                .end(END)
                .build();

        assertThat(validDto.isStartBeforeEnd()).isTrue();
    }

    @Test
    void testValidationStartAfterEnd() {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(END) // start после end
                .end(START)
                .build();

        assertThat(invalidDto.isStartBeforeEnd()).isFalse();
    }

    @Test
    void testValidationWithNullDates() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(null)
                .end(null)
                .build();

        assertThat(dto.isStartBeforeEnd()).isTrue();
    }

    @Test
    void testValidDto() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(START)
                .end(END)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidDtoWithNullFields() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        Set<String> fieldNames = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(fieldNames).containsExactlyInAnyOrder("itemId", "start", "end");
    }

    @Test
    void testInvalidDtoWithEndBeforeStart() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(END)
                .end(START)
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("propertyPath")
                .toString().equals("startBeforeEnd");
    }

    @Test
    void testInvalidDtoWithStartAndEndInPast() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build();

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);
        Set<String> fieldNames = violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(violations).hasSize(2);
        assertThat(fieldNames).containsExactlyInAnyOrder("start", "end");
    }
}
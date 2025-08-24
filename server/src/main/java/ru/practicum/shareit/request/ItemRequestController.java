package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto addRequest(
            @Valid @RequestBody ItemRequestCreateDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Adding new request by user: {}", userId);
        return itemRequestService.save(userId, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestResponseDto> getRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests by user: {}", userId);
        return itemRequestService.findByUserId(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestResponseDto getRequestsById(
            @PathVariable @Valid @Positive Long requestId) {
        log.info("Get request by request id: {}", requestId);
        return itemRequestService.findById(requestId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestResponseDto> getAllRequests() {
        log.info("Get all requests");
        return itemRequestService.findAll();
    }
}

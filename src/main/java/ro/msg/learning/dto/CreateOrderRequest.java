package ro.msg.learning.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private LocalDateTime timeStamp;

    @NotNull
    private UUID deliveryAddressId;

    @NotNull
    @Valid
    private List<OrderItemRequest> orderItems;
}

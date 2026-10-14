package ro.msg.learning.dto;


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

    private UUID userId;
    private LocalDateTime timeStamp;
    private UUID deliveryAddressId;
    private List<OrderItemRequest> orderItems;
}

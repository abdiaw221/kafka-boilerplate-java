package io.confluent.examples.clients.cloud.order_services.service;

import io.confluent.examples.clients.cloud.order_services.base_domain.domain.Order;

public interface OrderManageService {
    Order confirm(Order orderPayment, Order orderStock);
}

package io.confluent.examples.clients.cloud.order_services.service.imp;

import io.confluent.examples.clients.cloud.order_services.base_domain.domain.Order;
import io.confluent.examples.clients.cloud.order_services.base_domain.enums.OrderStatus;
import io.confluent.examples.clients.cloud.order_services.service.OrderManageService;

public class OrderManageServiceImp implements OrderManageService {
    @Override
    public Order confirm(final Order orderPayment, final Order orderStock) {
        // Initialize payment
        final Order order = new Order();
        order.setId(orderPayment.getId());
        order.setCustomerId(orderPayment.getCustomerId());
        order.setProductCount(orderPayment.getProductCount());
        order.setPrice(orderPayment.getPrice());
        if (order.getStatus().equals(OrderStatus.ACCEPT) && orderStock.getStatus().equals(OrderStatus.ACCEPT)) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else if (orderPayment.getStatus().equals(OrderStatus.REJECT) && orderStock.getStatus().equals(OrderStatus.REJECT)) {
            order.setStatus(OrderStatus.REJECT);
        } else if (orderPayment.getStatus().equals(OrderStatus.REJECT) || orderStock.getStatus().equals(OrderStatus.REJECT)) {
            final String source = orderPayment.getStatus().equals(OrderStatus.REJECT) ? OrderStatus.PAYMENT.name() : OrderStatus.STOCK.name();
            order.setStatus(OrderStatus.ROLLBACK);
            order.setSource(source);
        }
        return order;
    }
}

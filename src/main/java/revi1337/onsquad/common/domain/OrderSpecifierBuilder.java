package revi1337.onsquad.common.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class OrderSpecifierBuilder {

    private final List<OrderSpecifier<?>> orders = new ArrayList<>();

    private OrderSpecifierBuilder() {
    }

    public static OrderSpecifierBuilder empty() {
        return new OrderSpecifierBuilder();
    }

    public static OrderSpecifierBuilder startWith(OrderSpecifier<?>... order) {
        OrderSpecifierBuilder builder = new OrderSpecifierBuilder();
        builder.orders.addAll(Arrays.asList(order));
        return builder;
    }

    public static OrderSpecifierBuilder startWith(EntityPathBase<?> qClass, Pageable pageable) {
        return new OrderSpecifierBuilder().addPageableSort(qClass, pageable);
    }

    public OrderSpecifierBuilder addPageableSort(EntityPathBase<?> qClass, Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return this;
        }

        PathBuilder<?> pathBuilder = new PathBuilder<>(qClass.getType(), qClass.getMetadata());
        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
        }
        return this;
    }

    public OrderSpecifierBuilder add(OrderSpecifier<?>... order) {
        if (order != null) {
            this.orders.addAll(Arrays.asList(order));
        }
        return this;
    }

    public OrderSpecifier<?>[] build() {
        return orders.toArray(OrderSpecifier[]::new);
    }
}

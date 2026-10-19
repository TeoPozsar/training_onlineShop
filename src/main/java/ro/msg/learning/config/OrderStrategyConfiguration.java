package ro.msg.learning.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.msg.learning.repository.StockRepository;
import ro.msg.learning.service.strategy.MostAbundantStrategy;
import ro.msg.learning.service.strategy.OrderStrategy;
import ro.msg.learning.service.strategy.OrderStrategyType;
import ro.msg.learning.service.strategy.SingleLocationStrategy;

@Configuration
public class OrderStrategyConfiguration {

    @Bean
    public OrderStrategy orderStrategy(@Value("${order.strategy}") OrderStrategyType strategyType, StockRepository stockRepository) {

        return switch (strategyType) {
            case MOST_ABUNDANT -> new MostAbundantStrategy(stockRepository);
            case SINGLE_LOCATION -> new SingleLocationStrategy(stockRepository);
        };
    }
}

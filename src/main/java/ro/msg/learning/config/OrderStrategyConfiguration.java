package ro.msg.learning.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.msg.learning.repository.StockRepository;
import ro.msg.learning.service.strategy.MostAbundantStrategy;
import ro.msg.learning.service.strategy.OrderStrategy;
import ro.msg.learning.service.strategy.SingleLocationStrategy;

@Configuration
public class OrderStrategyConfiguration {

    @Bean
    public OrderStrategy orderStrategy( @Value("${order.strategy}") String strategyName, StockRepository stockRepository) {
        return switch (strategyName) {
            case "single-location" -> new SingleLocationStrategy(stockRepository);
            case "most-abundant" -> new MostAbundantStrategy(stockRepository);
            default -> throw new IllegalArgumentException("Invalid order strategy: " + strategyName);
        };
    }
}

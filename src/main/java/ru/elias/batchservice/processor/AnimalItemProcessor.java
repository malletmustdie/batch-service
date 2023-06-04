package ru.elias.batchservice.processor;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.elias.batchservice.model.Animal;

@Slf4j
@Component
public class AnimalItemProcessor implements ItemProcessor<Animal, Animal> {

    @Override
    public Animal process(Animal item) {
        log.info("Processing {}", item.getName());
        return new Animal()
                .setId(item.getId())
                .setName(item.getName().toUpperCase(Locale.ROOT));
    }

}

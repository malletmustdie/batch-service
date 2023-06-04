package ru.elias.batchservice.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import ru.elias.batchservice.model.Animal;

@Component
public class AnimalFieldSetMapper implements FieldSetMapper<Animal> {

    @Override
    public Animal mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Animal()
                .setId(fieldSet.readLong("id"))
                .setName(fieldSet.readString("name"));
    }

}

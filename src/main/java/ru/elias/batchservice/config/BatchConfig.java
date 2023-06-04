package ru.elias.batchservice.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import ru.elias.batchservice.mapper.AnimalFieldSetMapper;
import ru.elias.batchservice.model.Animal;
import ru.elias.batchservice.processor.AnimalItemProcessor;

@EnableBatchProcessing
@Configuration
public class BatchConfig extends DefaultBatchConfiguration {

    @Value("${application.tokens}")
    private String[] tokens;

    @Bean
    public Job myJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("myJob", jobRepository)
                .flow(step)
                .end()
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     ItemReader<Animal> csvItemReader,
                     ItemWriter<Animal> jsonItemWriter,
                     ItemWriter<Animal> dbItemWriter) {
        return new StepBuilder("myStep", jobRepository)
                .<Animal, Animal>chunk(3, getTransactionManager())
                .reader(csvItemReader)
                .processor(new AnimalItemProcessor())
                .writer(jsonItemWriter)
                .writer(dbItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Animal> csvItemReader(@Value("#{jobParameters['file.input']}") String input) {
        var builder = new FlatFileItemReaderBuilder<Animal>();
        return builder.name("animalReader")
                      .resource(new FileSystemResource(input))
                      .delimited()
                      .delimiter(";")
                      .names(tokens)
                      .fieldSetMapper(new AnimalFieldSetMapper())
                      .build();
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<Animal> jsonItemWriter(@Value("#{jobParameters['file.output']}") String output) {
        var builder = new JsonFileItemWriterBuilder<Animal>();
        var marshaller = new JacksonJsonObjectMarshaller<Animal>();
        return builder.name("animalWriter")
                      .jsonObjectMarshaller(marshaller)
                      .resource(new FileSystemResource(output))
                      .build();
    }

    @Bean
    public JdbcBatchItemWriter<Animal> dbItemWriter() {
        return new JdbcBatchItemWriterBuilder<Animal>()
                .dataSource(getDataSource())
                .sql("insert into animals(id, name) values(:id, :name)")
                .beanMapped()
                .build();
    }

}

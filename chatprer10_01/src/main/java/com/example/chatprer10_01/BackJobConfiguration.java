package com.example.chatprer10_01;

import com.example.chatprer10_01.component.*;
import com.example.chatprer10_01.domain.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemWriterBuilder;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Configuration
public class BackJobConfiguration {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;


    public BackJobConfiguration(JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public FieldSetMapper<CustomerUpdate> customerUpdatesFieldSetMapper() {
        return (FieldSet fieldSet) -> {
            int recordId = fieldSet.readInt(0);
            switch (recordId) {
                case 1: return new CustomerNameUpdate(fieldSet.readLong("customerId"),
                        fieldSet.readString("firstName"),
                        fieldSet.readString("middleName"),
                        fieldSet.readString("lastName"));
                case 2: return new CustomerAddressUpdate(fieldSet.readLong("customerId"),
                        fieldSet.readString("address1"),
                        fieldSet.readString("address2"),
                        fieldSet.readString("city"),
                        fieldSet.readString("state"),
                        fieldSet.readString("postalCode"));
                case 3:
                    String rawPreference = fieldSet.readString("notificationPreference");
                    Integer notificationPreference = null;
                    if (StringUtils.hasText(rawPreference)) {
                        notificationPreference = Integer.parseInt(rawPreference);
                    }
                    return new CustomerContactUpdate(fieldSet.readLong("customerId"),
                            fieldSet.readString("emailAddress"),
                            fieldSet.readString("homePhone"),
                            fieldSet.readString("cellPhone"),
                            fieldSet.readString("workPhone"),
                            notificationPreference);
                default: throw new IllegalArgumentException("Invalid record type was found:" + fieldSet.readInt("recordId"));

            }
        };

    }

    @Bean
    public LineTokenizer customerUpdatesLineTokenizer() throws Exception {
        DelimitedLineTokenizer recordType1 = new DelimitedLineTokenizer();
        recordType1.setNames("recordId", "customerId", "firstName", "middleName", "lastName");
        recordType1.afterPropertiesSet();

        DelimitedLineTokenizer recordType2 = new DelimitedLineTokenizer();
        recordType2.setNames("recordId", "customerId", "address1", "address2", "city", "state", "postalCode");
        recordType2.afterPropertiesSet();

        DelimitedLineTokenizer recordType3 = new DelimitedLineTokenizer();
        recordType3.setNames("recordId", "customerId", "emailAddress", "homePhone", "cellPhone", "workPhone", "notificationPreference");
        recordType3.afterPropertiesSet();

        HashMap<String, LineTokenizer> lineTokenizerHashMap = new HashMap<>();
        lineTokenizerHashMap.put("1*", recordType1);
        lineTokenizerHashMap.put("2*", recordType2);
        lineTokenizerHashMap.put("3*", recordType3);

        PatternMatchingCompositeLineTokenizer patternMatchingCompositeLineTokenizer = new PatternMatchingCompositeLineTokenizer();
        patternMatchingCompositeLineTokenizer.setTokenizers(lineTokenizerHashMap);

        return patternMatchingCompositeLineTokenizer;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CustomerUpdate> customerFlatFileItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) throws Exception {
        return new FlatFileItemReaderBuilder<CustomerUpdate>()
                .name("customerFlatFileItemReader")
                .resource(inputFile)
                .lineTokenizer(customerUpdatesLineTokenizer())
                .fieldSetMapper(customerUpdatesFieldSetMapper())
                .build();
    }

    @Bean
    public ValidatingItemProcessor<CustomerUpdate> customerIdValidatorItemProcessor(CustIdValidator validator){
        ValidatingItemProcessor<CustomerUpdate> customerUpdateValidatingItemProcessor = new ValidatingItemProcessor<>(validator);
        customerUpdateValidatingItemProcessor.setFilter(true);
        return customerUpdateValidatingItemProcessor;
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerNameUpdateJdbcBatchItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE CUSTOMER " +
                        "SET FIRST_NAME = COALESCE(:firstName, FIRST_NAME), " +
                        "MIDDLE_NAME = COALESCE(:middleName, MIDDLE_NAME), " +
                        "LAST_NAME = COALESCE(:lastName, LAST_NAME) " +
                        "WHERE CUSTOMER_ID = :customerId")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerAddressUpdateItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE CUSTOMER SET " +
                        "ADDRESS1 = COALESCE(:address1, ADDRESS1), " +
                        "ADDRESS2 = COALESCE(:address2, ADDRESS2), " +
                        "CITY = COALESCE(:city, CITY), " +
                        "STATE = COALESCE(:state, STATE), " +
                        "POSTAL_CODE = COALESCE(:postalCode, POSTAL_CODE) " +
                        "WHERE CUSTOMER_ID = :customerId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<CustomerUpdate> customerContactUpdateItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
                .beanMapped()
                .sql("UPDATE CUSTOMER SET " +
                        "EMAIL_ADDRESS = COALESCE(:emailAddress, EMAIL_ADDRESS), " +
                        "HOME_PHONE = COALESCE(:homePhone, HOME_PHONE), " +
                        "CELL_PHONE = COALESCE(:cellPhone, CELL_PHONE), " +
                        "WORK_PHONE = COALESCE(:workPhone, WORK_PHONE), " +
                        "NOTIFICATION_PREF = COALESCE(:notificationPreferences, NOTIFICATION_PREF) " +
                        "WHERE CUSTOMER_ID = :customerId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<CustomerUpdate> customerUpdateItemWriter() {
        CustomerUpdateClassifier classifier = new CustomerUpdateClassifier(
                customerNameUpdateJdbcBatchItemWriter(null),
                customerAddressUpdateItemWriter(null),
                customerContactUpdateItemWriter(null));

        return new ClassifierCompositeItemWriterBuilder<CustomerUpdate>()
                .classifier(classifier)
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<Transaction> transactionStaxEventItemReader(
            @Value("#{jobParameters['transactionInput']}")Resource resource){
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Transaction.class);
        return new StaxEventItemReaderBuilder<Transaction>()
                .name("transactionStaxEventItemReader")
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(jaxb2Marshaller)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> transactionItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (TRANSACTION_ID, " +
                        "ACCOUNT_ACCOUNT_ID, " +
                        "DESCRIPTION, " +
                        "CREDIT, " +
                        "DEBIT, " +
                        "TIMESTAMP) VALUES (:transactionId, " +
                        ":accountId, " +
                        ":description, " +
                        ":credit, " +
                        ":debit, " +
                        ":timestamp)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Transaction> applyTransactionItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Transaction>()
                .name("applyTransactionItemReader")
                .dataSource(dataSource)
                .sql("select transaction_id, " +
                        "account_account_id, " +
                        "description, " +
                        "credit, " +
                        "debit, " +
                        "timestamp " +
                        "from transaction " +
                        "order by timestamp")
                .rowMapper((resultSet, i) ->
                        new Transaction(
                                resultSet.getLong("transaction_id"),
                                resultSet.getLong("account_account_id"),
                                resultSet.getString("description"),
                                resultSet.getBigDecimal("credit"),
                                resultSet.getBigDecimal("debit"),
                                resultSet.getTimestamp("timestamp")))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> applyTransactionItemWriter(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .beanMapped()
                .sql("UPDATE ACCOUNT SET " +
                        "BALANCE = BALANCE + :transactionAmount " +
                        "WHERE ACCOUNT_ID = :accountId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Statement> statementItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Statement>()
                .name("statementItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM CUSTOMER")
                .rowMapper((resultSet, i) -> {
                    Customer customer = new Customer(resultSet.getLong("customer_id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("middle_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("address1"),
                            resultSet.getString("address2"),
                            resultSet.getString("city"),
                            resultSet.getString("state"),
                            resultSet.getString("postal_code"),
                            resultSet.getString("ssn"),
                            resultSet.getString("email_address"),
                            resultSet.getString("home_phone"),
                            resultSet.getString("cell_phone"),
                            resultSet.getString("work_phone"),
                            resultSet.getInt("notification_pref"));

                    return new Statement(customer);
                }).build();
    }

    @Bean
    public FlatFileItemWriter<Statement> individualStatementItemWriter(){
        return new FlatFileItemWriterBuilder<Statement>()
                .name("individualStatementItemWriter")
                .headerCallback(new StatementHeaderCallback())
                .lineAggregator(new StatementLineAggregator())
                .build();
    }

    @Bean
    public MultiResourceItemWriter<Statement> statementMultiResourceItemWriter(){
        return new MultiResourceItemWriterBuilder<Statement>()
                .name("statementMultiResourceItemWriter")
                .delegate(individualStatementItemWriter())
                .resource(new FileSystemResource("input/statement"))
                .itemCountLimitPerResource(1)
                .resourceSuffixCreator(index -> index + ".txt")
                .build();
    }
    @Bean
    public Step customerUpdateStep() throws Exception {
        return stepBuilderFactory.get("customerUpdateStep")
                .<CustomerUpdate, CustomerUpdate>chunk(10)
                .reader(customerFlatFileItemReader(null))
                .processor(customerIdValidatorItemProcessor(null))
                .writer(customerUpdateItemWriter())
                .build();
    }

    @Bean
    public Step transactionStep() {
        return stepBuilderFactory.get("transactionStep")
                .<Transaction, Transaction>chunk(10)
                .reader(transactionStaxEventItemReader(null))
                .writer(transactionItemWriter(null))
                .build();
    }

    @Bean
    public Step applyTransactionStep() {
        return stepBuilderFactory.get("applyTransactionStep")
                .<Transaction, Transaction>chunk(10)
                .reader(applyTransactionItemReader(null))
                .writer(applyTransactionItemWriter(null))
                .build();
    }


    @Bean Step generateStatement(AccountItemProcessor accountItemProcessor){
        return stepBuilderFactory.get("generateStatement")
                .<Statement, Statement>chunk(1)
                .reader(statementItemReader(null))
                .processor(accountItemProcessor)
                .writer(statementMultiResourceItemWriter())
                .build();
    }
    @Bean
    public Job customerUpdateJob() throws Exception {
        return jobBuilderFactory.get("customerUpdateJob")
                .start(customerUpdateStep())
                .next(transactionStep())
                .next(applyTransactionStep())
                .next(generateStatement(null))
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
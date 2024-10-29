create table if not exists foo_job_execution_seq
(
    ID         bigint not null,
    UNIQUE_KEY char   not null,
    constraint UNIQUE_KEY_UN
    unique (UNIQUE_KEY)
    );

create table if not exists foo_job_instance
(
    JOB_INSTANCE_ID bigint       not null
    primary key,
    VERSION         bigint       null,
    JOB_NAME        varchar(100) not null,
    JOB_KEY         varchar(32)  not null,
    constraint JOB_INST_UN
    unique (JOB_NAME, JOB_KEY)
    );

create table if not exists foo_job_execution
(
    JOB_EXECUTION_ID           bigint        not null
    primary key,
    VERSION                    bigint        null,
    JOB_INSTANCE_ID            bigint        not null,
    CREATE_TIME                datetime(6)   not null,
    START_TIME                 datetime(6)   null,
    END_TIME                   datetime(6)   null,
    STATUS                     varchar(10)   null,
    EXIT_CODE                  varchar(2500) null,
    EXIT_MESSAGE               varchar(2500) null,
    LAST_UPDATED               datetime(6)   null,
    JOB_CONFIGURATION_LOCATION varchar(2500) null,
    constraint JOB_INST_EXEC_FK
    foreign key (JOB_INSTANCE_ID) references foo_job_instance (JOB_INSTANCE_ID)
    );

create table if not exists foo_job_execution_context
(
    JOB_EXECUTION_ID   bigint        not null
    primary key,
    SHORT_CONTEXT      varchar(2500) not null,
    SERIALIZED_CONTEXT text          null,
    constraint JOB_EXEC_CTX_FK
    foreign key (JOB_EXECUTION_ID) references foo_job_execution (JOB_EXECUTION_ID)
    );

create table if not exists foo_job_execution_params
(
    JOB_EXECUTION_ID bigint       not null,
    TYPE_CD          varchar(6)   not null,
    KEY_NAME         varchar(100) not null,
    STRING_VAL       varchar(250) null,
    DATE_VAL         datetime(6)  null,
    LONG_VAL         bigint       null,
    DOUBLE_VAL       double       null,
    IDENTIFYING      char         not null,
    constraint JOB_EXEC_PARAMS_FK
    foreign key (JOB_EXECUTION_ID) references foo_job_execution (JOB_EXECUTION_ID)
    );

create table if not exists foo_job_seq
(
    ID         bigint not null,
    UNIQUE_KEY char   not null,
    constraint UNIQUE_KEY_UN
    unique (UNIQUE_KEY)
    );

create table if not exists foo_step_execution
(
    STEP_EXECUTION_ID  bigint        not null
    primary key,
    VERSION            bigint        not null,
    STEP_NAME          varchar(100)  not null,
    JOB_EXECUTION_ID   bigint        not null,
    START_TIME         datetime(6)   not null,
    END_TIME           datetime(6)   null,
    STATUS             varchar(10)   null,
    COMMIT_COUNT       bigint        null,
    READ_COUNT         bigint        null,
    FILTER_COUNT       bigint        null,
    WRITE_COUNT        bigint        null,
    READ_SKIP_COUNT    bigint        null,
    WRITE_SKIP_COUNT   bigint        null,
    PROCESS_SKIP_COUNT bigint        null,
    ROLLBACK_COUNT     bigint        null,
    EXIT_CODE          varchar(2500) null,
    EXIT_MESSAGE       varchar(2500) null,
    LAST_UPDATED       datetime(6)   null,
    constraint JOB_EXEC_STEP_FK
    foreign key (JOB_EXECUTION_ID) references foo_job_execution (JOB_EXECUTION_ID)
    );

create table if not exists foo_step_execution_context
(
    STEP_EXECUTION_ID  bigint        not null
    primary key,
    SHORT_CONTEXT      varchar(2500) not null,
    SERIALIZED_CONTEXT text          null,
    constraint STEP_EXEC_CTX_FK
    foreign key (STEP_EXECUTION_ID) references foo_step_execution (STEP_EXECUTION_ID)
    );

create table if not exists foo_step_execution_seq
(
    ID         bigint not null,
    UNIQUE_KEY char   not null,
    constraint UNIQUE_KEY_UN
    unique (UNIQUE_KEY)
    );


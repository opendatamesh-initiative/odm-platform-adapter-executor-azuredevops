-- Postgres 11;

CREATE SCHEMA IF NOT EXISTS "ODMEXECUTOR";
CREATE SEQUENCE "ODMEXECUTOR".HIBERNATE_SEQUENCE START WITH 1;

CREATE TABLE "ODMEXECUTOR"."PIPELINE_RUNS"(

    "TASK_ID" BIGINT PRIMARY KEY,
    "RUN_ID" BIGINT,

    "ORGANIZATION" VARCHAR(255),
    "PROJECT" VARCHAR(255),
    "PIPELINE_ID" VARCHAR(255),
    "PIPELINE_NAME" VARCHAR(255),

    "STATUS" VARCHAR(255),
    "RESULT" VARCHAR(255),
    "VARIABLES" VARCHAR(5000),
    "TEMPLATE_PARAMETERS" VARCHAR(5000),

    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP

);

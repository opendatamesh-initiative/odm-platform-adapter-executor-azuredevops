-- Postgres 11;

CREATE SCHEMA IF NOT EXISTS "ODMEXECUTOR";
CREATE SEQUENCE "ODMEXECUTOR".HIBERNATE_SEQUENCE START WITH 1;

CREATE TABLE "ODMEXECUTOR"."PIPELINE_RUNS"(
    "TASKID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,

    "RUNID" BIGINT,
    "ORGANIZATION" VARCHAR(255),
    "PROJECT" VARCHAR(255),
    "PIPELINEID" VARCHAR(255),
    "STATUS" VARCHAR(255),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);

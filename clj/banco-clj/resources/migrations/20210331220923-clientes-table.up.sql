CREATE TABLE "clientes" (
  "id" SERIAL PRIMARY KEY,
  "nome" varchar(200),
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "deleted_at" timestamp
);

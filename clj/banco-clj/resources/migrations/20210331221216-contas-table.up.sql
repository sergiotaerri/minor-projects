CREATE TABLE "contas" (
  "id" SERIAL PRIMARY KEY,
  "tipo" integer,
  "credito" float,
  "cliente_id" integer,
  "created_at" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  "deleted_at" timestamp
);
--;;

ALTER TABLE "contas" ADD FOREIGN KEY ("cliente_id") REFERENCES "clientes" ("id");

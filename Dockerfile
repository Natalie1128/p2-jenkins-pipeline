FROM alpine:latest

# RUN apk update && apk add sqlite

WORKDIR /var/lib/sqlite/data/

COPY expenses_system_db.db .

# CMD ["sqlite3", "expenses_system_db.db"]
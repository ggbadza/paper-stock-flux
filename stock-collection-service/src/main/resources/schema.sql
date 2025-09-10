CREATE TABLE IF NOT EXISTS tracked_stocks (
    ticker VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN,
    created_date DATETIME,
    modified_date DATETIME
);

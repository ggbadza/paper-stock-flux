CREATE TABLE IF NOT EXISTS tracked_stocks (
    ticker VARCHAR(255) PRIMARY KEY,
    stock_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN,
    created_date DATETIME,
    modified_date DATETIME
);

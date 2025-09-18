CREATE TABLE IF NOT EXISTS tracked_kospi_stocks (
    ticker VARCHAR(255) PRIMARY KEY,
    stock_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN,
    created_date DATETIME,
    modified_date DATETIME
);

CREATE TABLE IF NOT EXISTS tracked_nasdaq_stocks (
    ticker VARCHAR(255) PRIMARY KEY,
    stock_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN,
    created_date DATETIME,
    modified_date DATETIME
    );

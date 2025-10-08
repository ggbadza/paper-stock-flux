-- KOSPI 거래 정보 테이블
CREATE TABLE IF NOT EXISTS kospi_trade (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    trade_type VARCHAR(10)
    );

-- KOSPI 호가 정보 테이블
CREATE TABLE IF NOT EXISTS kospi_order_book (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    -- 매수 호가 (Bids) 1 ~ 10
    bid_price_1 BIGINT,
    bid_volume_1 BIGINT,
    bid_price_2 BIGINT,
    bid_volume_2 BIGINT,
    bid_price_3 BIGINT,
    bid_volume_3 BIGINT,
    bid_price_4 BIGINT,
    bid_volume_4 BIGINT,
    bid_price_5 BIGINT,
    bid_volume_5 BIGINT,
    bid_price_6 BIGINT,
    bid_volume_6 BIGINT,
    bid_price_7 BIGINT,
    bid_volume_7 BIGINT,
    bid_price_8 BIGINT,
    bid_volume_8 BIGINT,
    bid_price_9 BIGINT,
    bid_volume_9 BIGINT,
    bid_price_10 BIGINT,
    bid_volume_10 BIGINT,
    -- 매도 호가 (Asks) 1 ~ 10
    ask_price_1 BIGINT,
    ask_volume_1 BIGINT,
    ask_price_2 BIGINT,
    ask_volume_2 BIGINT,
    ask_price_3 BIGINT,
    ask_volume_3 BIGINT,
    ask_price_4 BIGINT,
    ask_volume_4 BIGINT,
    ask_price_5 BIGINT,
    ask_volume_5 BIGINT,
    ask_price_6 BIGINT,
    ask_volume_6 BIGINT,
    ask_price_7 BIGINT,
    ask_volume_7 BIGINT,
    ask_price_8 BIGINT,
    ask_volume_8 BIGINT,
    ask_price_9 BIGINT,
    ask_volume_9 BIGINT,
    ask_price_10 BIGINT,
    ask_volume_10 BIGINT
    );

-- NASDAQ 거래 정보 테이블
CREATE TABLE IF NOT EXISTS nasdaq_trade (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    price DECIMAL NOT NULL,
    volume BIGINT NOT NULL,
    trade_type VARCHAR(10)
    );

-- NASDAQ 호가 정보 테이블
CREATE TABLE IF NOT EXISTS nasdaq_order_book (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    bid_price_1  DECIMAL,
    bid_volume_1 BIGINT,
    ask_price_1  DECIMAL,
    ask_volume_1 BIGINT
    );

-- 각 Hypertable에 (ticker, time 순서) 복합 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_kospi_trade_ticker_time ON kospi_trade (ticker, time DESC);
CREATE INDEX IF NOT EXISTS idx_kospi_order_book_ticker_time ON kospi_order_book (ticker, time DESC);
CREATE INDEX IF NOT EXISTS idx_nasdaq_trade_ticker_time ON nasdaq_trade (ticker, time DESC);
CREATE INDEX IF NOT EXISTS idx_nasdaq_order_book_ticker_time ON nasdaq_order_book (ticker, time DESC);

-- Hypertable로 변환 (테이블이 비어있을 때 한 번만 실행)
SELECT create_hypertable('kospi_trade', 'time', if_not_exists => TRUE);
SELECT create_hypertable('kospi_order_book', 'time', if_not_exists => TRUE);
SELECT create_hypertable('nasdaq_trade', 'time', if_not_exists => TRUE);
SELECT create_hypertable('nasdaq_order_book', 'time', if_not_exists => TRUE);
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
    bid_price1 BIGINT,
    bid_volume1 BIGINT,
    bid_price2 BIGINT,
    bid_volume2 BIGINT,
    bid_price3 BIGINT,
    bid_volume3 BIGINT,
    bid_price4 BIGINT,
    bid_volume4 BIGINT,
    bid_price5 BIGINT,
    bid_volume5 BIGINT,
    bid_price6 BIGINT,
    bid_volume6 BIGINT,
    bid_price7 BIGINT,
    bid_volume7 BIGINT,
    bid_price8 BIGINT,
    bid_volume8 BIGINT,
    bid_price9 BIGINT,
    bid_volume9 BIGINT,
    bid_price10 BIGINT,
    bid_volume10 BIGINT,
    -- 매도 호가 (Asks) 1 ~ 10
    ask_price1 BIGINT,
    ask_volume1 BIGINT,
    ask_price2 BIGINT,
    ask_volume2 BIGINT,
    ask_price3 BIGINT,
    ask_volume3 BIGINT,
    ask_price4 BIGINT,
    ask_volume4 BIGINT,
    ask_price5 BIGINT,
    ask_volume5 BIGINT,
    ask_price6 BIGINT,
    ask_volume6 BIGINT,
    ask_price7 BIGINT,
    ask_volume7 BIGINT,
    ask_price8 BIGINT,
    ask_volume8 BIGINT,
    ask_price9 BIGINT,
    ask_volume9 BIGINT,
    ask_price10 BIGINT,
    ask_volume10 BIGINT
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
    bid_price1  DECIMAL,
    bid_volume1 BIGINT,
    ask_price1  DECIMAL,
    ask_volume1 BIGINT
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
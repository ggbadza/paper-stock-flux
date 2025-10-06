-- KOSPI 거래 정보 테이블 생성
CREATE TABLE IF NOT EXISTS kospi_trade (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    trade_type VARCHAR(10)
    );

-- KOSPI 호가 정보 테이블 생성
CREATE TABLE IF NOT EXISTS kospi_order_book (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    order_type VARCHAR(10)
    );

-- NASDAQ 거래 정보 테이블 생성
CREATE TABLE IF NOT EXISTS nasdaq_trade (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    trade_type VARCHAR(10)
    );

-- NASDAQ 호가 정보 테이블 생성
CREATE TABLE IF NOT EXISTS nasdaq_order_book (
    time TIMESTAMPTZ NOT NULL,
    ticker VARCHAR(50) NOT NULL,
    price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    order_type VARCHAR(10)
    );

-- 각 Hypertable에 (ticker, time 순서) 복합 인덱스 추가
CREATE INDEX ON kospi_trade (ticker, time DESC);
CREATE INDEX ON kospi_order_book (ticker, time DESC);
CREATE INDEX ON nasdaq_trade (ticker, time DESC);
CREATE INDEX ON nasdaq_order_book (ticker, time DESC);

-- Hypertable로 변환 (테이블이 비어있을 때 한 번만 실행)
SELECT create_hypertable('kospi_trade', 'time', if_not_exists => TRUE);
SELECT create_hypertable('kospi_order_book', 'time', if_not_exists => TRUE);
SELECT create_hypertable('nasdaq_trade', 'time', if_not_exists => TRUE);
SELECT create_hypertable('nasdaq_order_book', 'time', if_not_exists => TRUE);


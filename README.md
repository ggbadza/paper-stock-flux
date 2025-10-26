# Paper Stock Flux

## 프로젝트 개요

본 프로젝트는 실시간 주식 데이터를 수집, 저장하고 사용자에게 웹소켓을 통해 실시간으로 전달하는 MSA 기반의 시스템입니다. Spring WebFlux와 Kotlin을 사용하여 비동기 및 논블로킹 방식으로 데이터를 처리하여 높은 성능과 확장성을 목표로 합니다.
(개발중)

## 아키텍처

전체 시스템은 아래와 같은 마이크로서비스들로 구성되어 있으며, 각 서비스는 독립적으로 배포 및 확장이 가능합니다. 서비스 간의 데이터 교환은 Apache Kafka를 통해 이루어집니다.

```
[외부 증권사 API] <-- (WebSocket) --> [Stock Collection Service] --> [Apache Kafka]
                                                                    |
                                                                    +--> [Stock Persister Service] --> [TimescaleDB]
                                                                    |
                                                                    +--> [Stock Relay Service] -- (WebSocket) --> [Client]
```

## 주요 기술 스택

- **Framework**: Spring WebFlux
- **Language**: Kotlin
- **Message Queue**: Apache Kafka
- **Database**: TimescaleDB

## 서비스 상세 설명

### 1. 주식 수집 서버 (stock-collection-service)

- 외부 증권사 API와 웹소켓 연결을 맺고, 실시간으로 주식 체결 데이터와 호가 데이터를 수신합니다.
- 수신한 데이터를 Kafka 토픽으로 전송하여 다른 서비스들이 사용할 수 있도록 합니다.
- KOSPI, NASDAQ 등 여러 시장의 데이터를 동시에 처리할 수 있도록 설계되었습니다.

### 2. 주식 저장 서버 (stock-persister-service)

- Kafka로부터 주식 데이터를 구독합니다.
- 전달받은 데이터를 TimescaleDB에 저장하여 시계열 데이터 분석 및 조회를 위한 기반을 마련합니다.
- 데이터 종류(체결, 호가)에 따라 각각 다른 테이블에 저장합니다.

### 3. 주식 전달 서버 (stock-relay-service)

- Kafka로부터 주식 데이터를 구독합니다.
- 데이터를 요청하는 클라이언트와 웹소켓 연결을 맺고, 실시간으로 주식 데이터를 전달합니다.
- 클라이언트는 이 서비스를 통해 실시간으로 변동하는 주가, 호가 등의 정보를 받아볼 수 있습니다.

## 실행 방법

프로젝트 루트 디렉토리의 `docker-compose.yml` 파일을 사용하여 모든 서비스를 한 번에 실행할 수 있습니다.

```bash
docker-compose up -d
```


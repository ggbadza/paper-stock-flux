package com.ggbadza.stock_persister_service.common

import com.ggbadza.stock_persister_service.kospi.dto.KospiOrderBookDto
import com.ggbadza.stock_persister_service.kospi.dto.KospiTradeDto
import com.ggbadza.stock_persister_service.kospi.entity.KospiOrderBookEntity
import com.ggbadza.stock_persister_service.kospi.entity.KospiTradeEntity
import com.ggbadza.stock_persister_service.nasdaq.dto.NasdaqOrderBookDto
import com.ggbadza.stock_persister_service.nasdaq.dto.NasdaqTradeDto
import com.ggbadza.stock_persister_service.nasdaq.entity.NasdaqOrderBookEntity
import com.ggbadza.stock_persister_service.nasdaq.entity.NasdaqTradeEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 카프카로부터 수신한 DTO(Data Transfer Object)를 데이터베이스에 저장하기 위한
 * Entity 객체로 변환하는 역할을 수행
 */
@Component
class StockDataMapper {

    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss")
    }

    /**
     * 코스피 체결가 DTO(KospiTradeDto)를 코스피 체결가 Entity(KospiTradeEntity)로 변환
     * 날짜(bsopDate)와 시간(stckCntgHour) 필드를 조합하여 완전한 시간(time) 필드를 생성
     */
    fun toEntity(dto: KospiTradeDto): KospiTradeEntity {
        val dateTimeString = dto.bsopDate + dto.stckCntgHour

        return KospiTradeEntity(
            time = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER),
            ticker = dto.mkscShrnIscd,
            price = dto.stckPrpr.toLong(),
            volume = dto.cntgVol.toLong(),
            tradeType = dto.ccldDvsn
        )
    }

    /**
     * 코스피 호가 DTO(KospiOrderBookDto)를 코스피 호가 Entity(KospiOrderBookEntity)로 변환
     * 호가 시간(bsopHour)에 현재 날짜를 조합하여 완전한 시간(time) 필드를 생성
     * 10단계의 매수/매도 호가 및 잔량을 Long 타입으로 변환하며, 실패 시 0으로 처리
     */
    fun toEntity(dto: KospiOrderBookDto): KospiOrderBookEntity {
        // 서버의 오늘 날짜로 세팅
        val today = LocalDateTime.now().toLocalDate()
        // BSOP_HOUR (HHmmss) 문자열을 LocalTime으로 파싱
        val localTime = LocalTime.parse(dto.bsopHour, TIME_FORMATTER)
        val time = LocalDateTime.of(today, localTime)

        return KospiOrderBookEntity(
            time = time,
            ticker = dto.mkscShrnIscd,
            // 매수 호가 가격 및 잔량 (Long 타입으로 변환, 변환 실패 시 0L 처리)
            bidPrice1 = dto.bidp1?.toLongOrNull() ?: 0L,
            bidVolume1 = dto.bidpRsqn1?.toLongOrNull() ?: 0L,
            bidPrice2 = dto.bidp2?.toLongOrNull() ?: 0L,
            bidVolume2 = dto.bidpRsqn2?.toLongOrNull() ?: 0L,
            bidPrice3 = dto.bidp3?.toLongOrNull() ?: 0L,
            bidVolume3 = dto.bidpRsqn3?.toLongOrNull() ?: 0L,
            bidPrice4 = dto.bidp4?.toLongOrNull() ?: 0L,
            bidVolume4 = dto.bidpRsqn4?.toLongOrNull() ?: 0L,
            bidPrice5 = dto.bidp5?.toLongOrNull() ?: 0L,
            bidVolume5 = dto.bidpRsqn5?.toLongOrNull() ?: 0L,
            bidPrice6 = dto.bidp6?.toLongOrNull() ?: 0L,
            bidVolume6 = dto.bidpRsqn6?.toLongOrNull() ?: 0L,
            bidPrice7 = dto.bidp7?.toLongOrNull() ?: 0L,
            bidVolume7 = dto.bidpRsqn7?.toLongOrNull() ?: 0L,
            bidPrice8 = dto.bidp8?.toLongOrNull() ?: 0L,
            bidVolume8 = dto.bidpRsqn8?.toLongOrNull() ?: 0L,
            bidPrice9 = dto.bidp9?.toLongOrNull() ?: 0L,
            bidVolume9 = dto.bidpRsqn9?.toLongOrNull() ?: 0L,
            bidPrice10 = dto.bidp10?.toLongOrNull() ?: 0L,
            bidVolume10 = dto.bidpRsqn10?.toLongOrNull() ?: 0L,

            // 매도 호가 가격 및 잔량 (Long 타입으로 변환, 변환 실패 시 0L 처리)
            askPrice1 = dto.askp1?.toLongOrNull() ?: 0L,
            askVolume1 = dto.askpRsqn1?.toLongOrNull() ?: 0L,
            askPrice2 = dto.askp2?.toLongOrNull() ?: 0L,
            askVolume2 = dto.askpRsqn2?.toLongOrNull() ?: 0L,
            askPrice3 = dto.askp3?.toLongOrNull() ?: 0L,
            askVolume3 = dto.askpRsqn3?.toLongOrNull() ?: 0L,
            askPrice4 = dto.askp4?.toLongOrNull() ?: 0L,
            askVolume4 = dto.askpRsqn4?.toLongOrNull() ?: 0L,
            askPrice5 = dto.askp5?.toLongOrNull() ?: 0L,
            askVolume5 = dto.askpRsqn5?.toLongOrNull() ?: 0L,
            askPrice6 = dto.askp6?.toLongOrNull() ?: 0L,
            askVolume6 = dto.askpRsqn6?.toLongOrNull() ?: 0L,
            askPrice7 = dto.askp7?.toLongOrNull() ?: 0L,
            askVolume7 = dto.askpRsqn7?.toLongOrNull() ?: 0L,
            askPrice8 = dto.askp8?.toLongOrNull() ?: 0L,
            askVolume8 = dto.askpRsqn8?.toLongOrNull() ?: 0L,
            askPrice9 = dto.askp9?.toLongOrNull() ?: 0L,
            askVolume9 = dto.askpRsqn9?.toLongOrNull() ?: 0L,
            askPrice10 = dto.askp10?.toLongOrNull() ?: 0L,
            askVolume10 = dto.askpRsqn10?.toLongOrNull() ?: 0L
        )
    }

    /**
     * 나스닥 체결가 DTO(NasdaqTradeDto)를 나스닥 체결가 Entity(NasdaqTradeEntity)로 변환
     * 날짜(kymd)와 시간(khms) 필드를 조합하여 완전한 시간(time) 필드를 생성
     * 가격(last)은 소수점 위치(zdiv)를 기준으로 BigDecimal로 변환
     * 거래 타입(tradeType)은 bivl, asvl 필드의 존재 여부에 따라 동적으로 생성
     */
    fun toEntity(dto: NasdaqTradeDto): NasdaqTradeEntity {
        val dateTimeString = dto.kymd + dto.khms
        // 매도 인 경우 "1", 매수 인 경우 "2", 그 외는 "0"
        val tradeSuffix =  if(!dto.bivl.isBlank()) "1" else if(!dto.asvl.isBlank()) "2" else "0"

        return NasdaqTradeEntity(
            time = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER),
            ticker = dto.symb,
            price = dto.last.toBigDecimal(),
            volume = dto.evol.toLong(),
            tradeType = dto.mtyp+tradeSuffix
        )
    }

    /**
     * 나스닥 호가 DTO(NasdaqOrderBookDto)를 나스닥 호가 Entity(NasdaqOrderBookEntity)로 변환
     * 날짜(kymd)와 시간(khms) 필드를 조합하여 완전한 시간(time) 필드를 생성
     * 가격(pbid1, pask1)은 소수점 위치(zdiv)를 기준으로 BigDecimal로 변환하며, 실패 시 0으로 처리
     */
    fun toEntity(dto: NasdaqOrderBookDto): NasdaqOrderBookEntity {
        val dateTimeString = dto.kymd + dto.khms
        return NasdaqOrderBookEntity(
            time = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER),
            ticker = dto.symb,
            bidPrice1 = dto.pbid1.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            bidVolume1 = dto.vbid1.toLongOrNull() ?: 0L,
            askPrice1 = dto.pask1.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            askVolume1 = dto.vask1.toLongOrNull() ?: 0L,

        )
    }
}

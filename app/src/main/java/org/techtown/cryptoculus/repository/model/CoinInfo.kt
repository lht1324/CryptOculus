package org.techtown.cryptoculus.repository.model

import org.techtown.cryptoculus.pojo.Ticker

class CoinInfo {
    var exchange = "" // 거래소
    var coinNameOriginal = "" // 통신 시 받아오는 종목 코드 원본
    var coinName = "" // 가공 후 저장되는 종목 코드
    // var coinNameKorean = "" // 원본으로 받아오는 한글명
    var coinViewCheck = true
    var ticker: Ticker? = null
}
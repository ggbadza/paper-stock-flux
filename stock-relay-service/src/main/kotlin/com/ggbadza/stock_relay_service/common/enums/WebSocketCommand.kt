package com.ggbadza.stock_relay_service.common.enums

enum class WebSocketCommand (val command: String) {
    SUBSCRIBE("SUBSCRIBE"),
    UNSUBSCRIBE("UNSUBSCRIBE");

    companion object {
        /**
         * 문자열을 입력받아 해당하는 WebSocketCommand Enum을 찾아 반환합니다.
         * 해당하는 Enum이 없으면 null을 반환합니다.
         * @param command 명령어 문자열 (대소문자 무시)
         * @return WebSocketCommand 또는 null
         */
        fun from(command: String?): WebSocketCommand? {
            // values()는 배열을 반환
            return values().find { it.command.equals(command, ignoreCase = true) }
        }
    }
}
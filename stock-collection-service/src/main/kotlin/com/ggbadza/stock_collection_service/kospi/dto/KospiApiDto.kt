package com.ggbadza.stock_collection_service.kospi.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ApprovalRequest(
    @JsonProperty("grant_type")
    val grantType: String = "client_credentials",
    @JsonProperty("appkey")
    val appKey: String,
    @JsonProperty("secretkey")
    val secretKey: String
)

data class ApprovalResponse(
    @JsonProperty("approval_key")
    val approvalKey: String
)

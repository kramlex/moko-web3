/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.web3.requests

import com.soywiz.kbignum.BigInt
import dev.icerock.moko.web3.*
import dev.icerock.moko.web3.entity.Transaction
import dev.icerock.moko.web3.entity.TransactionReceipt
import dev.icerock.moko.web3.requests.polling.shortPollingUntilNotNull
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement


suspend fun <T> Web3Executor.executeBatch(vararg requests: Web3RpcRequest<*, T>): List<T> =
    executeBatch(requests.toList())

suspend fun Web3Executor.getTransaction(
    transactionHash: TransactionHash
): Transaction = executeBatch(Web3Requests.getTransaction(transactionHash)).first()

suspend fun Web3Executor.getTransactionReceipt(
    transactionHash: TransactionHash
): TransactionReceipt? = executeBatch(Web3Requests.getTransactionReceipt(transactionHash)).first()

suspend fun Web3Executor.getNativeBalance(
    walletAddress: WalletAddress,
    blockState: BlockState = BlockState.Latest
): BigInt = executeBatch(Web3Requests.getNativeBalance(walletAddress, blockState)).first()

suspend fun Web3Executor.getNativeTransactionCount(
    walletAddress: WalletAddress,
    blockState: BlockState = BlockState.Pending
): BigInt = executeBatch(Web3Requests.getNativeTransactionCount(walletAddress, blockState)).first()

suspend fun <T> Web3Executor.call(
    transactionCall: JsonElement,
    responseDataSerializer: KSerializer<T>,
    blockState: BlockState = BlockState.Latest,
): T = executeBatch(Web3Requests.call(transactionCall, responseDataSerializer, blockState)).first()

suspend fun Web3Executor.send(
    signedTransaction: String
): TransactionHash = executeBatch(Web3Requests.send(signedTransaction)).first()

suspend fun Web3Executor.getGasPrice(): BigInt = executeBatch(Web3Requests.getGasPrice()).first()

suspend fun Web3Executor.waitForTransactionReceipt(
    hash: TransactionHash,
    // one minute is the default timeout
    timeOutMillis: Long? = 1 * 60 * 1_000,
    // interval is the default interval,
    intervalMillis: Long = 1_000
): TransactionReceipt =
    shortPollingUntilNotNull(timeOutMillis, intervalMillis) {
        getTransactionReceipt(hash)
    }
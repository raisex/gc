package com.wavesplatform.it

import com.wavesplatform.api.http.assets.{SignedIssueV1Request, SignedIssueV2Request}
import com.wavesplatform.it.util._
import com.wavesplatform.state.DataEntry
import com.wavesplatform.transaction.assets.{IssueTransactionV1, IssueTransactionV2}
import com.wavesplatform.state._
import com.wavesplatform.transaction.smart.script.{Script, ScriptCompiler}
import com.wavesplatform.utils.Base58

package object sync {
  val smartFee                   = 0.06.GXB
  val minFee                        = 0.02.GXB
  val leasingFee                 = 0.02.GXB
  val issueFee                   = 1000.GXB
  val burnFee                    = 10.GXB
  val sponsorFee                 = 10.GXB
  val setAssetScriptFee          = 10.GXB
  val setScriptFee               = 0.04.GXB
  val transferAmount             = 10.GXB
  val leasingAmount              = transferAmount
  val issueAmount                = transferAmount
  val massTransferFeePerTransfer = 0.005.GXB
  val someAssetAmount            = 9999999999999L
  val matcherFee                 = 0.04.GXB
  val orderFee                   = matcherFee
  val smartMatcherFee            = 0.06.GXB
  val smartMinFee                = minFee + smartFee

  def calcDataFee(data: List[DataEntry[_]]): Long = {
    val dataSize = data.map(_.toBytes.length).sum + 128
    if (dataSize > 1024) {
      minFee * (dataSize / 1024 + 1)
    } else minFee
  }

  def calcMassTransferFee(numberOfRecipients: Int): Long = {
    minFee + massTransferFeePerTransfer * (numberOfRecipients + 1)
  }

  val supportedVersions: List[Byte] = List(1, 2)

  val script: Script       = ScriptCompiler(s"""true""".stripMargin, isAssetScript = false).explicitGet()._1
  val scriptBase64: String = script.bytes.value.base64

  val errNotAllowedByToken = "Transaction is not allowed by token-script"

  def createSignedIssueRequest(tx: IssueTransactionV1): SignedIssueV1Request = {
    import tx._
    SignedIssueV1Request(
      Base58.encode(tx.sender.publicKey),
      new String(name),
      new String(description),
      quantity,
      decimals,
      reissuable,
      fee,
      timestamp,
      signature.base58
    )
  }

  def createSignedIssueRequest(tx: IssueTransactionV2): SignedIssueV2Request = {
    import tx._
    SignedIssueV2Request(
      2.toByte,
      Base58.encode(tx.sender.publicKey),
      new String(name),
      new String(description),
      quantity,
      decimals,
      reissuable,
      fee,
      timestamp,
      proofs.proofs.map(_.toString),
      tx.script.map(_.bytes().base64)
    )
  }

}

package com.wavesplatform.it.sync.transactions

import com.wavesplatform.it.api.SyncHttpApi._
import com.wavesplatform.it.api.PaymentRequest
import com.wavesplatform.it.transactions.BaseTransactionSuite
import com.wavesplatform.it.util._
import org.scalatest.prop.TableDrivenPropertyChecks

class PaymentTransactionSuite extends BaseTransactionSuite with TableDrivenPropertyChecks {

  private val paymentAmount = 5.GXB
  private val defaulFee     = 1.GXB

  test("GXB payment changes GXB balances and eff.b.") {

    val (firstBalance, firstEffBalance)   = notMiner.accountBalances(firstAddress)
    val (secondBalance, secondEffBalance) = notMiner.accountBalances(secondAddress)

    val transferId = sender.payment(firstAddress, secondAddress, paymentAmount, defaulFee).id
    nodes.waitForHeightAriseAndTxPresent(transferId)
    notMiner.assertBalances(firstAddress, firstBalance - paymentAmount - defaulFee, firstEffBalance - paymentAmount - defaulFee)
    notMiner.assertBalances(secondAddress, secondBalance + paymentAmount, secondEffBalance + paymentAmount)
  }

  val payment = PaymentRequest(5.GXB, 1.GXB, firstAddress, secondAddress)
  val endpoints =
    Table("/GXB/payment/signature", "/GXB/create-signed-payment", "/GXB/external-payment", "/GXB/broadcast-signed-payment")
  forAll(endpoints) { (endpoint: String) =>
    test(s"obsolete endpoints respond with BadRequest. Endpoint:$endpoint") {
      val errorMessage = "This API is no longer supported"
      assertBadRequestAndMessage(sender.postJson(endpoint, payment), errorMessage)
    }
  }
}

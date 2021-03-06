package com.wavesplatform.state.diffs

import com.wavesplatform.features.BlockchainFeatures
import com.wavesplatform.features.FeatureProvider._
import com.wavesplatform.lang.Version
import com.wavesplatform.lang.v1.DenyDuplicateVarNames
import com.wavesplatform.lang.v1.compiler.Terms.EXPR
import com.wavesplatform.state.{Blockchain, Diff, LeaseBalance, Portfolio}
import com.wavesplatform.transaction.ValidationError
import com.wavesplatform.transaction.ValidationError.GenericError
import com.wavesplatform.transaction.smart.SetScriptTransaction
import com.wavesplatform.utils.varNames

import scala.util.Right

object SetScriptTransactionDiff {
  def apply(blockchain: Blockchain, height: Int)(tx: SetScriptTransaction): Either[ValidationError, Diff] = {
    val scriptOpt = tx.script
    for {
      _ <- scriptOpt.fold(Right(()): Either[ValidationError, Unit]) { script =>
        if (blockchain.isFeatureActivated(BlockchainFeatures.SmartAccountTrading, height)) {
          Right(())
        } else {
          val version = script.version
          if (version == Version.V1 || version == Version.V2)
            DenyDuplicateVarNames(version, varNames(version), script.expr.asInstanceOf[EXPR]).left.map(GenericError.apply)
          else Right(())
        }
      }
    } yield {
      Diff(
        height = height,
        tx = tx,
        portfolios = Map(tx.sender.toAddress -> Portfolio(-tx.fee, LeaseBalance.empty, Map.empty)),
        scripts = Map(tx.sender.toAddress    -> scriptOpt)
      )
    }
  }
}

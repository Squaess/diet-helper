package diethelper

import scala.util.Random
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

object LaminarExample {
  private val dataVar: Var[DataList] = Var(List.empty)
  private val dataSignal: Signal[DataList] = dataVar.signal

  def addDataItem(item: DataItem): Unit = {
    dataVar.update(items => items :+ item)
  }

  def removeDataItem(id: DataItemId): Unit = {
    dataVar.update(items => items.filterNot(_.id == id))
  }

  def appElement() = {
    div(
      h1("Live Chart"),
      renderTable(),
      renderDataList()
    )
  }

  def renderTable() = {
    table(
      thead(
        tr(
          th("Label"),
          th("Price"),
          th("Count"),
          th("Full Price"),
          th("Action")
        )
      ),
      tbody(
        children <-- dataSignal.split(_.id) { (id, initial, itemSignal) =>
          renderDataItem(id, itemSignal)
        }
      ),
      tfoot(
        tr(
          td(button("➕", onClick --> (_ => addDataItem(DataItem())))),
          td(),
          td(),
          td(
            child.text <-- dataSignal.map(data =>
              "%.2f".format(data.map(_.fullPrice).sum)
            )
          ),
          td()
        )
      )
    )
  }

  def renderDataItem(id: DataItemId, itemSignal: Signal[DataItem]) = {
    tr(
      td(
        inputForString(
          itemSignal.map(_.label),
          makeDataItemUpdater[String](
            id,
            { (item, newLabel) => item.copy(label = newLabel) }
          )
        )
      ),
      td(
        inputForDouble(
          itemSignal.map(_.price),
          makeDataItemUpdater[Double](
            id,
            { (item, newPrice) => item.copy(price = newPrice) }
          )
        )
      ),
      td(
        inputForInt(
          itemSignal.map(_.count),
          makeDataItemUpdater[Int](
            id,
            { (item, newCount) => item.copy(count = newCount) }
          )
        )
      ),
      td(child.text <-- itemSignal.map(item => "%.2f".format(item.fullPrice))),
      td(button("🗑️", onClick --> (_ => removeDataItem(id))))
    )
  }

  def inputForString(
      valueSignal: Signal[String],
      valueUpdater: Observer[String]
  ) = {
    input(
      typ := "text",
      value <-- valueSignal,
      onInput.mapToValue --> valueUpdater
    )
  }

  def inputForDouble(
      valueSignal: Signal[Double],
      valueUpdate: Observer[Double]
  ) = {
    val strValue = Var("")
    input(
      typ := "text",
      value <-- strValue,
      onInput.mapToValue --> strValue,
      valueSignal --> strValue.updater[Double] { (prevStr, newValue) =>
        if prevStr.toDoubleOption.contains(newValue) then prevStr
        else newValue.toString
      },
      strValue.signal --> { valueStr =>
        valueStr.toDoubleOption.foreach(valueUpdate.onNext)
      }
    )
  }

  def inputForInt(valueSignal: Signal[Int], valueUpdater: Observer[Int]) = {
    input(
      typ := "text",
      controlled(
        value <-- valueSignal.map(_.toString),
        onInput.mapToValue.map(_.toIntOption).collect { case Some(newCount) =>
          newCount
        } --> valueUpdater
      )
    )
  }

  def makeDataItemUpdater[A](
      id: DataItemId,
      f: (DataItem, A) => DataItem
  ): Observer[A] = {
    dataVar.updater { (data, newValue) =>
      data.map { item => if item.id == id then f(item, newValue) else item }
    }
  }

  def renderDataList() = {
    ul(
      children <-- dataSignal.split(_.id) { (id, initial, itemSignal) =>
        li(
          child.text <-- itemSignal.map(item => s"${item.count}: ${item.label} - ${item.price}")
        )
      }
    )
  }
}

final class DataItemId

case class DataItem(id: DataItemId, label: String, price: Double, count: Int) {
  def fullPrice: Double = price * count
}
object DataItem {
  def apply(): DataItem = {
    DataItem(DataItemId(), "?", Random.nextDouble(), Random.nextInt(10) + 1)
  }
}

type DataList = List[DataItem]

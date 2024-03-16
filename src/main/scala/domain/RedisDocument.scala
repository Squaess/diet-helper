package domain

trait RedisDocument {
  def table: String
  def id: String
}

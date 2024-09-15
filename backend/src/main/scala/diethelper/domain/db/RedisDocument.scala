package diethelper.domain.db

trait RedisDocument {
  def table: String
  def id: String
}

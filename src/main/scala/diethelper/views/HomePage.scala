package diethelper.views

import scalatags.Text.TypedTag
import scalatags.Text.all.*
import diethelper.views.htmx.HtmxAttributes
import diethelper.domain.db.Recipe

object HomePage {
  def generate(bodyContents: TypedTag[String]): TypedTag[String] = generate(List(bodyContents))
  def generate(bodyContents: List[TypedTag[String]] = List.empty): TypedTag[String] = {
    html(
      head(
        link(rel := "stylesheet", href := "https://cdn.jsdelivr.net/npm/@picocss/pico@1/css/pico.min.css"),
        script(src := "https://unpkg.com/htmx.org@1.9.10"),
        link(rel := "stylesheet", href := "/static/css/main.css")
      ),
      body(
        `class` := "container",
        div(
          HtmxAttributes.boost(),
          bodyContents
        )
      ),
      p(
        a(href := "/recipe", "Recipes"),
        a(href := "/product", "Products")
      )
    )
  }

  def viewRecipe(recipe: Recipe) = div(
    h1(recipe.name),
    div(
      div(s"Calories: ${recipe.calories}"),
      div(s"Description: ${recipe.description}")
    ),
    p(
      a(href := "/contacts", "Back")
    )
  )

  def listView(recipes: List[String], page: Int) = div(
    `class` := "container",
    form(
      table(
        `class` := "table",
        thead(
          tr(
            th("Name")
          )
        ),
        tbody(
          recipes.map(c =>
            tr(
              // td(input(`type` := "checkbox", name := "selected_contact_ids", value := c.id)),
              td(c),
              // td(
              //   a(
              //     href := "#",
              //     // HtmxAttributes.delete(s"/contacts/${c.id}"),
              //     HtmxAttributes.swap("outerHTML swap:1s"),
              //     HtmxAttributes.confirm("Are you sure you want to delete this contact?"),
              //     HtmxAttributes.target("closest tr"),
              //     "Delete"
              //   )
              // )
            ),
          )
        )
      ),
      div(
        style := "display: flex; justify-content: space-between",
        // button(
        //   style := "width: 160px",
        //   // HtmxAttributes.get(s"/contacts?page=${page}&q=${searchTerm}"),
        //   HtmxAttributes.target("closest tr"),
        //   HtmxAttributes.swap("outerHTML"),
        //   HtmxAttributes.select("tbody > tr"),
        //   "Load More"
        // ),
        // button(
        //   style := "width: 280px",
        //   HtmxAttributes.delete("/contacts"),
        //   HtmxAttributes.confirm("Are you sure you want to delete these contacts?"),
        //   HtmxAttributes.target("body"),
        //   "Delete Selected Contacts"
        // )
      )
    ),
    p(
      a(href := "/contacts/new", "Add Contact"),
      span(s" (${page} page)")
    )
  )
}
